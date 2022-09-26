package com.legyver.utils.graphjxml.reader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Reader to read XML input from an InputStream
 */
public class XmlInputStreamReader extends AbstractInputReader<InputStream> {

    /**
     * Construct a reader to read XML input from an InputStream
     * @param graphXmlReader the graph reader to decorate
     */
    public XmlInputStreamReader(GraphXmlReader graphXmlReader) {
        super(graphXmlReader);
    }

    @Override
    protected Reader getReader(InputStream input) {
        return new InputStreamReader(input);
    }

}
