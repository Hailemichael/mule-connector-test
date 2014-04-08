package org.mule.modules.tests;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rolodato on 4/8/14.
 */
public class TestFlowsFile {
    private List<String> testFlowsNames = new ArrayList<String>();

    public TestFlowsFile(String filename) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder;
        Document testFlowsDocument = null;
        NodeList flowsList;

        documentBuilder = documentBuilderFactory.newDocumentBuilder();
        InputStream dataStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
        testFlowsDocument = documentBuilder.parse(dataStream);

        flowsList = testFlowsDocument.getElementsByTagName("flow");
        for (int flowIndex = 0; flowIndex < flowsList.getLength(); flowIndex++) {
            Node flow = flowsList.item(flowIndex);

            if (flow.getNodeType() == Node.ELEMENT_NODE) {
                Element flowElement = (Element) flow;
                this.testFlowsNames.add(flowElement.getAttribute("name"));
            }
        }
    }

    public List<TestFlow> getTestFlows() {
        List<TestFlow> testFlows = new ArrayList<TestFlow>();
        for (String flowName : this.testFlowsNames) {

        }
        return testFlows;
    }
}
