package org.mule.modules.tests;

import org.mule.api.MuleContext;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * A representation of the TestRunMessage, which consists of flow variables
 * and the message's payload. Its contents can be accessed in a flow
 * through Mule Expression Language (MEL) expressions.
 */
public class TestData {

    private Map<String, Object> flowVars;
    private Object payload;
    private final ApplicationContext context;
    private final MuleContext muleContext;

    TestData(ApplicationContext context, MuleContext muleContext) {
        this.init();
        this.context = context;
        this.muleContext = muleContext;
    }

    private void init() {
        this.payload = null;
        this.flowVars = new HashMap<String, Object>();
    }

    /**
     * Initializes the test data from a given map. If there is a key named
     * {@code payloadContent}, it will be set as the payload.
     * @param oldData The map from which to create the test run message.
     */
    public void initFromMap(Map<String, Object> oldData) {
        this.init();
        for (String key : oldData.keySet()) {
            if (!"payloadContent".equals(key)) {
                this.setFlowVar(key, oldData.get(key));
            }
        }
        if (oldData.containsKey("payloadContent")) {
            this.setPayload(oldData.get("payloadContent"));
        }
    }

    /**
     * Initializes the test data from a given Spring bean ID. If the bean is
     * a map and its ID ends with {@code TestData}, then this method will have
     * the same behaviour as {@link TestData#initFromMap}. If not, it will be
     * set as the message payload.
     * @param beanId The Spring bean ID.
     */
    public void initFromBean(String beanId) {
        Object bean = context.getBean(beanId);
        if (bean instanceof Map && beanId.endsWith("TestData")) {
            this.initFromMap((Map) bean);
        } else {
            this.init();
            this.flowVars = new HashMap<String, Object>();
            this.setPayload(bean);
        }
    }

    /**
     * Assigns a value to a flow variable. If it already exists, it is updated.
     * @param flowVarName The flow variable to create or modify.
     * @param value The content to assign to this flow variable.
     * @return The modified test run message after assigning the variable.
     */
    public TestData setFlowVar(String flowVarName, Object value) {
        this.flowVars.put(flowVarName, value);
        return this;
    }

    /**
     * Returns the content associated to a flow variable. In an actual flow, this
     * can be accessed by a MEL expression: {@code #[flowVars.flowVarName]}
     * @param flowVarName The name of the flow variable.
     * @return If no such flow variable has been defined, returns {@code null}. Keep in
     * mind that {@code null} is a legal value for a flow variable.
     */
    public Object getFlowVar(String flowVarName) {
        if (flowVarName.equals("payloadContent")) {
            return this.getPayload();
        } else {
            return this.getFlowVars().get(flowVarName);
        }
    }

    /**
     * @return A mapping of flow variable names to their contents.
     */
    public Map<String, Object> getFlowVars() {
        return this.flowVars;
    }

    /**
     * Replaces all the flow variables with a map.
     * @param flowVars A mapping of flow variable names to their contents.
     * @return The modified test run message.
     */
    public TestData setFlowVars(Map<String, Object> flowVars) {
        this.flowVars = flowVars;
        return this;
    }

    /**
     * Adds all the flow variables given by a map. If a flow variable already exists,
     * it is updated with the new value. If not, it is created.
     * @param flowVars
     * @return The modified test run message.
     */
    public TestData addFlowVars(Map<String, Object> flowVars) {
        this.flowVars.putAll(flowVars);
        return this;
    }

    /**
     * @return The payload associated to this TestRunMessage object. It can be
     * accessed in a flow by using a MEL expression, as such: {@code #[payload]}
     */
    public <T> T getPayload() {
        return (T) this.payload;
    }


    /**
     * Sets the payload of this test run message object, which can be accessed
     * in a flow through a MEL expression: {@code #[payload]}
     * @param payload The payload object to set.
     * @return The modified test run message object.
     */
    public TestData setPayload(Object payload) {
        this.payload = payload;
        return this;
    }

}
