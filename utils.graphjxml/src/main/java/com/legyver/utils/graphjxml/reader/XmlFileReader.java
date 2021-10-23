package com.legyver.utils.graphjxml.reader;

import com.legyver.utils.graphjxml.XmlGraph;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.stream.XMLStreamException;
import java.io.*;

/**
 * Decorator for a {@link GraphXmlReader} to supply the file reference to be read
 */
public class XmlFileReader {
    private static final Logger logger = LogManager.getLogger(XmlFileReader.class);

    /**
     * The decorated graph reader that handles the XML to graph transformation
     */
    private final GraphXmlReader graphXmlReader;

    /**
     * Decorate an GraphXmlReader to construct an XmlFileReader
     * @param graphXmlReader the graph reader to decorate
     */
    public XmlFileReader(GraphXmlReader graphXmlReader) {
        this.graphXmlReader = graphXmlReader;
    }

    /**
     * Parse the xml file into a graph
     * @param file the xml file to parse
     * @return the corresponding XmlGraph
     */
    public XmlGraph parse(File file) {
        XmlGraph xmlGraph = null;
        try (FileReader fileReader = new FileReader(file)) {
            xmlGraph = graphXmlReader.parse(fileReader);
        } catch (IOException | XMLStreamException e) {
            logger.error("Unable to read file", e);
        }
        return xmlGraph;
    }
}
