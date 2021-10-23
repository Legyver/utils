package com.legyver.utils.graphjxml.poc;

import com.legyver.utils.graphjxml.XmlGraph;
import com.legyver.utils.graphjxml.reader.GraphXmlReader;
import com.legyver.utils.graphjxml.reader.XmlFileReader;

import java.io.File;

import static com.legyver.utils.graphjxml.poc.POCContext.etcFile;

/**
 * POC to demonstrate reading XML into a graph.
 * The read XML is checked into the /etc directory in the top-level of this git project
 */
public class ReaderPOC {
    /**
     * POC demonstrating reading of XML into a graph
     * @param args ignored
     */
    public static void main(String[] args) {
        File file = etcFile("simple.xml");

        XmlFileReader xmlFileReader = new XmlFileReader(new GraphXmlReader());
        XmlGraph xmlGraph = xmlFileReader.parse(file);
        //just another executable line to allow debugger to examine result
        boolean breakHere = true;
    }
}
