package org.mule.modules.tests;

import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;

public class TestFlowResult {

    private MuleEvent response;
    private TestFlow testFlow;

    TestFlowResult(TestFlow testFlow, TestData testData, MuleEvent response) {
        this.testFlow = testFlow;
        this.response = response;
    }

    public MuleMessage getMessage() {
        return this.response.getMessage();
    }

    public <T> T getPayload() {
        return (T) this.getMessage().getPayload();
    }

    public TestFlow getFlow() {
        return this.testFlow;
    }
}
