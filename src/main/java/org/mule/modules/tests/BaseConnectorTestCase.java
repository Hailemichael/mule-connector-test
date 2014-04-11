package org.mule.modules.tests;

import org.mule.MessageExchangePattern;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.config.ConfigurationBuilder;
import org.mule.config.spring.SpringXmlConfigurationBuilder;
import org.mule.tck.MuleTestUtils;
import org.mule.tck.junit4.AbstractMuleContextTestCase;

/**
 * This class allows us to use composition instead of inheritance in
 * {@link ConnectorTestCase}, which lets us have control of the interface
 * we wish to expose. As a trade-off, it requires this code to leverage
 * functionality that FunctionalTestCase already has.
 */
class BaseConnectorTestCase extends AbstractMuleContextTestCase {

    private final String flowsXml;

    public BaseConnectorTestCase(String flowsXml) {
        this.flowsXml = flowsXml;
    }

    public static MuleEvent getTestEvent(Object data) throws Exception {
        return MuleTestUtils.getTestEvent(data, MessageExchangePattern.REQUEST_RESPONSE, muleContext);
    }

    @Override
    protected ConfigurationBuilder getBuilder() throws Exception
    {
        return new SpringXmlConfigurationBuilder(flowsXml);
    }

    // Work around muleContext being protected
    public MuleContext getMuleContext() {
        MuleContext context = null;
        try {
            context = super.createMuleContext();
            context.start();
        } catch (Exception e) {
            // If MuleContext initialization fails, there is nothing
            // that can be done. By not throwing an exception here,
            // we don't force ConnectorTestCase subclasses to declare
            // a default constructor that throws Exception.
        }
        return context;

    }
}
