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
public class AutomationTestFlowsFileTest extends ConnectorTestCaseTestParent {

    @Test
    public void testOverrideXmlConfig() throws Exception {
        assertEquals(getFlow("test").run().getPayload(), "Override");
    }

    @Override
    protected String getConfigXmlFile() {
        return "automation-test-flows-override.xml";
    }
}
