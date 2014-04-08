/**
 * (c) 2003-2013 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */

package org.mule.modules.tests;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;
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
public class ConnectorTestCaseTest extends ConnectorTestCaseTestParent {

	private static ApplicationContext testContext = new ClassPathXmlApplicationContext(getConfigSpringFiles());
    
    private static Map<String, Object> aTestDataMap;
    private static Map<String, Object> aRegularMap;
    private static String pojoValue;
    
    
    @Rule
    public ExpectedException thrownException = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		aTestDataMap = (HashMap<String, Object>) testContext.getBean("aMapTestData");
    	aRegularMap = (HashMap<String, Object>) testContext.getBean("aRegularMap");
    	pojoValue = ((Integer) testContext.getBean("aPOJO")).toString();
    }
      
    @Test
    public void testInitializeTestRunMessagePassingKeyValuePair() {
        initializeTestRunMessage("key", aTestDataMap.get("aTestDataTestDataMapKey"));
        assertEquals(aTestDataMap.get("aTestDataTestDataMapKey"), getTestRunMessageValue("key"));
    }
    
    @Test
    public void testInitializeTestRunMessagePassingMap()  {
    	initializeTestRunMessage(aTestDataMap);
    	assertEquals(aTestDataMap.get("aTestDataTestDataMapKey"), getTestRunMessageValue("aTestDataTestDataMapKey"));      
    }
    
    @Test
    public void testInitializeTestRunMessageFromTestDataMapBeanId()  {
        initializeTestRunMessage("aMapTestData");
        assertEquals(aTestDataMap.get("aTestDataTestDataMapKey"), getTestRunMessageValue("aTestDataTestDataMapKey"));      
    }
    
    @Test
    public void testGetTestRunMessagePayload() {
    	initializeTestRunMessage("aRegularMap");
        assertEquals(getTestRunMessageValue("payloadContent"), getTestRunMessagePayload());  
    }
    
    @Test
    public void testInitializeTestRunMessageFromRegularMapBeanId()  {
        initializeTestRunMessage("aRegularMap");
        assertEquals(aRegularMap, getTestRunMessagePayload());      
    }
    
    @Test
    public void testInitializeTestRunMessageFromPOJOBeanId()  {
        initializeTestRunMessage("aPOJO");      
        assertEquals(pojoValue, getTestRunMessagePayload().toString());
    }
    
    @Test
    public void testRunFlowAndGetMessageConsumingTestRunMessage() throws Exception {
    	initializeTestRunMessage("aMapTestData");
        MuleMessage message = runFlowAndGetMessage("test-maps-as-beans");
        assertEquals(String.format("%s|%s", aTestDataMap.get("aTestDataMapKey").toString(), aRegularMap.get("aRegularMapKey").toString()), message.getPayload().toString());   
    }
    
    @Test
    public void testRunFlowAndGetMessagePassingTestDataMapBeanId() throws Exception {
    	initializeTestRunMessage();
    	MuleMessage message = runFlowAndGetMessage("test-maps-as-beans", "aMapTestData");
        assertEquals(String.format("%s|%s", aTestDataMap.get("aTestDataMapKey").toString(), aRegularMap.get("aRegularMapKey").toString()), message.getPayload().toString());
        assertFalse(getTestRunMessageKeySet().containsAll(aTestDataMap.keySet()));
    }
    
    @Test
    public void testRunFlowAndGetMessagePassingPOJOBeanId() throws Exception {
    	initializeTestRunMessage();
    	MuleMessage message = runFlowAndGetMessage("test-pojo-as-bean", "aPOJO");
        assertEquals(pojoValue, message.getPayload().toString());
    }
    
    @Test
    public void testRunFlowAndGetPayloadConsumingTestRunMessage() throws Exception {
    	initializeTestRunMessage("aMapTestData");
        String payload = runFlowAndGetPayload("test-maps-as-beans");
        assertEquals(String.format("%s|%s", aTestDataMap.get("aTestDataMapKey").toString(), aRegularMap.get("aRegularMapKey").toString()), payload);   
    }
    
    @Test
    public void testRunFlowAndGetPayloadPassingTestDataMapBeanId() throws Exception {
    	initializeTestRunMessage();
    	String payload = runFlowAndGetPayload("test-maps-as-beans", "aMapTestData");
        assertEquals(String.format("%s|%s", aTestDataMap.get("aTestDataMapKey").toString(), aRegularMap.get("aRegularMapKey").toString()), payload);
        assertFalse(getTestRunMessageKeySet().containsAll(aTestDataMap.keySet()));
    }  
    
    @Test
    public void testRunFlowAndGetPayloadPassingPOJOBeanId() throws Exception {
    	initializeTestRunMessage();
        Integer payload = runFlowAndGetPayload("test-pojo-as-bean", "aPOJO");
        assertEquals(pojoValue, payload.toString());
    }

    /**
     * Test runFlow throws the actual cause and not a MessagingException
     * @throws Exception
     */
    @Test
    public void testGettingException() throws Exception {
    	initializeTestRunMessage();
        thrownException.expect(org.mule.api.transformer.TransformerMessagingException.class);
        runFlowAndGetPayload("test-exception");
    }
    
    @Test
    public void testRunFlowAndGetMessagePassingBeanIdInitializationException() throws Exception {
    	thrownException.expect(org.junit.runners.model.InitializationError.class);
        runFlowAndGetMessage("test-maps-as-beans", "aMapTestData");
    }
    
    @Test
    public void testRunFlowAndGetPayloadPassingBeanIdInitializationException() throws Exception {
        thrownException.expect(org.junit.runners.model.InitializationError.class);
        runFlowAndGetPayload("test-maps-as-beans", "aMapTestData");
    }
    
    @Test
    public void testRunFlowAndGetMessageInitializationException() throws Exception {
    	thrownException.expect(org.junit.runners.model.InitializationError.class);
        runFlowAndGetMessage("test-maps-as-beans");
    }
    
    @Test
    public void testRunFlowAndGetPayloadInitializationException() throws Exception {
        thrownException.expect(org.junit.runners.model.InitializationError.class);
        runFlowAndGetPayload("test-maps-as-beans");
    }
    
}
