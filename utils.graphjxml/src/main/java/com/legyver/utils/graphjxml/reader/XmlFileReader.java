package com.legyver.utils.graphjxml.reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

/**
 * Decorator for a {@link GraphXmlReader} to supply the file reference to be read
 */
public class XmlFileReader extends AbstractInputReader<File> {

    /**
     * Decorate an GraphXmlReader to construct an XmlFileReader
     * @param graphXmlReader the graph reader to decorate
     */
    public XmlFileReader(GraphXmlReader graphXmlReader) {
        super(graphXmlReader);
    }

    @Override
    protected Reader getReader(File input) throws FileNotFoundException {
        return new FileReader(input);
    }


}
