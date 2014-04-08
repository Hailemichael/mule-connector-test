/**
 * (c) 2003-2013 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */

package org.mule.modules.tests;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.apache.mina.core.RuntimeIoException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runners.model.InitializationError;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.construct.Flow;
import org.mule.tck.junit4.FunctionalTestCase;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * Connector Tests Framework Parent Class
 *
 * @author Mulesoft, Inc
 */
@SuppressWarnings("unchecked")
public class ConnectorTestCase extends FunctionalTestCase {

    private static final String AUTOMATION_CREDENTIALS_BEAN_NOT_FOUND = "automationCredentials bean not found. Check that ConnectorTestCaseSpringBeans.xml was imported.";

	private static final Logger LOGGER = Logger.getLogger(ConnectorTestCase.class);

    protected static final String DEFAULT_SPRING_CONFIG_FILE = "AutomationSpringBeans.xml";
    protected static List<String> SPRING_CONFIG_FILES = new LinkedList<String>();    
    
    
    private final static String EMPTY_CREDENTIALS_FILE = "Credentials file is empty";
    private final static String SPRINGBEANS_NOT_INITIALIZED = "Problem loading Spring beans file, couldn't create the context for ConnectorTestParent.";
    private final static String TEST_FLOWS_FILE_NOT_FOUND = "Test flows xml file was not found.";
    private final static String MALFORMED_TEST_FLOWS_FILE = "Test flows xml is not well formed.";
    private final static String TESTRUNMESSAGE_NOT_INITIALIZED = "TestRunMessage was not initialized for current test.";
    private final static String FLOW_NOT_FOUND = "Flow not found on flow file.";
    private final static String BEAN_NOT_FOUND = "Bean not found on Spring beans file.";
    
	private Map<String, Object> testData = null;
	private static ApplicationContext context;
	
	protected static Properties automationCredentials;
	private static List<String> testFlowsNames = new LinkedList<String>();
	
	protected static void loadTestFlows(String configResources) {
    	
    	DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
	
		DocumentBuilder documentBuilder;
		Document testFlowsDocument = null;
		NodeList flowsList;
		
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			InputStream dataStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(configResources);
			testFlowsDocument = documentBuilder.parse(dataStream);
		} catch (IOException e) {
			throw new RuntimeIoException(TEST_FLOWS_FILE_NOT_FOUND);
		} catch (ParserConfigurationException e) {
			throw new RuntimeIoException(MALFORMED_TEST_FLOWS_FILE);
		} catch (SAXException e) {
			throw new RuntimeException();
		}
		
