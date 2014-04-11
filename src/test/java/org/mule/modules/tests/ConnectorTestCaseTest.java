/**
 * (c) 2003-2013 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */

package org.mule.modules.tests;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.model.InitializationError;
import org.mule.api.MuleMessage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
    	pojoValue = testContext.getBean("aPOJO").toString();
    }

    @Test
    public void testRunFlowAndGetMessageFromTestRunMessage() throws Exception {
        TestData data = new TestData(aTestDataMap, aRegularMap);
        MuleMessage message = getFlow("test-maps-as-beans").run(data).getMessage();
        assertEquals(String.format("%s|%s", aTestDataMap.get("aTestDataMapKey").toString(), aRegularMap.get("aRegularMapKey").toString()), message.getPayload().toString());
    }

    @Test
    public void testRunFlowAndGetMessagePassingTestDataMapBeanId() throws Exception {
    	TestFlowResult result = getFlow("test-maps-as-beans").runWithBean("aMapTestData");
        assertEquals(String.format("%s|%s", aTestDataMap.get("aTestDataMapKey").toString(), aRegularMap.get("aRegularMapKey").toString()), result.getMessage().getPayload().toString());
        assertFalse(result.getTestData().getFlowVars().keySet().containsAll(aTestDataMap.keySet()));
    }

    @Test
    public void testRunFlowAndGetMessagePassingPOJOBeanId() throws Exception {
    	MuleMessage message = getFlow("test-pojo-as-bean").runWithBean("aPOJO").getMessage();
        assertEquals(pojoValue, message.getPayload().toString());
    }

    @Test
    public void testRunFlowAndGetPayloadFromTestRunMessage() throws Exception {
        TestData data = new TestData(aTestDataMap, aRegularMap);
        String payload = getFlow("test-maps-as-beans").run(data).getPayload();
        assertEquals(String.format("%s|%s", aTestDataMap.get("aTestDataMapKey").toString(), aRegularMap.get("aRegularMapKey").toString()), payload);
    }

    @Test
    public void testRunFlowAndGetPayloadPassingTestDataMapBeanId() throws Exception {
    	TestFlowResult result = getFlow("test-maps-as-beans").runWithBean("aMapTestData");
        assertEquals(String.format("%s|%s", aTestDataMap.get("aTestDataMapKey").toString(), aRegularMap.get("aRegularMapKey").toString()), result.getPayload());
        assertFalse(result.getTestData().getFlowVars().keySet().containsAll(aTestDataMap.keySet()));
    }

    @Test
    public void testRunFlowAndGetPayloadPassingPOJOBeanId() throws Exception {
        Integer payload = getFlow("test-pojo-as-bean").runWithBean("aPOJO").getPayload();
        assertEquals(pojoValue, payload.toString());
    }

    /**
     * Test runFlow throws the actual cause and not a MessagingException
     * @throws Exception
     */
    @Test
    public void testGettingException() throws Exception {
        thrownException.expect(org.mule.api.transformer.TransformerMessagingException.class);
        getFlow("test-exception").run();
    }

    @Test
    public void testRunNonExistentFlow() throws Exception {
        thrownException.expect(InitializationError.class);
        getFlow("non-existent-flow").run();
    }

}
