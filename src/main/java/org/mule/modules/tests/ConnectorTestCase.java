/**
 * (c) 2003-2013 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */

package org.mule.modules.tests;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.runners.model.InitializationError;
import org.mule.api.MuleMessage;
import org.mule.tck.junit4.FunctionalTestCase;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.*;


/**
 * Connector Tests Framework Parent Class
 *
 * @author Mulesoft, Inc
 */
@SuppressWarnings("unchecked")
public class ConnectorTestCase extends FunctionalTestCase {

    private static final Logger LOGGER = Logger.getLogger(ConnectorTestCase.class);

    protected static final String DEFAULT_SPRING_CONFIG_FILE = "AutomationSpringBeans.xml";
    protected static List<String> SPRING_CONFIG_FILES = new LinkedList<String>();


    private final static String EMPTY_CREDENTIALS_FILE = "Credentials file is empty";
    private final static String CREDENTIALS_VALUE_MISSING = "Credentials key is missing its value";
    private final static String SPRINGBEANS_NOT_INITIALIZED = "Problem loading Spring beans file, couldn't create the context for ConnectorTestParent.";
    private final static String TESTRUNMESSAGE_NOT_INITIALIZED = "TestRunMessage was not initialized for current test.";

	private TestData testData = new TestData();
	private static ApplicationContext context;

	protected static Properties automationCredentials;

	private static void terminateTestRun(String message) {
		LOGGER.fatal(message);
		System.exit(1);

	}

    @BeforeClass
    public static void beforeClass() {
    	initializeSpringApplicationContext();
    	loadAndVerifyAutomationCredentials();
    }

	private static void initializeSpringApplicationContext() {
		SPRING_CONFIG_FILES.add(DEFAULT_SPRING_CONFIG_FILE);
    	try {
    		context = new ClassPathXmlApplicationContext(getConfigSpringFiles());
        } catch (BeansException e) {
        	terminateTestRun(SPRINGBEANS_NOT_INITIALIZED);
        }
	}

	private static void loadAndVerifyAutomationCredentials() {
		try {
			automationCredentials = (Properties) context.getBean("automationCredentials");
		} catch (BeansException e) {
			terminateTestRun(e.getMessage());
		}
		if (!automationCredentials.isEmpty()) {
			for (String name : automationCredentials.stringPropertyNames()) {
				if ((automationCredentials.getProperty(name)).isEmpty()) {
					terminateTestRun(CREDENTIALS_VALUE_MISSING);
				}
			}
		} else {
			terminateTestRun(EMPTY_CREDENTIALS_FILE);
		}
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
        return getConfigXmlFile();
    }

    protected String getConfigXmlFile() {
        return "automation-test-flows.xml";
    }

    /**
     * Use initializeTestRunMessage instead
     * @param data
     */
    @Deprecated
    protected void loadTestRunMessage(String beanId) {
		Object bean = context.getBean(beanId);
		if (bean instanceof Map) {
			testData.addFlowVars((Map<String, Object>) context.getBean(beanId));
		} else {
			testData.setFlowVar(beanId, bean);
		}
    }

    /**
     * Use initializeTestRunMessage instead
     * @param data
     */
    @Deprecated
	public void loadTestRunMessage(Map<String,Object> data) {
		this.testData.setFlowVars(data);
	}

    public <T> T getBeanFromContext(String beanId) throws BeansException {
		return (T) context.getBean(beanId);
	}

    @Deprecated
    public void upsertBeanFromContextOnTestRunMessage(String beanId) {
		Object bean = getBeanFromContext(beanId);
		if (bean instanceof Map) {
			this.testData.getFlowVars().putAll((Map<String, Object>) context.getBean(beanId));
		} else {
			this.testData.setPayload(bean);
		}
    }

    @Deprecated
    public void upsertBeanFromContextOnTestRunMessage(String key,String beanId) {
		Object bean = getBeanFromContext(beanId);
		this.testData.getFlowVars().put(key, bean);
    }

    @Deprecated
	public void upsertOnTestRunMessage(String key, Object value) {
        testData.setFlowVar(key, value);
	}

    @Deprecated
	public void upsertOnTestRunMessage(Map<String,Object> data) {
		testData.setFlowVars(data);
	}

    @Deprecated
	public void upsertPayloadContentOnTestRunMessage(Object payloadContent) {
		this.testData.setPayload(payloadContent);
	}

    @Deprecated
	public <T> T getTestRunMessagePayload() {
		return this.testData.getPayload();
	}

    @Deprecated
	public <T> T getTestRunMessageValue(String key) {
		return (T) testData.getFlowVarContent(key);
	}

    @Deprecated
	public void removeFromTestRunMessage(String key) {
		testData.getFlowVars().remove(key);
	}

    @Deprecated
	public boolean keyContainedInTestRunMessage(Object key) {
		return testData.getFlowVars().containsKey(key);
	}

    @Deprecated
	public Set<String> getTestRunMessageKeySet() {
		return testData.getFlowVars().keySet();
	}


    /**
     * TestRunMessage is already loaded with values for the operation
     * @param flowName
     * @return
     * @throws Exception
     */
    @Deprecated
    protected <T> T runFlowAndGetPayload(String flowName) throws Exception {
        return getFlow(flowName).run().getPayload();
    }

    protected TestFlow getFlow(String flowName) throws InitializationError {
        return new TestFlow(muleContext, context, flowName, testData);
    }


	/**
     * TestRunMessage is already loaded with values for the operation
     * @param flowName
     * @return
     * @throws Exception
     */
    @Deprecated
    protected MuleMessage runFlowAndGetMessage(String flowName) throws Exception {
    	return getFlow(flowName).run().getMessage();
    }

	/**
     * @param flowName
     * @param invocationProperty
     * @return
     * @throws Exception
     */
    @Deprecated
    protected <T> T runFlowAndGetInvocationProperty(String flowName, String invocationProperty) throws Exception {
        return getFlow(flowName).run().getMessage().getInvocationProperty(invocationProperty);
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
    @Deprecated
    protected MuleMessage runFlowAndGetMessage(String flowName, String beanId) throws Exception {
        return getFlow(flowName).runWithBeanAsPayload(beanId).getMessage();
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
    @Deprecated
    protected <T> T runFlowAndGetPayload(String flowName, String beanId) throws Exception {
        return getFlow(flowName).runWithBeanAsPayload(beanId).getPayload();
    }

    @Deprecated
    protected void initializeTestRunMessage(String key, Object object) {
        this.testData.setFlowVar(key, object);
    }

    @Deprecated
    protected void initializeTestRunMessage(Map<String, Object> map) {
        this.testData = TestData.fromMap(map);
    }

    @Deprecated
    protected void initializeTestRunMessage(String beanId) {
        this.testData = TestData.fromBean(beanId, context);
    }

    @Deprecated
    protected void initializeTestRunMessage() {

    }

    public TestData getTestData() {
        return this.testData;
    }

    // TODO: Provide these methods here or not, through getTestData()?

    public void setPayload(Object payload) {
        this.testData.setPayload(payload);
    }

    public void setFlowVar(String flowVar, Object value) {
        this.testData.setFlowVar(flowVar, value);
    }

    public void addFlowVars(Map<String, Object> flowVars) {
        this.testData.addFlowVars(flowVars);
    }

    public void setFlowVars(Map<String, Object> flowVars) {
        this.testData.setFlowVars(flowVars);
    }

}
