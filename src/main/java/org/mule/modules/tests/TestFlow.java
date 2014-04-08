package org.mule.modules.tests;

import org.junit.runners.model.InitializationError;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.construct.Flow;
import org.springframework.context.ApplicationContext;

public class TestFlow {

    private final Flow flow;
    private TestData testData;
    private ApplicationContext context;

    TestFlow(MuleContext muleContext, ApplicationContext context, String flowName, TestData testData) throws InitializationError {
        this.testData = testData;
        this.flow = (Flow) muleContext.getRegistry().lookupFlowConstruct(flowName);
        if (this.flow == null) {
            throw new InitializationError("Flow named " + flowName + " does not exist");
        }
        this.context = context;
    }

    public TestFlowResult run() throws Exception {
        if (testData == null) {
            throw new InitializationError("Could not run flow " + this.flow.getName() + ", test data not initialized");
        }
        // TODO: What to do with exception here? Throw as-is or modify?
        MuleEvent response = this.flow.process(testData.getMuleEvent());
        return new TestFlowResult(this, this.testData, response);
    }

    public TestFlowResult runWithBeanAsPayload(String beanId) throws Exception {
        this.testData = TestData.fromBean(beanId, this.context);
        return this.run();
    }

    public TestFlowResult runWithPayload(Object payload) throws Exception {
        this.testData = new TestData().setPayload(payload);
        return this.run();
    }

    public String getName() {
        return this.flow.getName();
    }

}