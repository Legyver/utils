package com.legyver.utils.graphjxml.writer;

import com.legyver.utils.graphjxml.XmlGraph;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Writer;

/**
 * Convert a graph into XML
 */
public class GraphXmlWriter {
    private static final Logger logger = LogManager.getLogger(GraphXmlWriter.class);

    private XMLOutputFactory xmlof;

    /**
     * Construct a GraphXmlWriter to convert a graph to XML
     */
    public GraphXmlWriter() {
        this.xmlof = XMLOutputFactory.newInstance();
    }

    /**
     * Write the graph
     * @param xmlGraph the graph to write
     * @param writer the writer to write to
     * @throws XMLStreamException if there is an error constructing the XML
     */
    public void writeGraph(XmlGraph xmlGraph, Writer writer) throws XMLStreamException {
        XMLStreamWriter streamWriter = null;
        try {
            streamWriter = xmlof.createXMLStreamWriter(writer);

            streamWriter.writeStartDocument();
            writeGraph(xmlGraph, streamWriter);
            streamWriter.writeEndDocument();
            streamWriter.flush();
        } finally {
            if (streamWriter != null) {
                try {
                    streamWriter.close();
                } catch (XMLStreamException e) {
                    logger.error("Error closing stream writer", e);
                }
            }
        }
    }

    private void writeGraph(XmlGraph xmlGraph, XMLStreamWriter streamWriter) throws XMLStreamException {
        String name = xmlGraph.getName();
        String value = xmlGraph.getValue();
        XmlGraph.NodeType nodeType = xmlGraph.getNodeType();
        switch (nodeType) {
            case ELEMENT:
                if (value == null && xmlGraph.getChildren().isEmpty()) {
                    streamWriter.writeEmptyElement(name);
                } else {
                    streamWriter.writeStartElement(name);
                }
                break;
            case ATTRIBUTE:
                streamWriter.writeAttribute(name, value);
                break;
            default:
                //noop
        }

        for (XmlGraph child : xmlGraph.getChildren()) {
            //write nested elements and attributes
            writeGraph(child, streamWriter);
        }

        switch (nodeType) {
            case ELEMENT:
                if (value != null) {
                    streamWriter.writeCharacters(value);
                }
                //close element if an empty element was not written
                if (value != null || !xmlGraph.getChildren().isEmpty()) {
                    streamWriter.writeEndElement();
                }
                break;
            default:
                //noop
        }
    }
}
