/**
 * (c) 2003-2013 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */

package org.mule.modules.tests;

import org.mule.api.MuleContext;
import org.mule.api.config.ConfigurationBuilder;
import org.mule.config.spring.SpringXmlConfigurationBuilder;
import org.mule.tck.junit4.AbstractMuleContextTestCase;

/**
 * This class allows us to use composition instead of inheritance in
 * {@link org.mule.modules.tests.AutomationTestCase}, which lets us have control of the interface
 * we wish to expose. As a trade-off, it requires this code to leverage
 * functionality that FunctionalTestCase already has.
 */
class BaseConnectorTestCase extends AbstractMuleContextTestCase {

    private final String flowsXml;
    private MuleContext muleContext = null;

    public BaseConnectorTestCase(String flowsXml) {
        this.flowsXml = flowsXml;
    }

    @Override
    protected ConfigurationBuilder getBuilder() throws Exception
    {
        return new SpringXmlConfigurationBuilder(flowsXml);
    }

    // Work around muleContext being protected
    public MuleContext getMuleContext() throws Exception {
        if (this.muleContext != null) {
            return muleContext;
        } else {
            setUpMuleContext();
            MuleContext context = super.createMuleContext();
            context.start();
            return context;
        }
    }
}
