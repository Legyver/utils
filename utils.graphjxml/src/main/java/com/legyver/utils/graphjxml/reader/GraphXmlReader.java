package com.legyver.utils.graphjxml.reader;

import com.legyver.utils.graphjxml.XmlGraph;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.Reader;
import java.util.StringJoiner;

/**
 * Read and parse an XML file into a graph
 */
public class GraphXmlReader {
    private static final Logger logger = LogManager.getLogger(GraphXmlReader.class);
    private XMLInputFactory xmlif;

    /**
     * Construct an XML Reader to read XML into a graph
     */
    public GraphXmlReader() {
        try {
            xmlif = XMLInputFactory.newInstance();
            xmlif.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, true);
            xmlif.setProperty(XMLInputFactory.IS_COALESCING, true);
        } catch (Exception ex) {
            logger.error("Error configuring StAX parser", ex);
        }
    }

    /**
     * Parse the contents of XML into an XMLGraph.
     * @param reader the reader supplying the XML
     * @return the graph
     * @throws XMLStreamException if there is an error reading the XML
     */
    public XmlGraph parse(Reader reader) throws XMLStreamException {
        XmlGraph graph;
        XMLStreamReader xmlr = null;
        try {
            xmlr = xmlif.createXMLStreamReader(reader);
            graph = parse(xmlr, null);
        } finally {
            //XMLStreamReader is not auto-closeable
            if (xmlr != null) {
                try {
                    xmlr.close();
                } catch (XMLStreamException ex) {
                    logger.error("Error closing stream", ex);
                }
            }
        }
        return graph;
    }

    private XmlGraph parse(XMLStreamReader xmlr, XmlGraph graph) throws XMLStreamException {
        XmlGraph parent = graph;
        while (xmlr.hasNext()) {
            switch (xmlr.next()) {
                case XMLEvent.START_ELEMENT:
                    QName qName = xmlr.getName();
                    String currentElement = qName.getLocalPart();
                    XmlGraph child = new XmlGraph(currentElement, parent, XmlGraph.NodeType.ELEMENT);
                    child.appendContext("<").append(currentElement);
                    addAttributes(xmlr, child);
                    child.appendContext(">");
                    if (parent != null) {
                        parent.accept(child);
                    }
                    parent = child;
                    break;
                case XMLEvent.CHARACTERS:
                    String content = xmlr.getText().trim();
                    parent.setValue(content);
                    parent.appendContext(content);
                    break;
                case XMLEvent.END_ELEMENT:
                    if (parent.getName() != null) {
                        parent.appendContext("</").append(parent.getName()).append(">");
                    }
                    if (parent.getParent() != null) {
                        parent = parent.getParent();
                    }
                    break;
                default: //noop
            }
        }
        return parent;
    }

    private void addAttributes(XMLStreamReader xmlr, XmlGraph parent) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        for (int i = 0; i < xmlr.getAttributeCount(); i++) {
            String name = xmlr.getAttributeName(i).toString();
            String value = xmlr.getAttributeValue(i);
            XmlGraph child = new XmlGraph(name, parent, XmlGraph.NodeType.ATTRIBUTE);
            child.setValue(value);
            stringJoiner.add(name + "=" + value);
            parent.accept(child);
        }
        parent.appendContext(stringJoiner.toString());
    }
}