		flowsList = testFlowsDocument.getElementsByTagName("flow");
		for(int flowIndex = 0; flowIndex < flowsList.getLength(); flowIndex++) {
			Node flow = flowsList.item(flowIndex);
			
			if (flow.getNodeType() == Node.ELEMENT_NODE) { 
				Element flowElement = (Element) flow;
				testFlowsNames.add(flowElement.getAttribute("name"));
			}
		}

	}
        	
	private static void initializeSpringApplicationContext() throws BeansException {
		SPRING_CONFIG_FILES.add(DEFAULT_SPRING_CONFIG_FILE);
		context = new ClassPathXmlApplicationContext(getConfigSpringFiles());
	}

	private static void loadAndVerifyAutomationCredentials() throws Exception {
		automationCredentials = (Properties) context.getBean("automationCredentials");
		if (automationCredentials.isEmpty()) {
			throw new Exception(EMPTY_CREDENTIALS_FILE);
		}
	
	}
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception { 
    	try {
    		initializeSpringApplicationContext();
    		try {
    			loadAndVerifyAutomationCredentials();
    		} catch (BeansException e) {
    			throw new Exception(AUTOMATION_CREDENTIALS_BEAN_NOT_FOUND);
    		}
    	} catch (BeansException e) {
    		throw new Exception(SPRINGBEANS_NOT_INITIALIZED); 
    	}  
    }
    

	@Before
    public final void clearTestData() {
    	testData = null;
    	
    }
	
    protected static String[] getConfigSpringFiles() {
    	return SPRING_CONFIG_FILES.toArray(new String[SPRING_CONFIG_FILES.size()]);
    }
    
    /**
     * Add a file to be loaded on the spring context. <p>
     * <b>Note:</b>This method must be called from a static initializer static \{...\}
     * @param fileLocation The location of the file containing the spring beans definitions
     */
    protected static void addConfigSpringFile(String fileLocation) {
    	SPRING_CONFIG_FILES.add(fileLocation);
    }
    
    /**
     * Replace the list of spring beans to be loaded into the context. <p>
     * <b>Note:</b>This method must be called from a static initializer static \{...\}
     * @param configFilesLocation The list with the files location containing the spring beans definitions
     */
    protected static void setConfigSpringFiles(List<String> configFilesLocation) {
    	SPRING_CONFIG_FILES = configFilesLocation;
    }
    
    @Override
    protected String getConfigResources() {
    	String configXmlFile = getConfigXmlFile();
    	loadTestFlows(configXmlFile);
        return configXmlFile;
    }

    protected String getConfigXmlFile() {
        return "automation-test-flows.xml";
    }
    
    private void verifyValidFlowName(String flowName) throws Exception {
    	if (!(testFlowsNames.contains(flowName))) {
    		throw (new Exception(FLOW_NOT_FOUND));
    	}	
	}
    
    private void verifyValidBeanId(String beanId) throws Exception {
    	if (!(context.containsBean(beanId))) {
    		throw (new Exception(BEAN_NOT_FOUND));
    	}	
	}
	
    private void preInvokationVerifications(String flowName, String ... beanId) throws Exception {
    	verifyTestRunMessageIsInitialized();
    	verifyValidFlowName(flowName);
    	if (beanId.length > 0) {
    		verifyValidBeanId(beanId[0]);
    	}	
    }
    
    protected void verifyTestRunMessageIsInitialized() throws Exception {
    	if (testData == null) {
    		throw new InitializationError(TESTRUNMESSAGE_NOT_INITIALIZED); 
    	}
    }

    /**
     * Generates the MuleEvent that's processed by the Flow based on the data on the TestRunMessage.
     * 
     * testData stored key/value pairs are set as flowVars in the MuleEvent with the exception 
     * of "payloadContent" whose content will be set as payload of the MuleMessage.
     * 
     * @return MuleEvent to be processed to the Flow.
     * @throws Exception
     */
    private MuleEvent generateMuleEvent() throws Exception {
    	MuleEvent event;
    	Object payload = null;

    	Boolean hasPayloadContent = testData.containsKey("payloadContent");
    	if (hasPayloadContent) {
    		payload = testData.get("payloadContent");
    		
    	}
    	
    	event = getTestEvent(payload);
		for(String key : testData.keySet()) {
			if (!key.equals("payloadContent")) {
				event.setFlowVariable(key, testData.get(key));
				
			}	

		}

		return event;
    	
    }
    
    /**
     * Generates the MuleEvent that's processed by the Flow either from a Map or a POJO from the Spring context.
     * This method is used by the runFlowAndGetPayload/Message methods that run without using the TestRunMessage.
     * 
     * In case the beanId is a "TestData" Map
     * 	Map is retrieved from the Spring context and its values transformed into MuleEvent flowVars .
     * Case beanId is POJO or "Non-TestData" Map
     * 	POJO or "Non-TestData" Map is retrieved from the Spring context and set as the MuleMessage payload.
     * 
     * @param beanId id of a Spring bean that is declared on the AutomationSpringBeans file.
     * @return MuleEvent to be passed to the flow invocation.
     * @throws Exception
     */
    private MuleEvent generateMuleEvent(String beanId) throws Exception {
    	MuleEvent event;
    	Object payload = null;
    	Map<String,Object> operationAttributesValues = new HashMap<String,Object>() ;
    	Boolean hasPayloadContent;
    	
    	Object bean = context.getBean(beanId);   	
    		
    	if ((bean instanceof Map) && (beanId.endsWith("TestData"))) {  
			operationAttributesValues = (HashMap<String,Object>) bean;
		
		} else {
			payload = bean;
    		
    	}
    	
		hasPayloadContent = operationAttributesValues.containsKey("payloadContent");
    	if (hasPayloadContent) {
    		payload = operationAttributesValues.get("payloadContent");
    		
    	}

    	event = getTestEvent(payload);
		for(String key : operationAttributesValues.keySet()) {
			if (!key.equals("payloadContent")) {
				event.setFlowVariable(key, operationAttributesValues.get(key));
				
			}	

		}

		return event;
    	
    }
    
    protected Flow lookupFlowConstruct(String name) {
		Flow flow = null;
    	try {
    		flow = (Flow) muleContext.getRegistry().lookupFlowConstruct(name);
    	} catch (Exception e) {
    		LOGGER.fatal(e.getMessage());
    	} 	
        return flow;
    }
    
    /**
     * Use initializeTestRunMessage instead
     * @param data
     */
    @Deprecated
    protected void loadTestRunMessage(String beanId) {
		Object bean = context.getBean(beanId);
		testData.clear();
		if (bean instanceof Map) {
			testData.putAll((Map<String, Object>) context.getBean(beanId));
		} else {
			testData.put(beanId, bean);
		}
    }
 
    /**
     * Use initializeTestRunMessage instead
     * @param data
     */
    @Deprecated
	public void loadTestRunMessage(Map<String,Object> data) {
		testData.clear();
		testData.putAll(data);
	}
	
    protected void initializeTestRunMessage() {
		testData = new HashMap<String, Object>();
    }
    
    /**
     * If beanId belongs to a POJO it is set as payloadContent.
     * What if Map needs to be set as payload, needs to be analysed.
     */
    protected void initializeTestRunMessage(String beanId) {
    	testData = new HashMap<String, Object>();
    	
    	Object bean = context.getBean(beanId);
		if ((bean instanceof Map) && (beanId.endsWith("TestData"))) {
			testData.putAll((Map<String, Object>) context.getBean(beanId));
		} else {
			testData.put("payloadContent", bean);
		}
    }

    /**
     * Use payloadContent as key if you want a Map to be stored in the payload
     */
	public void initializeTestRunMessage(String key, Object value) {
		testData = new HashMap<String, Object>();
		testData.put(key, value);
	}
    
	public void initializeTestRunMessage(Object data) {
		testData = new HashMap<String, Object>();
		if (data instanceof Map) {
			testData.putAll((Map<String, Object>) data);
		} else {
			testData.put("payloadContent", data);
		};
	}
   
    public <T> T getBeanFromContext(String beanId) throws BeansException {
		return (T) context.getBean(beanId);
	}
    
    public void upsertBeanFromContextOnTestRunMessage(String beanId) {
		Object bean = getBeanFromContext(beanId);
		if (bean instanceof Map) {
			testData.putAll((Map<String, Object>) context.getBean(beanId));
		} else {
			testData.put("payloadContent", bean);
		}
    }
    
    public void upsertBeanFromContextOnTestRunMessage(String key,String beanId) {
		Object bean = getBeanFromContext(beanId);
		testData.put(key, bean);
    }

	public void upsertOnTestRunMessage(String key, Object value) {
		testData.put(key, value);
	}
    
	public void upsertOnTestRunMessage(Map<String,Object> data) {
		testData.putAll(data);
	}
	
	public void upsertPayloadContentOnTestRunMessage(Object payloadContent) {
		upsertOnTestRunMessage("payloadContent", payloadContent);
	}
	
	public <T> T getTestRunMessagePayload() {
		Object payloadContent = null;	
		if (testData.containsKey("payloadContent")) {
			payloadContent = testData.get("payloadContent");
		} 
		
		return (T) payloadContent;
		
	}
	
	public <T> T getTestRunMessageValue(String key) {
		return (T) testData.get(key);
	}

	public void removeFromTestRunMessage(String key) {
		testData.remove(key);
	}

	public boolean keyContainedInTestRunMessage(Object key) {
		return testData.containsKey(key);
	}
	
	public Set<String> getTestRunMessageKeySet() {
		return testData.keySet();
	}   
	
	
    /**
     * TestRunMessage is already loaded with values for the operation
     * @param flowName
     * @return
     * @throws Exception
     */
    protected <T> T runFlowAndGetPayload(String flowName) throws Exception {
    	preInvokationVerifications(flowName);
        MuleEvent response = lookupFlowConstruct(flowName).process(generateMuleEvent());
        return (T) response.getMessage().getPayload();
    }
	
	
	/**
     * TestRunMessage is already loaded with values for the operation
     * @param flowName
     * @return
     * @throws Exception
     */
    protected MuleMessage runFlowAndGetMessage(String flowName) throws Exception {
    	preInvokationVerifications(flowName);
    	MuleEvent response = lookupFlowConstruct(flowName).process(generateMuleEvent());
    	return response.getMessage();
    }

	/**
     * @param flowName
     * @param invocationProperty
     * @return
     * @throws Exception
     */
    protected <T> T runFlowAndGetInvocationProperty(String flowName, String invocationProperty) throws Exception {
    	preInvokationVerifications(flowName);
        return (T) ((MuleMessage) runFlowAndGetMessage(flowName)).getInvocationProperty(invocationProperty);
    }

    /**
     * Returns the MuleMessage containing the payload of the operation.
     * TestRunMessage is not the source of data for this method.
     * Meant for retrieving auxiliary information or when test involves a single call to the operation under test. 
     * 
     * @param flowName
     * @param beanId is the id of a map Spring bean declared in the AutomationSpringBeans
     * @return
     * @throws Exception
     */
    protected MuleMessage runFlowAndGetMessage(String flowName, String beanId) throws Exception {
    	preInvokationVerifications(flowName, beanId);
    	MuleEvent response = lookupFlowConstruct(flowName).process(generateMuleEvent(beanId));
        return response.getMessage();
    }
    
    
    /**
     * Returns the Payload of the flowName operation.
     * TestRunMessage is not the source of data for this method.
     * Meant for retrieving auxiliary information or when test involves a single call to the operation under test. 
     * 
     * @param flowName
     * @param beanId is the id of a map Spring bean declared in the AutomationSpringBeans
     * @return
     * @throws Exception
     */
    protected <T> T runFlowAndGetPayload(String flowName, String beanId) throws Exception {
    	preInvokationVerifications(flowName, beanId);
    	MuleEvent response = lookupFlowConstruct(flowName).process(generateMuleEvent(beanId));
        return (T) response.getMessage().getPayload();
    }
    
    protected void runFlowIgnoringPayload(String flowName) throws Exception {
		runFlowAndGetPayload(flowName);
	}

}
