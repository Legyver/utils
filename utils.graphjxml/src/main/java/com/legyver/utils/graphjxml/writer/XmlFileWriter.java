package com.legyver.utils.graphjxml.writer;

import com.legyver.utils.graphjxml.XmlGraph;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Decorator for a GraphXmlWriter to output the result to a file
 */
public class XmlFileWriter {
    private static final Logger logger = LogManager.getLogger(XmlFileWriter.class);

    /**
     * The GraphXmlWriter that converts the graph to XML
     */
    private final GraphXmlWriter graphXmlWriter;

    /**
     * Decorate a GraphXmlWriter to construct an XmlFileWriter to output the XML to a file
     * @param graphXmlWriter the writer to decorate
     */
    public XmlFileWriter(GraphXmlWriter graphXmlWriter) {
        this.graphXmlWriter = graphXmlWriter;
    }

    /**
     * Export the graph to an XML file
     * @param xmlGraph the graph to export
     * @param file the file to export the graph to
     */
    public void writeToFile(XmlGraph xmlGraph, File file) {
        try (FileWriter fileWriter = new FileWriter(file)) {
            graphXmlWriter.writeGraph(xmlGraph, fileWriter);
        } catch (IOException|XMLStreamException e) {
            logger.error("Error writing graph to file", e);
        }
    }
}
