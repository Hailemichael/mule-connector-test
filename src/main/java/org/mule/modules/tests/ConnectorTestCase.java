/**
 * (c) 2003-2013 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */

package org.mule.modules.tests;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.construct.Flow;
import org.mule.tck.junit4.FunctionalTestCase;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Connector Tests Framework Parent Class
 *
 * @author Mulesoft, Inc
 */
@SuppressWarnings("unchecked")
public abstract class ConnectorTestCase extends FunctionalTestCase {

    private static final Logger LOGGER = Logger.getLogger(ConnectorTestCase.class);

    protected static final String DEFAULT_SPRING_CONFIG_FILE = "AutomationSpringBeans.xml";
    protected static List<String> SPRING_CONFIG_FILES = new LinkedList<String>();    

    static {
    	SPRING_CONFIG_FILES.add(DEFAULT_SPRING_CONFIG_FILE);
    }
    
	private Map<String, Object> testData = new HashMap<String, Object>();
	private static ApplicationContext context;

    @BeforeClass
    public static void beforeClass(){  	
    	try {
    		context = new ClassPathXmlApplicationContext(getConfigSpringFiles());
        } catch (Exception e) {
            LOGGER.warn("Spring beans file was not found. Couldn't create the context for ConnectorTestParent.");
        }
    }
    
    protected static String[] getConfigSpringFiles() {
    	return (String[]) SPRING_CONFIG_FILES.toArray();
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
        return getConfigXmlFile();
    }

    protected String getConfigXmlFile() {
        return "automation-test-flows.xml";
    }
  
    /**
     * Generates the MuleEvent to be consumed by the Flow either from the TestRunMessage
     * content or from a Map beanId.
     * 
     * payloadContent convention: Objects that can only be passed to the operation 
     * from the payload should be referenced by a payloadContent for this method to 
     * load them in the payload of the MuleMessage.
     * 
     * 
     * @param beanId id of a Map Spring bean that is declared on the AutomationSpringBeans file
     * @return MuleEvent to be passed to the flow invocation.
     * @throws Exception
     */
    private MuleEvent generateMuleEvent(String... beanId) throws Exception {
    	MuleEvent event;
    	Object payload = null;
    	
    	Map<String,Object> dataValues = new HashMap<String,Object>();
    	dataValues.putAll(testData);
    	if (!(beanId.length == 0)) {
    		dataValues = (HashMap<String,Object>) context.getBean(beanId[0]);
    		
    	} 
    	
    	Boolean payloadContent = dataValues.containsKey("payloadContent");
    	if (payloadContent) {
    		payload = dataValues.get("payloadContent");
    		
    	}

    	event = getTestEvent(payload);
		for(String key : dataValues.keySet()) {	
			event.setFlowVariable(key, dataValues.get(key));

		}
		
		if (payloadContent) {
			event.removeFlowVariable("payloadContent");
    		
    	}

		return event;
    	
    }
    
    protected Flow lookupFlowConstruct(String name) {
        return (Flow) muleContext.getRegistry().lookupFlowConstruct(name);
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
	
    protected void initializeTestRunMessage(String beanId) {
		Object bean = context.getBean(beanId);
		testData.clear();
		if (bean instanceof Map) {
			testData.putAll((Map<String, Object>) context.getBean(beanId));
		} else {
			testData.put(beanId, bean);
		}
    }
 
	public void initializeTestRunMessage(String key, Object value) {
		testData.clear();
		testData.put(key, value);
	}
    
	public void initializeTestRunMessage(Map<String,Object> data) {
		testData.clear();
		testData.putAll(data);
	}
   
    public <T> T getBeanFromContext(String beanId) throws BeansException {
		return (T) context.getBean(beanId);
	}
    
    public void upsertBeanFromContextOnTestRunMessage(String beanId) {
		Object bean = getBeanFromContext(beanId);
		testData.put(beanId, bean);
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
    	MuleEvent response = lookupFlowConstruct(flowName).process(generateMuleEvent(beanId));
        return (T) response.getMessage().getPayload();
    }

}
