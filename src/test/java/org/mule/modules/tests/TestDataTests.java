/**
 * (c) 2003-2013 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */

package org.mule.modules.tests;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TestDataTests extends AutomationTestCaseTestParent {

    private Map<String, Object> map = new HashMap<String, Object>();

    @Test(expected = UnsupportedOperationException.class)
    public void flowVarsAreImmutable() {
        map.put("foo", "unmodified");
        TestData data = TestData.fromMap(map);
        map.put("foo", "modified");
        assertEquals("unmodified", data.getFlowVar("foo"));
        data.getFlowVars().put("foo", "modified");
    }

    @Test
    public void createDataFromMap() {
        map.put("foo", "bar");
        map.put("payloadContent", 100);
        TestData data = TestData.fromMap(map);
        assertEquals(100, data.getPayload());
        assertEquals("bar", data.getFlowVar("foo"));
        assertEquals(1, data.getFlowVars().size());
    }

    @Test
    public void addFlowVar() {
        TestData data = new TestData().withFlowVar("foo", 42);
        assertEquals(42, data.getFlowVar("foo"));
    }

    @Test
    public void setPayload() {
        TestData data = new TestData().withPayload(1000);
        assertEquals(1000, data.getPayload());
    }

}
