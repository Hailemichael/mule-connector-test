/**
 * (c) 2003-2013 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */

package org.mule.modules.tests;

import com.google.common.base.Throwables;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.common.util.concurrent.UncheckedTimeoutException;
import org.mule.MessageExchangePattern;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.mule.construct.Flow;
import org.mule.tck.MuleTestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.concurrent.*;

public class TestFlow {

    protected final Flow flow;
    protected final ApplicationContext context;
    protected final MuleContext muleContext;
    private static final Logger logger = LoggerFactory.getLogger(TestFlow.class);

    TestFlow(MuleContext muleContext, ApplicationContext context, String flowName) {
        this.context = context;
        this.muleContext = muleContext;
        this.flow = (Flow) muleContext.getRegistry().lookupFlowConstruct(flowName);
        if (this.flow == null) {
            throw new IllegalArgumentException("Flow named " + flowName + " does not exist");
        }
    }

    protected MuleEvent getTestEvent(Object payload) {
        try {
            return MuleTestUtils.getTestEvent(payload, MessageExchangePattern.REQUEST_RESPONSE, this.muleContext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected MuleEvent getMuleEvent(TestData testData) {
        MuleEvent event = this.getTestEvent(testData.getPayload());
        for (String key : testData.getFlowVars().keySet()) {
            event.setFlowVariable(key, testData.getFlowVars().get(key));
        }
        return event;
    }

    /**
     * Executes this flow with the specified {@link TestData}.
     * @return A {@link TestFlowResult} object containing the result of running this flow.
     */
    public TestFlowResult run(TestData testData) {
        // TODO: What to do with exception here? Throw as-is or modify?
        MuleEvent event;
        try {
            event = this.flow.process(getMuleEvent(testData));
        } catch (MuleException e) {
            throw new RuntimeException(e);
        }
        return new TestFlowResult(this, testData, event.getMessage());
    }

    /**
     * Executes this flow without passing any test data to it.
     * @return A {@link TestFlowResult} object containing the result of running this flow.
     */
    public TestFlowResult run() {
        return this.run(new TestData(null, null));
    }

    /**
     * Runs the given flow using a Spring bean as test data. If the bean is a map
     * and its ID ends with {@code TestData}, the test data will be constructed
     * following the same convention as {@link TestData#fromMap}. Otherwise, it
     * will be set as the payload as-is.
     * @param beanId The ID of the Spring bean
     * @return A {@link TestFlowResult} object containing the result of running this flow.
     * @throws org.springframework.beans.BeansException if the bean could not be found
     */
    @SuppressWarnings("unchecked")
    public TestFlowResult runWithBean(String beanId) {
        TestData testData;
        Object bean = context.getBean(beanId);
        if (bean instanceof Map && beanId.endsWith("TestData")) {
            testData = TestData.fromMap((Map<String, Object>) bean);
        } else {
            testData = new TestData().withPayload(bean);
        }
        return this.run(testData);
    }

    /**
     * <p>
     * Runs this flow and waits until a response is
     * received on the VM queue named {@code vmQueueName} or a timeout is
     * reached. This method is used to test inbound endpoints.
     * </p>
     * <p>
     * A {@code vm:outbound-endpoint} must <b>manually</b> be appended to the flow
     * under test before calling this method.
     * </p>
     * <p>
     * This method does not throw checked exceptions to avoid cluttering test code,
     * since usually tests should fail as soon as possible with no error handling.
     * Thrown exceptions are still documented to allow error handling if
     * necessary.
     * </p>
     *
     * @param data The test data to run this flow with.
     * @param vmQueueName The name of the queue to listen for messages on after running
     *                    the flow. If the queue's URL is {@code vm://foo}, then this
     *                    parameter should be {@code foo}.
     * @param timeoutMs   Time in milliseconds to wait for a message before throwing a
     *                    {@link TimeoutException}.
     * @return The data received at the specified VM queue.
     * @throws UncheckedTimeoutException if the request timed out
     * @throws UncheckedExecutionException if an error occurred while retrieving a message
     * @throws RuntimeException is the waiting thread was interrupted
     */
    public TestFlowResult runAndWaitOnVM(final TestData data, final String vmQueueName, long timeoutMs) {
        final String vmEndpointUrl = "vm://" + vmQueueName;
        final MuleClient client = muleContext.getClient();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<TestFlowResult> waitForResponse = new Callable<TestFlowResult>() {
            @Override
            public TestFlowResult call() throws MuleException {
                // -1 to wait indefinitely appears to be broken
                MuleMessage message = client.request(vmEndpointUrl, Long.MAX_VALUE);
                return new TestFlowResult(TestFlow.this, data, message);
            }
        };
        FutureTask<TestFlowResult> futureResponse = new FutureTask<TestFlowResult>(waitForResponse);
        // Start waiting for response before message is sent
        executor.submit(futureResponse);
        // Send the message
        this.run(data);
        TestFlowResult result = null;
        try {
            result = futureResponse.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            logger.info("Timed out while waiting for VM endpoint", e);
            throw new UncheckedTimeoutException(e);
        } catch (ExecutionException e) {
            logger.error("Error while processing VM endpoint message", e);
            throw new UncheckedExecutionException(e);
        } catch (InterruptedException e) {
            logger.warn("Interrupted while waiting for message on VM endpoint", e);
            Throwables.propagate(e);
        }
        return result;
    }

    /**
     * Runs this flow and waits until a response is
     * received on the VM queue named {@code vmQueueName} or a timeout is
     * reached. This method is used to test inbound endpoints.
     *
     * @see #runAndWaitOnVM(TestData, String, long) to use test data
     */
    public TestFlowResult runAndWaitOnVM(final String vmQueueName, long timeoutMs) {
        return this.runAndWaitOnVM(new TestData(), vmQueueName, timeoutMs);
    }
}