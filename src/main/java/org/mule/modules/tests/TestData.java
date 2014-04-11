package org.mule.modules.tests;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * An immutable representation of the test data, which consists of flow variables
 * and the message's payload. Its contents can be accessed in a flow
 * through Mule Expression Language (MEL) expressions.
 */
public class TestData {

    private final Map<String, Object> flowVars;
    private final Object payload;

    /**
     * Creates a new {@code TestData} object from a map that follows a convention.
     * If there is a key named {@code payloadContent}, its value will be
     * set as the payload. The other values will be set as flow variables.
     * @param map The map to create the {@code TestData} object from.
     */
    public TestData fromMap(Map<String, Object> map) {
        Object payload = null;
        Map<String, Object> flowVars = new HashMap<String, Object>();
        for (String key : map.keySet()) {
            if (key.equals("payloadContent")) {
                payload = map.get(key);
            } else {
                flowVars.put(key, map.get(key));
            }
        }
        return new TestData(flowVars, payload);
    }

    public TestData(Map<String, Object> flowVars, Object payload) {
        if (flowVars == null) {
            this.flowVars = new HashMap<String, Object>();
        } else {
            this.flowVars = flowVars;
        }
        this.payload = payload;
    }

    /**
     * Creates a new {@code TestData} object with no flow variables and no payload.
     */
    public TestData() {
        this(null, null);
    }

    /**
     * Returns a new {@code TestData} object with a created or updated flow variable.
     * @param flowVarName The flow variable to create or modify.
     * @param value The content to assign to this flow variable.
     */
    public TestData withFlowVar(String flowVarName, Object value) {
        Map<String, Object> newFlowVars = new HashMap<String, Object>();
        newFlowVars.putAll(this.flowVars);
        newFlowVars.put(flowVarName, value);
        return new TestData(newFlowVars, this.payload);
    }

    /**
     * Returns the content associated to a flow variable. In an actual flow, this
     * can be accessed by a MEL expression: {@code #[flowVars.flowVarName]}
     * @param flowVarName The name of the flow variable.
     * @return If no such flow variable has been defined, returns {@code null}. Keep in
     * mind that {@code null} is a legal value for a flow variable.
     */
    public Object getFlowVar(String flowVarName) {
        return this.getFlowVars().get(flowVarName);
    }

    /**
     * @return An immutable mapping of flow variable names to their contents.
     */
    public Map<String, Object> getFlowVars() {
        return Collections.unmodifiableMap(this.flowVars);
    }

    /**
     * Returns a new {@code TestData} object with updated flow variables given by a map.
     * If a flow variable already exists, it is updated with the new value. If not,
     * it is created.
     * @param flowVars The mapping of flow variable names to their contents to update.
     * @return The new and updated test data object.
     */
    public TestData addFlowVars(Map<String, Object> flowVars) {
        Map<String, Object> newFlowVars = new HashMap<String, Object>();
        newFlowVars.putAll(this.flowVars);
        newFlowVars.putAll(flowVars);
        return new TestData(newFlowVars, this.payload);
    }

    /**
     * @return The payload associated to this {@code TestData} object. It can be
     * accessed in a flow by using a MEL expression, as such: {@code #[payload]}
     */
    @SuppressWarnings("unchecked")
    public <T> T getPayload() {
        return (T) this.payload;
    }


    /**
     * Returns a new {@code TestData} object with the modified payload.
     * @param payload The payload object to set.
     */
    public TestData withPayload(Object payload) {
        return new TestData(this.flowVars, payload);
    }

}
