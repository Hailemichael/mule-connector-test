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

import static junit.framework.Assert.assertEquals;

/**
 * @author Mulesoft, Inc.
 */
public class TestParentTest extends TestParent {

    private static final String TEST_KEY = "key";
    private static final String TEST_VALUE = "testing";

    @Test
    public void testNotModifiedPayload() throws Exception {
        addToMessageTestObject(TEST_KEY, TEST_VALUE);
        Map<String, Object> payload = runFlowAndGetPayload("test-without-modifying-payload");
        assertEquals(payload.get(TEST_KEY), TEST_VALUE);
    }

    @Test
    public void testModifiedPayload() throws Exception {
        addToMessageTestObject(TEST_KEY, "Modified");

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("key2", "testing");
        addToMessageTestObject(map);

        String payload = runFlowAndGetPayload("test-get-payload");
        assertEquals(payload, "Modified testing");
    }

    @Test
    public void testGetValueFromMessageTestObject() {
        addToMessageTestObject(TEST_KEY, TEST_VALUE);
        assertEquals(getValueFromMessageTestObject(TEST_KEY), TEST_VALUE);
    }
}
