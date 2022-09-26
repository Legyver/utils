package com.legyver.utils.graphjxml.reader;

import com.legyver.utils.graphjxml.XmlGraph;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.Reader;

/**
 * Abstract reader to read xml resources
 * See {@link XmlFileReader}, {@link XmlInputStreamReader}
 * @param <T> the type of the input to read
 */
public abstract class AbstractInputReader<T> {
    private static final Logger logger = LogManager.getLogger(AbstractInputReader.class);

    /**
     * The decorated graph reader that handles the XML to graph transformation
     */
    private final GraphXmlReader graphXmlReader;

    /**
     * Construct a Reader to read the input
     * @param graphXmlReader the graph reader to decorate
     */
    protected AbstractInputReader(GraphXmlReader graphXmlReader) {
        this.graphXmlReader = graphXmlReader;
    }

    /**
     * Read the resource into a graph
     * @param input the input containing the XML
     * @return the xml as a graph
     */
    public XmlGraph parse(T input) {
        XmlGraph xmlGraph = null;
        try (Reader reader = new LineFeedReader(getReader(input))) {
            xmlGraph = graphXmlReader.parse(reader);
        } catch (IOException | XMLStreamException e) {
            logger.error("Unable to read input stream", e);
        }
        return xmlGraph;
    }

    /**
     * Get the reader to read the input
     * @param input the input to read
     * @return the reader for the input
     * @throws IOException if there is some error like FileNotFoundException in the case of FileReader
     */
    protected abstract Reader getReader(T input) throws IOException;

    /**
     * XML stream readers are really picky about no blank lines before XML content.
     * This reader attempts to seek passed these lines to avoid issues.
     */
    private static class LineFeedReader extends Reader {
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
