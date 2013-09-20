Mule Connector Test Framework
=============================

Framework for making Mule connectors testing easier.

Get Started
-----------

Add
<pre><code>

         <dependency>
            <groupId>org.mule.tests</groupId>
            <artifactId>mule-tests-functional</artifactId>
            <version>${mule.version}</version>
            <scope>test</scope>
        </dependency>

</code></pre><pre><code>

        <dependency>
            <groupId>org.mule.modules</groupId>
            <artifactId>mule-connector-test</artifactId>
            <version>${mule.devkit.version}</version>
            <scope>test</scope>
        </dependency>
        
</code></pre>        
        
to the pom.xml of the connector.

The [TestParent](https://github.com/mulesoft/mule-connector-test/blob/master/src/main/java/org/mule/modules/tests/TestParent.java)
class simplifies how to call Mule flows and process the corresponding results, you only need to extend it in your connector
TestParent (e.g. [SalesforceTestParent](https://github.com/mulesoft/salesforce-connector/blob/master/src/test/java/org/mule/modules/salesforce/automation/testcases/SalesforceTestParent.java?source=cc)).
It includes the following elements:

- getConfigXmlFile(): points to the Mule XML config file (default "automation-test-flows.xml").
- DEFAULT_SPRING_CONFIG_FILE: Default Spring XML file for the testing beans ("AutomationSpringBeans.xml").
- messageTestObject: Map treated as Mule Message in a flow, it can be updated to add runtime generated data thus working
as backbone between @Before, @Test and @After methods. The methods <b>addToMessageTestObject(...)</b> and <b>getValueFromMessageTestObject(...)</b>
must be used by adding and consuming values to/from messageTestObject.
- runFlowAndGetPayload(String flow): provides a simple way for running a flow and getting the payload:
<pre><code>String payload = runFlowAndGetPayload("test-get-payload");</code></pre>
<pre><code>MyObject payload = runFlowAndGetPayload("test-get-payload");</code></pre>

For futher samples please take a look at the included [tests](https://github.com/mulesoft/mule-connector-test/tree/master/src/test/java/org/mule/modules/tests).
