/**
 * (c) 2003-2013 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */

package org.mule.modules.tests;

import org.mule.api.MuleMessage;

public class TestFlowResult {

    private MuleMessage message;
    private TestFlow testFlow;
    private TestData testData;

    TestFlowResult(TestFlow testFlow, TestData testData, MuleMessage message) {
        this.testFlow = testFlow;
        this.message = message;
        this.testData = testData;
    }

    public MuleMessage getMessage() {
        return this.message;
    }

    @SuppressWarnings("unchecked")
    public <T> T getPayload() {
        return (T) this.getMessage().getPayload();
    }

    public TestFlow getFlow() {
        return this.testFlow;
    }

    public TestData getTestData() {
        return this.testData;
    }
}
