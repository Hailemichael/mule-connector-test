Getting started
===============
The `AutomationTestCase` class provides a simplified way to interact with Mule by calling flows and processing the results.
The examples and tests use JUnit, but it can be used with any testing framework.

This is a simple case of running a flow using a Spring bean as input data and making an assertion on the result.
```java
@Test public void simpleTest() {
    TestFlow myFlow = getFlow("my-flow");
    TestFlowResult result = myFlow.runWithBean("myBean");
    assertEquals(2, result.getPayload());
}
```

Flows are defined in `automation-test-flows.xml` by default.
Override `getConfigXmlFile()` to change this.

Spring beans are defined in `AutomationSpringBeans.xml` by default.
Override `getBeansXmlFile()` to change this.

The `TestData` class can be used to create test messages at runtime.
It consists of flow variables and a payload, which can be accessed with MEL (Mule Expression Language) expressions.

```java
@Test public void anotherTest() {
    TestData data = new TestData().withFlowVar("test", 10);
    assertEquals(10, getFlow("test-flow").run(data).getPayload());
}
```

```xml
<flow name="test-flow">
    <set-payload value="#[flowVars.test]" />
</flow>
```

Inbound endpoints
=================

To test an inbound endpoint, you must add an outbound VM endpoint after it.
An outbound VM endpoint is an in-memory message queue.

For example, if you are testing an SQS queue, your flow will look something like this after adding the VM endpoint:

```xml
<flow name="receive-message">
    <sqs:receive-messages config-ref="Sqs" />
    <!-- process received messages here -->
    <vm:outbound-endpoint address="vm://sqs" />
</flow>
```

In your test, call `runAndWaitOnVM()` on a flow to run it and return the result received by the VM queue.

```xml
<flow name="send-message" doc:name="SendMessage">
    <sqs:send-message config-ref="Sqs" message="Hi" />
</flow>
```

```java
@Test public void receiveMessage() {
    TestFlowResult result = getFlow("send-message").runAndWaitOnVM("sqs", 30 * 1000);
    assertEquals("Hi", result.getPayload());
}
```

Further reading
===============

For more information, read the Javadocs or see the included tests.
If you need to do something with Mule that this framework doesn't support, you have two options:

1. Send a pull request
2. Use `getMuleContext()` and interact with Mule manually
