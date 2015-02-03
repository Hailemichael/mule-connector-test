/**
 * (c) 2003-2013 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */

package org.mule.modules.tests;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.mule.modules.tests.initialization.InitializationTestCases;

public class FrameworkInitializationTest {

	private static final String AUTOMATION_CREDENTIALS_LOCATION = "classpath:automation-credentials-location-by-placeholder.properties";
	
	@Test
	public void testLoadAndVerifyAutomationCredentialsDefaultLocalFile() {
		Request singleTestRequest = Request.method(InitializationTestCases.class, "testAutomationCredentialsValuesDefaultLocalFile");
		Result result = (new JUnitCore()).run(singleTestRequest);
		assertTrue(result.wasSuccessful());
		
	}

	@Test
	public void testLoadAndVerifyAutomationCredentialsLocationByPlaceholder() {
		System.setProperty("AUTOMATION_CREDENTIALS", AUTOMATION_CREDENTIALS_LOCATION);
		Request singleTestRequest = Request.method(InitializationTestCases.class, "testAutomationCredentialsValuesLocationByPlaceholder");
		Result result = (new JUnitCore()).run(singleTestRequest);
		assertTrue(result.wasSuccessful());
		
	}

	

}
