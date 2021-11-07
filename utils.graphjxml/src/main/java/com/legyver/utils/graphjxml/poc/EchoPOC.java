package com.legyver.utils.graphjxml.poc;

import com.legyver.utils.graphjxml.XmlGraph;
import com.legyver.utils.graphjxml.reader.GraphXmlReader;
import com.legyver.utils.graphjxml.reader.XmlFileReader;
import com.legyver.utils.graphjxml.writer.GraphXmlWriter;
import com.legyver.utils.graphjxml.writer.XmlFileWriter;

import java.io.File;

import static com.legyver.utils.graphjxml.poc.POCContext.etcFile;

/**
 * POC demonstrating reading and writing xml with pretty-print
 */
public class EchoPOC {
    /**
     * POC demonstrating writing graph to XML
     * @param args ignored
     */
    public static void main(String[] args) {
        XmlFileReader xmlFileReader = new XmlFileReader(new GraphXmlReader());
        XmlFileWriter xmlFileWriter = new XmlFileWriter(new GraphXmlWriter());
        //original graph
        XmlGraph simple = xmlFileReader.parse(etcFile("simple.xml"));
        File file = etcFile("echo.xml");
        xmlFileWriter.writeToFile(simple,  file);
    }
}
