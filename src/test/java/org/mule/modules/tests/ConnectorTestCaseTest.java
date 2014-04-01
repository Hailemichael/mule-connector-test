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
import java.util.UUID;

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
public class ConnectorTestCaseTest extends ConnectorTestCaseTestParent {

	private static ApplicationContext testContext = new ClassPathXmlApplicationContext(getConfigSpringFiles());
    
    private static Map<String, Object> aMap;
    private static Map<String, Object> anotherMap;
    private static String pojoValue;
    
    @Rule
    public ExpectedException thrownException = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		aMap = (HashMap<String, Object>) testContext.getBean("aMap");
    	anotherMap = (HashMap<String, Object>) testContext.getBean("anotherMap");
    	pojoValue = ((Integer) testContext.getBean("aPOJO")).toString();
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
    public void testInitializeTestRunMessageFromMapBeanId()  {
        initializeTestRunMessage("aMap");
        assertEquals(aMap.get("key"), getTestRunMessageValue("key"));      
    }
    
    @Test
    public void testInitializeTestRunMessageFromPOJOBeanId()  {
        initializeTestRunMessage("aPOJO");      
        assertEquals(pojoValue, getTestRunMessageValue("payloadContent").toString());
    }
    
    @Test
    public void testRunFlowAndGetMessageFromTestRunMessage() throws Exception {
    	initializeTestRunMessage("aMap");
        MuleMessage message = runFlowAndGetMessage("test-maps-as-beans");
        assertEquals(String.format("%s|%s", aMap.get("aMapKey").toString(), anotherMap.get("anotherMapKey").toString()), message.getPayload().toString());   
    }
    
    @Test
    public void testRunFlowAndGetMessagePassingMapBeanId() throws Exception {
        MuleMessage message = runFlowAndGetMessage("test-maps-as-beans", "aMap");
        assertEquals(String.format("%s|%s", aMap.get("aMapKey").toString(), anotherMap.get("anotherMapKey").toString()), message.getPayload().toString());
        assertFalse(getTestRunMessageKeySet().containsAll(aMap.keySet()));
    }
    
    @Test
    public void testRunFlowAndGetMessagePassingPOJOBeanId() throws Exception {
        MuleMessage message = runFlowAndGetMessage("test-pojo-as-bean", "aPOJO");
        assertEquals(pojoValue, message.getPayload().toString());
    }
    
    @Test
    public void testRunFlowAndGetPayloadFromTestRunMessage() throws Exception {
    	initializeTestRunMessage("aMap");
        String payload = runFlowAndGetPayload("test-maps-as-beans");
        assertEquals(String.format("%s|%s", aMap.get("aMapKey").toString(), anotherMap.get("anotherMapKey").toString()), payload);   
    }
    
    @Test
    public void testRunFlowAndGetPayloadPassingMapBeanId() throws Exception {
    	String payload = runFlowAndGetPayload("test-maps-as-beans", "aMap");
        assertEquals(String.format("%s|%s", aMap.get("aMapKey").toString(), anotherMap.get("anotherMapKey").toString()), payload);
        assertFalse(getTestRunMessageKeySet().containsAll(aMap.keySet()));
    }  
    
    @Test
    public void testRunFlowAndGetPayloadPassingPOJOBeanId() throws Exception {
        Integer payload = runFlowAndGetPayload("test-pojo-as-bean", "aPOJO");
        assertEquals(pojoValue, payload.toString());
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

    @Test
    public void testInboundEndpointFlow() throws Exception {
        String message = UUID.randomUUID().toString();
        upsertOnTestRunMessage("payload", message);
        assertEquals(message, runFlowAndWaitForResponseVM("sendFlow", "collectVm", 1000));
    }
}
