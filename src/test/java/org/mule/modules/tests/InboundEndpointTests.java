/**
 * (c) 2003-2013 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */

package org.mule.modules.tests;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InboundEndpointTests extends AutomationTestCaseTestParent {

    @Test
    public void testInboundEndpoint() {
        TestData data = new TestData().withPayload(42);
        TestFlowResult result = getFlow("inbound-send").runAndWaitOnVM(data, "collect", 20000);
        assertEquals(84, result.getPayload());
    }
}
