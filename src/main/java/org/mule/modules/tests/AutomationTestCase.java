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

    @SuppressWarnings("unchecked")
    public <T> T getBeanFromContext(String beanId) {
        return (T) context.getBean(beanId);
    }

    /**
     * Returns a TestFlow object that can be run with custom test data.
     * @param flowName The value of the {@code name} tag of the {@code flow} element.
     * @return A TestFlow object representing the requested Mule flow.
     * @throws java.lang.IllegalArgumentException if the flow could not be found
     */
    protected TestFlow getFlow(String flowName) {
        return new TestFlow(baseConnectorTestCase.getMuleContext(), context, flowName);
    }

    /**
     * This method provides access to the Mule context, which allows manual usage
     * of all Mule functions.
     * <p>Only use this method as a last resort when this class doesn't provide
     * the functionality you need.</p>
     */
    protected MuleContext getMuleContext() {
        return this.baseConnectorTestCase.getMuleContext();
    }

}
