/**
 * (c) 2003-2013 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */

package org.mule.modules.tests;

import org.junit.BeforeClass;
import org.mule.api.MuleContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Connector Tests Framework Parent Class
 *
 * @author Mulesoft, Inc
 */
public class AutomationTestCase {

    private static ApplicationContext context;
    private BaseConnectorTestCase baseConnectorTestCase = new BaseConnectorTestCase(getConfigXmlFile());

    @BeforeClass
    public static void beforeClass() throws NoSuchFieldException, IllegalAccessException {
        context = new ClassPathXmlApplicationContext(getConfigXmlFile(), getBeansXmlFile());
    }

    /**
     * Override this method if you want to specify a flow file to use for this class.
     * By default, it is {@code automation-test-flows.xml}.
     */
    protected static String getConfigXmlFile() {
        return "automation-test-flows.xml";
    }

    /**
     * Override this method if you want to specify a bean file to use for this class.
     * By default, it is {@code AutomationSpringBeans.xml}.
     */
    protected static String getBeansXmlFile() {
        return "AutomationSpringBeans.xml";
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
