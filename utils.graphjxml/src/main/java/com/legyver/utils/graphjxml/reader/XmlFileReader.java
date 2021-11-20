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
        try (Reader reader = new LineFeedReader(new FileReader(file))) {
            xmlGraph = graphXmlReader.parse(reader);
        } catch (IOException | XMLStreamException e) {
            logger.error("Unable to read file", e);
        }
        return xmlGraph;
    }

    /**
     * XML stream readers are really picky about no blank lines before XML content.
     * This reader attempts to seek passed these lines to avoid issues.
     */
    private class LineFeedReader extends Reader {
        private final Reader reader;
        private boolean firstLine = true;
        int offset = 0;
        public LineFeedReader(Reader reader) {
            this.reader = reader;
        }

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            int result = reader.read(cbuf, off, len);
            if (firstLine) {
                int start = -1;
                if ('\r' == cbuf[0]) {
                    start = 2;
                } else if ('\n' == cbuf[0]) {
                    start = 1;
                }
                if (start > -1) {
                    offset += start;
                    int stopAt = cbuf.length - 1;
                    for (int i = start; i < len; i++) {
                        char copy = cbuf[i];
                        if (i > len - start) {
                            if (start == 1) {
                                cbuf[i - 2] = '\n';
                            } else if (start == 2) {
                                cbuf[i - 2] = '\r';
                                cbuf[i - 1] = '\n';
                            }
                            break;
                        }
                        cbuf[i - 2] = copy;
                    }
                }
                firstLine = false;
            }
            return result;
        }

        @Override
        public void close() throws IOException {
            reader.close();
        }
    }
}
