/**
 * (c) 2003-2013 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */

package org.mule.modules.tests;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mule.api.MuleMessage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Mulesoft, Inc.
 */
public class ConnectorTestCaseTest extends ConnectorTestCase {

	private static ApplicationContext testContext = new ClassPathXmlApplicationContext(getConfigSpringFiles());
    
    private static Map<String, Object> aMap;
    private static Map<String, Object> anotherMap;
    
    
    @Rule
    public ExpectedException thrownException = ExpectedException.none();

	@Before
	public void setUp() {
		aMap = (HashMap<String, Object>) testContext.getBean("aMap");
    	anotherMap = (HashMap<String, Object>) testContext.getBean("anotherMap");
    }
      
    @Test
    public void testInitializeTestRunMessagePassingKeyValuePair() {
        initializeTestRunMessage("key", aMap.get("key"));
        assertEquals(aMap.get("key"), getTestRunMessageValue("key"));
    }
    
    @Test
    public void testInitializeTestRunMessagePassingMap()  {
    	initializeTestRunMessage(aMap);
    	assertEquals(aMap.get("key"), getTestRunMessageValue("key"));      
    }
    
    @Test
    public void testInitializeTestRunMessageFromBeanId()  {
        initializeTestRunMessage("aMap");
        assertEquals(aMap.get("key"), getTestRunMessageValue("key"));      
    }
    
    @Test
    public void testRunFlowAndGetMessageFromTestRunMessage() throws Exception {
    	initializeTestRunMessage("aMap");
        MuleMessage message = runFlowAndGetMessage("test-get-payload");
        assertEquals(String.format("%s|%s", aMap.get("aMapKey").toString(), anotherMap.get("anotherMapKey").toString()), message.getPayload().toString());   
    }
    
    @Test
    public void testRunFlowAndGetMessagePassingBeanId() throws Exception {
        MuleMessage message = runFlowAndGetMessage("test-get-payload", "aMap");
        assertEquals(String.format("%s|%s", aMap.get("aMapKey").toString(), anotherMap.get("anotherMapKey").toString()), message.getPayload().toString());
        assertFalse(getTestRunMessageKeySet().containsAll(aMap.keySet()));
    }
    
    @Test
    public void testRunFlowAndGetPayloadFromTestRunMessage() throws Exception {
    	initializeTestRunMessage("aMap");
        String payload = runFlowAndGetPayload("test-get-payload");
        assertEquals(String.format("%s|%s", aMap.get("aMapKey").toString(), anotherMap.get("anotherMapKey").toString()), payload);   
    }
    
    @Test
    public void testRunFlowAndGetPayloadPassingBeanId() throws Exception {
    	String payload = runFlowAndGetPayload("test-get-payload", "aMap");
        assertEquals(String.format("%s|%s", aMap.get("aMapKey").toString(), anotherMap.get("anotherMapKey").toString()), payload);
        assertFalse(getTestRunMessageKeySet().containsAll(aMap.keySet()));
    }  

    /**
     * Test runFlow throws the actual cause and not a MessagingException
     * @throws Exception
     */
    @Test
    public void testGettingException() throws Exception {
        thrownException.expect(org.mule.api.transformer.TransformerMessagingException.class);
        runFlowAndGetPayload("test-exception");
    }
}
