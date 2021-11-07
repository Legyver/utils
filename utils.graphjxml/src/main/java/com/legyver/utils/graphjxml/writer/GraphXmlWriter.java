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
            writeGraph(0, false, xmlGraph, streamWriter);
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

    private boolean writeGraph(int depth, boolean lineTerminated, XmlGraph xmlGraph, XMLStreamWriter streamWriter) throws XMLStreamException {
        String name = xmlGraph.getName();
        String value = xmlGraph.getValue();
        XmlGraph.NodeType nodeType = xmlGraph.getNodeType();
        if (!lineTerminated) {
            streamWriter.writeCharacters(System.lineSeparator());
        }
        writeIndentation(depth, streamWriter);
        boolean endsWithNewLine = false;
        switch (nodeType) {
            case ELEMENT:
                if (valueEmptyOrNull(value) && xmlGraph.getChildren().isEmpty()) {
                    streamWriter.writeEmptyElement(name);
                } else {
                    streamWriter.writeStartElement(name);
                }
                break;
            case ATTRIBUTE:
                streamWriter.writeAttribute(name, value);
                break;
            default:
                endsWithNewLine = true;
        }
        for (XmlGraph child : xmlGraph.getChildren()) {
            //write nested elements and attributes
            endsWithNewLine = writeGraph(depth + 1, endsWithNewLine, child, streamWriter);
        }

        switch (nodeType) {
            case ELEMENT:
                if (!valueEmptyOrNull(value)) {
                    streamWriter.writeCharacters(value);
                }
                //close element if an empty element was not written
                if (!valueEmptyOrNull(value) || !xmlGraph.getChildren().isEmpty()) {
                    if (valueEmptyOrNull(value)) {
                        writeIndentation(depth, streamWriter);
                    }
                    streamWriter.writeEndElement();
                    streamWriter.writeCharacters(System.lineSeparator());
                    endsWithNewLine = true;
                }
                break;
            default:
                //noop
        }

        return endsWithNewLine;
    }

    private boolean valueEmptyOrNull(String value) {
        return value == null || value.isEmpty();
    }

    private void writeIndentation(int depth, XMLStreamWriter streamWriter) throws XMLStreamException {
        for (int i = 0; i < depth; i++) {
            streamWriter.writeCharacters("\t");
        }
    }
}
