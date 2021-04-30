package util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class XMLProcessor {

    public static Document importXMLDocument(String filepath){
        String xmlCode = FileImporter.importFile(filepath);
        try {
            return parseXML(xmlCode);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Document parseXML(String xmlCode) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = null;

        ByteArrayInputStream input = null;
        try {
            input = new ByteArrayInputStream(
                    xmlCode.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            doc = builder.parse(input);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return doc;
    }

    public static String getAttributeValue(String attributeName, Node node){
        Node attribute = node.getAttributes().getNamedItem(attributeName);
        if(attribute!=null){
            return attribute.getNodeValue();
        }
        return null;
    }

    public static List<Node> getNodesByTagName(String tagName, Element element){
        List<Node> nodes = new ArrayList<>();
        NodeList nl = element.getElementsByTagName(tagName);

        if(nl!=null) {
            for (int i = 0; i < nl.getLength(); i++) {
                nodes.add(nl.item(i));
            }
        }
        return nodes;
    }

    public static List<Node> getNodesByTagName(String tagName, Document document){
        List<Node> nodes = new ArrayList<>();
        NodeList nl = document.getElementsByTagName(tagName);

        for(int i = 0; i<nl.getLength(); i++){
            nodes.add(nl.item(i));
        }

        return nodes;
    }

    public static Node getNodeById(String id, Document document){
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = null;

        try {
            String e = "//*[@id=\""+id+"\"]";
            expr = xpath.compile(e);
            NodeList nl = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
            if(nl.getLength()>0){
                return nl.item(0);
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Node getNodeById(String id, Element element){
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = null;

        try {
            expr = xpath.compile("//*[@id=\'"+id+"\']");
            NodeList nl = (NodeList) expr.evaluate(element, XPathConstants.NODESET);
            if(nl.getLength()>0){
                return nl.item(0);
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return null;
    }

}
