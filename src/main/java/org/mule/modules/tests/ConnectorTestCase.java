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
import org.mule.api.MuleContext;
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
public class ConnectorTestCase {

    private static final Logger LOGGER = Logger.getLogger(ConnectorTestCase.class);

    protected static final String DEFAULT_SPRING_CONFIG_FILE = "AutomationSpringBeans.xml";
    protected static List<String> SPRING_CONFIG_FILES = new LinkedList<String>();

    private final static String EMPTY_CREDENTIALS_FILE = "Credentials file is empty";
    private final static String CREDENTIALS_VALUE_MISSING = "Credentials key is missing its value";
    private final static String SPRINGBEANS_NOT_INITIALIZED = "Problem loading Spring beans file, couldn't create the context for ConnectorTestParent.";
    private final static String TESTRUNMESSAGE_NOT_INITIALIZED = "TestRunMessage was not initialized for current test.";

    private BaseConnectorTestCase baseConnectorTestCase = new BaseConnectorTestCase(getConfigXmlFile());
    private MuleContext muleContext = baseConnectorTestCase.getMuleContext();
	private static ApplicationContext context;

    protected static Properties automationCredentials;

	private static void terminateTestRun(String message) {
		LOGGER.fatal(message);
		System.exit(1);

	}

    private MuleContext getMuleContext() {
        return this.baseConnectorTestCase.getMuleContext();
    }

    @BeforeClass
    public static void beforeClass() throws NoSuchFieldException, IllegalAccessException {
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

    // TODO: Keep this for backwards compatibility?
    protected String getConfigResources() {
        return getConfigXmlFile();
    }

    protected String getConfigXmlFile() {
        return "automation-test-flows.xml";
    }

    public <T> T getBeanFromContext(String beanId) throws BeansException {
		return (T) context.getBean(beanId);
	}

    protected TestFlow getFlow(String flowName) throws Exception {
        return new TestFlow(muleContext, context, flowName);
    }

}
