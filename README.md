Mule Connector Test Framework
=============================

Framework for making Mule connectors testing easier.

Get Started
-----------
The [ConnectorTestCase](https://github.com/mulesoft/mule-connector-test/blob/master/src/main/java/org/mule/modules/tests/ConnectorTestCase.java)
class simplifies how to call Mule flows and process the corresponding results, you only need to extend it in your connector
TestParent class (e.g. [SalesforceTestParent](https://github.com/mulesoft/salesforce-connector/blob/master/src/test/java/org/mule/modules/salesforce/automation/testcases/SalesforceTestParent.java?source=cc)).

It includes the following elements:

- getConfigXmlFile(): points to the Mule XML config file (default "automation-test-flows.xml").
- DEFAULT_SPRING_CONFIG_FILE: Default Spring XML file for the testing beans ("AutomationSpringBeans.xml").
- The testData attribute is the TestRunMessage data structure. TestRunMessage can be updated to add runtime generated data thus working as backbone between @Before, @Test and @After methods. Tests interact with the TestRunMessage trough a set of methods.
- runFlowAndGetPayload: provides a simple way for running a flow and getting payload prior to loading its input data in the TestRunMessage:
<pre><code>String payload = runFlowAndGetPayload("test-get-payload");</code></pre>
<pre><code>MyObject payload = runFlowAndGetPayload("test-get-payload");</code></pre>
- runFlowAndGetMessage serves the same purpose as runFlowAndGetPayload but will return a Mule Message 

For futher samples please take a look at the included [tests](https://github.com/mulesoft/mule-connector-test/tree/master/src/test/java/org/mule/modules/tests).
