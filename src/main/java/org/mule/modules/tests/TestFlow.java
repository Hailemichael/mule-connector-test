/**
 * (c) 2003-2013 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */

package org.mule.modules.tests;

import org.junit.runners.model.InitializationError;
import org.mule.MessageExchangePattern;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.construct.Flow;
import org.mule.tck.MuleTestUtils;
import org.springframework.context.ApplicationContext;

import java.util.Map;

public class TestFlow {

    protected final Flow flow;
    protected final ApplicationContext context;
    protected final MuleContext muleContext;

    TestFlow(MuleContext muleContext, ApplicationContext context, String flowName) throws InitializationError {
        this.context = context;
        this.muleContext = muleContext;
        this.flow = (Flow) muleContext.getRegistry().lookupFlowConstruct(flowName);
        if (this.flow == null) {
            throw new InitializationError("Flow named " + flowName + " does not exist");
        }
    }

    protected MuleEvent getTestEvent(Object payload) throws Exception {
        return MuleTestUtils.getTestEvent(payload, MessageExchangePattern.REQUEST_RESPONSE, this.muleContext);
    }

    protected MuleEvent getMuleEvent(TestData testData) throws Exception {
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
    public TestFlowResult run(TestData testData) throws Exception {
        // TODO: What to do with exception here? Throw as-is or modify?
        MuleEvent response = this.flow.process(getMuleEvent(testData));
        return new TestFlowResult(this, testData, response);
    }

    /**
     * Executes this flow without passing any test data to it.
     * @return A {@link TestFlowResult} object containing the result of running this flow.
     */
    public TestFlowResult run() throws Exception {
        return this.run(new TestData(null, null));
    }

    /**
     * Runs the given flow using a Spring bean as test data. If the bean is a map
     * and its ID ends with {@code TestData}, the test data will be constructed
     * following the same convention as {@link TestData#fromMap}. Otherwise, it
     * will be set as the payload as-is.
     * @param beanId The ID of the Spring bean
     * @return A {@link TestFlowResult} object containing the result of running this flow.
     */
    @SuppressWarnings("unchecked")
    public TestFlowResult runWithBean(String beanId) throws Exception {
        TestData testData;
        Object bean = context.getBean(beanId);
        if (bean instanceof Map && beanId.endsWith("TestData")) {
            testData = new TestData().fromMap((Map<String, Object>) bean);
        } else {
            testData = new TestData().withPayload(bean);
        }
        return this.run(testData);
    }

}