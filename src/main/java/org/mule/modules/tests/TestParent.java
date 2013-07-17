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
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;
import org.mule.tck.junit4.FunctionalTestCase;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Connector Tests Framework Parent Class
 *
 * @author Mulesoft, Inc
 */
@SuppressWarnings("unchecked")
public abstract class TestParent extends FunctionalTestCase {

    private static final Logger LOGGER = Logger.getLogger(TestParent.class);
    private static final String DEFAULT_SPRING_CONFIG_FILE = "AutomationSpringBeans.xml";

    protected static final String[] SPRING_CONFIG_FILES = new String[] { DEFAULT_SPRING_CONFIG_FILE };
    protected static ApplicationContext context;

    private Map<String,Object> messageTestObject = new HashMap<String, Object>();

    @BeforeClass
    public static void beforeClass(){
        try {
            context = new ClassPathXmlApplicationContext(SPRING_CONFIG_FILES);
        } catch (Exception e) {
            LOGGER.warn("The default Spring beans config file was not found. Couldn't create the context for TestParent.");
        }
    }

    @Override
    protected String getConfigResources() {
        return getConfigXmlFile();
    }

    /**
     * Allows the user to override the default xml file
     * @return Mule xml config file
     */
    protected String getConfigXmlFile() {
        return "automation-test-flows.xml";
    }

    /**
     * Runs a flow using messageTestObject as payload
     *
     * @param flowName name of the flow to run
     * @return message payload
     * @throws Exception
     */
    protected <T> T runFlow(String flowName) throws Exception {
        MuleEvent response = lookupFlowConstruct(flowName).process(getTestEvent(messageTestObject));
        return (T) response.getMessage().getPayload();
    }

    private MessageProcessor lookupFlowConstruct(String name) {
        return (MessageProcessor) muleContext.getRegistry().lookupFlowConstruct(name);
    }

    /**
     * Adds a value to the message test object
     * @param key key
     * @param value value
     */
    protected void addToMessageTestObject(String key, Object value) {
        messageTestObject.put(key, value);
    }

    /**
     * Adds a complete map to the message test object
     * @param testObject map to be added to the Message test object
     */
    protected void addToMessageTestObject(Map testObject) {
        messageTestObject.putAll(testObject);
    }

    /**
     * Retrieves a value from the message test object using the given key
     *
     * @param key key
     * @return value from Message test object
     */
    protected <T> T getValueFromMessageTestObject(String key) {
        return (T) messageTestObject.get(key);
    }
}
