/**
 * (c) 2003-2013 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */

package org.mule.modules.tests;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;

import org.junit.Test;

public class AutomationSpringBeansFileTest extends ConnectorTestCaseTestParent {
	
	static final private String MY_FILE_LOCATION = "myFileLocation.xml";
	
	@Test
	public void testAddConfigSpringFile() {
		
		addConfigSpringFile(MY_FILE_LOCATION);
		
		assertEquals(2, SPRING_CONFIG_FILES.size());
		assertEquals(DEFAULT_SPRING_CONFIG_FILE, SPRING_CONFIG_FILES.get(0));
		assertEquals(MY_FILE_LOCATION, SPRING_CONFIG_FILES.get(1));
		
		String[] configSpringFiles = ConnectorTestCase.getConfigSpringFiles();
		
		assertTrue(configSpringFiles.length > 0);
		assertEquals(DEFAULT_SPRING_CONFIG_FILE, configSpringFiles[0]);
		assertEquals(MY_FILE_LOCATION, configSpringFiles[1]);
	}
	
	@Test
	public void testSetConfigSpringFiles() {
		
		LinkedList<String> linkedList = new LinkedList<String>();
		linkedList.add(MY_FILE_LOCATION);
		
		setConfigSpringFiles(linkedList);
		
		assertEquals(1, SPRING_CONFIG_FILES.size());
		assertEquals(MY_FILE_LOCATION, SPRING_CONFIG_FILES.get(0));
		
		String[] configSpringFiles = ConnectorTestCase.getConfigSpringFiles();
		
		assertTrue(configSpringFiles.length > 0);
		assertEquals(MY_FILE_LOCATION, configSpringFiles[0]);
	}
	

	
}
