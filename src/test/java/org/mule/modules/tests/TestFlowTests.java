package org.mule.modules.tests;

import org.junit.Test;
import org.junit.runners.model.InitializationError;

import static org.junit.Assert.assertEquals;

public class TestFlowTests extends ConnectorTestCaseTestParent {

    @Test
    public void runFlowManualTestData() throws Exception {
        TestData data = new TestData().withFlowVar("foo", "bar").withPayload(1000);
        TestFlowResult result = getFlow("cat-foo-and-payload").run(data);
        assertEquals("bar1000", result.getPayload());
    }

    @Test
    public void runFlowWithTestDataBean() throws Exception {
        TestFlowResult result = getFlow("cat-foo-and-payload").runWithBean("fooBar1000TestData");
        assertEquals("bar1000", result.getPayload());
    }

    @Test
    public void runFlowWithRegularMapBean() throws Exception {
        TestFlowResult result = getFlow("echo-payload").runWithBean("fooBar1000");
        assertEquals(getBeanFromContext("fooBar1000"), result.getPayload());
    }

    @Test
    public void runFlowWithBean() throws Exception {
        Integer expected = getBeanFromContext("integerPOJO");
        TestFlowResult result = getFlow("echo-payload").runWithBean("integerPOJO");
        assertEquals(expected, result.getPayload());
    }

    @Test
    public void runFlowWithoutData() throws Exception {
        TestFlowResult result = getFlow("echo-foo").run();
        assertEquals("foo", result.getPayload());
    }

    @Test(expected = InitializationError.class)
    public void nonExistentFlow() throws Exception {
        getFlow("fooFlow");
    }

}