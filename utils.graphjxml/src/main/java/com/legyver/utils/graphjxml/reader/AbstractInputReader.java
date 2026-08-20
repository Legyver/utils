package com.legyver.utils.graphjxml.reader;

import com.legyver.utils.graphjxml.XmlGraph;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.stream.XMLStreamException;
import java.io.BufferedReader;
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
        private boolean leadingNewlineChecked = false;

        public LineFeedReader(Reader reader) {
            //mark/reset must be reliably supported to skip a leading line terminator without
            //losing (or corrupting) the character(s) that follow it
            this.reader = reader.markSupported() ? reader : new BufferedReader(reader);
        }

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            if (!leadingNewlineChecked) {
                skipLeadingNewline();
                leadingNewlineChecked = true;
            }
            return reader.read(cbuf, off, len);
        }

        /**
         * Skip a single leading line terminator - "\r", "\n", or "\r\n" - if present, so the
         * (picky) underlying {@link javax.xml.stream.XMLStreamReader} doesn't choke on a blank
         * line before the XML content. Only ever consumes characters actually present at the
         * start of the stream; anything else is left untouched for the caller to read normally.
         */
        private void skipLeadingNewline() throws IOException {
            reader.mark(2);
            int c = reader.read();
            if (c == '\r') {
                int next = reader.read();
                if (next != '\n') {
                    //not a "\r\n" pair - rewind and consume only the "\r"
                    reader.reset();
                    reader.read();
                }
            } else if (c != '\n') {
                //no leading line terminator (or end of stream) - put everything back
                reader.reset();
            }
        }

        @Override
        public void close() throws IOException {
            reader.close();
        }
    }
}
