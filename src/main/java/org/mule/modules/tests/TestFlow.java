package org.mule.modules.tests;

import org.junit.runners.model.InitializationError;
import org.mule.MessageExchangePattern;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.construct.Flow;
import org.mule.tck.MuleTestUtils;
import org.springframework.context.ApplicationContext;

public class TestFlow {

    private final Flow flow;
    private TestData testData;
    private ApplicationContext context;
    private MuleContext muleContext;

    TestFlow(MuleContext muleContext, ApplicationContext context, String flowName, TestData testData) throws InitializationError {
        this.testData = testData;
        this.flow = (Flow) muleContext.getRegistry().lookupFlowConstruct(flowName);
        if (this.flow == null) {
            throw new InitializationError("Flow named " + flowName + " does not exist");
        }
        this.context = context;
        this.muleContext = muleContext;
    }

    private MuleEvent getTestEvent(Object payload) throws Exception {
        return MuleTestUtils.getTestEvent(payload, MessageExchangePattern.REQUEST_RESPONSE, this.muleContext);
    }

    private MuleEvent getMuleEvent(TestData testData) throws Exception {
        MuleEvent event = this.getTestEvent(testData.getPayload());
        for (String key : testData.getFlowVars().keySet()) {
            event.setFlowVariable(key, testData.getFlowVars().get(key));
        }
        return event;
    }

    /**
     * Executes this flow with the currently loaded TestRunMessage, which can be accessed and
     * modified through {@code getTestRunMessage()}.
     * @return A {@link TestFlowResult} object containing the result of running this flow.
     * @throws Exception
     */
    public TestFlowResult run() throws Exception {
        if (testData == null) {
            throw new InitializationError("Could not run flow " + this.flow.getName() + ", test data not initialized");
        }
        // TODO: What to do with exception here? Throw as-is or modify?
        MuleEvent response = this.flow.process(getMuleEvent(testData));
        return new TestFlowResult(this, this.testData, response);
    }

    /**
     * Convenience method to run this flow using the specified bean as the TestRunMessage.
     * <p>If the bean ID ends with "testData", the value of each key is set as a flow variable.
     * If there is {@code payloadContent} key, its value will be set as the payload.</p>
     * @param beanId The bean ID to set as the payload.
     * @return A {@link TestFlowResult} object containing the result of running this flow.
     * @throws Exception
     */
    public TestFlowResult runWithBeanAsPayload(String beanId) throws Exception {
        this.testData = TestData.fromBean(beanId, this.context, this.muleContext);
        return this.run();
    }

    /**
     * Convenience method to run this flow using the specified object as the payload of the
     * TestRunMessage. If a TestRunMessage was loaded, it is ignored here.
     * @param payload
     * @return
     * @throws Exception
     */
    public TestFlowResult runWithPayload(Object payload) throws Exception {
        this.testData = new TestData().setPayload(payload);
        return this.run();
    }

}