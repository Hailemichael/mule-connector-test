/**
 * (c) 2003-2013 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */

package org.mule.modules.tests;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author Mulesoft, Inc.
 */
public class TestParentOverrideFileTest extends ConnectorTestCase {

    @Test
    public void testOverrideXmlConfig() throws Exception {
        assertEquals(runFlowAndGetPayload("test"), "Override");
    }

    @Override
    protected String getConfigXmlFile() {
        return "automation-test-flows-override.xml";
    }
}
