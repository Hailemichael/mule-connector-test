package org.mule.modules.tests;

import org.mule.api.MuleEvent;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

public class TestData {

    private Map<String, Object> flowVars = new HashMap<String, Object>();
    private Object payload;

    /**
     * Helper method to migrate test data in the "old" format (payload stored
     * as the value assigned to "payloadContent" in a map).
     * <p>This method only exists to provide backwards compatibility and should be removed when possible.</p>
     * @param oldData The map from which to create the test run message.
     * @return A complete TestData object resulting from the specified map.
     */
    static TestData fromMap(Map<String, Object> oldData) {
        TestData newData = new TestData();
        if (oldData == null) {
            return newData;
        }
        for (String key : oldData.keySet()) {
            if (!"payloadContent".equals(key)) {
                newData.setFlowVar(key, oldData.get(key));
            }
        }
        if (oldData.containsKey("payloadContent")) {
            newData.setPayload(oldData.get("payloadContent"));
        }
        return newData;
    }

    /**
     * Helper method to use beans as test data following the old convention, which is as follows:
     * <p>
     * If the bean is a map and its name ends with "TestData", its contents are set as flow variables
     * and the content of the key named "payloadContent" is set as the payload.
     * If not, the entire map is set as the payload and no flow variables are set.
     * </p>
     * @param beanId The name of the bean
     * @param context
     * @return
     */
    static TestData fromBean(String beanId, ApplicationContext context) {
        Object bean = context.getBean(beanId);
        if (bean instanceof Map && beanId.endsWith("TestData")) {
            return TestData.fromMap((Map) bean);
        } else {
            return new TestData().setPayload(bean);
        }
    }

    /**
     * Generates a MuleEvent from the defined payload and flow variables.
     */
    MuleEvent getMuleEvent() throws Exception {
        MuleEvent event = ConnectorTestCase.getTestEvent(this.payload);
        for (String key : this.flowVars.keySet()) {
            event.setFlowVariable(key, this.flowVars.get(key));
        }
        return event;
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
    public Object getFlowVarContent(String flowVarName) {
        // Don't break backwards compatibility with old-style map
        if (flowVarName.equals("payloadContent")) {
            return this.getPayload();
        } else {
            return this.getFlowVars().get(flowVarName);
        }
    }

    /**
     * Returns a mapping of flow variable names to their contents.
     * @return
     */
    public Map<String, Object> getFlowVars() {
        return this.flowVars;
    }

    /**
     * Replaces all the flow variables with a map
     * @param flowVars A mapping of flow variable names to their contents
     * @return The modified test run message
     */
    public TestData setFlowVars(Map<String, Object> flowVars) {
        this.flowVars = flowVars;
        return this;
    }

    /**
     * Adds all the flow variables given by a map. If a flow variable already exists,
     * it is updated with the new value. If not, it is created.
     * @param flowVars
     * @return
     */
    public TestData addFlowVars(Map<String, Object> flowVars) {
        this.flowVars.putAll(flowVars);
        return this;
    }

    public <T> T getPayload() {
        return (T) this.payload;
    }

    public TestData setPayload(Object payload) {
        this.payload = payload;
        return this;
    }

}
