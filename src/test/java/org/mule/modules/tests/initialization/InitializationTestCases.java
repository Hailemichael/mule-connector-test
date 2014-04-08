/**
 * (c) 2003-2013 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */

package org.mule.modules.tests.initialization;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.Test;
import org.mule.modules.tests.ConnectorTestCase;

public class InitializationTestCases extends ConnectorTestCase {

	private static final String PLACEHOLDER_VALUES_PREFIX = "placeholder.";
	private static final String DEFAULT_LOCAL_FILE_VALUES_PREFIX = "local.";
	
    @Test
    public void testAutomationCredentialsValuesLocationByPlaceholder() throws Exception {
    	org.junit.Assume.assumeNotNull(System.getProperty("AUTOMATION_CREDENTIALS"));
    	assertTrue(automationCredentials.getProperty("connector.key").startsWith(PLACEHOLDER_VALUES_PREFIX));

    } 
    
    @Test
    public void testAutomationCredentialsValuesDefaultLocalFile() throws Exception {
    	assertTrue(automationCredentials.getProperty("connector.key").startsWith(DEFAULT_LOCAL_FILE_VALUES_PREFIX));
    } 
    
    
}
