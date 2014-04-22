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

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;


/**
 * Connector Tests Framework Parent Class
 *
 * @author Mulesoft, Inc
 */
@SuppressWarnings("unchecked")
public class AutomationTestCase {

    private static final Logger LOGGER = Logger.getLogger(AutomationTestCase.class);

    private BaseConnectorTestCase baseConnectorTestCase = new BaseConnectorTestCase(getConfigXmlFile());
    private static ApplicationContext context;

    @BeforeClass
    public static void beforeClass() throws NoSuchFieldException, IllegalAccessException {
        context = new ClassPathXmlApplicationContext(getConfigXmlFile());
    }

    protected static String getConfigXmlFile() {
        return "automation-test-flows.xml";
    }

    public <T> T getBeanFromContext(String beanId) throws BeansException {
        return (T) context.getBean(beanId);
    }

    protected TestFlow getFlow(String flowName) throws Exception {
        return new TestFlow(baseConnectorTestCase.getMuleContext(), context, flowName);
    }

    /**
     * This method provides access to the Mule context, which allows manual usage
     * of all Mule functions.
     * <p>Only use this method as a last resort when this class doesn't provide
     * the functionality you need.</p>
     * @throws Exception
     */
    protected MuleContext getMuleContext() throws Exception {
        return this.baseConnectorTestCase.getMuleContext();
    }

}
