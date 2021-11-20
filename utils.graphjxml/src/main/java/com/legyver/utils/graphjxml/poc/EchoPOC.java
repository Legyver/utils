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
        DemoIO writeBug = new DemoIO("problematic.xml", "echo-writebug.xml");
        DemoIO readBug = new DemoIO("unabletoread.xml", "echo-readbug.xml");
        runBug(readBug);
    }

    private static void runBug(DemoIO bug) {
        XmlFileReader xmlFileReader = new XmlFileReader(new GraphXmlReader());
        XmlFileWriter xmlFileWriter = new XmlFileWriter(new GraphXmlWriter());
        //original graph
        XmlGraph simple = xmlFileReader.parse(etcFile(bug.inName));
        File file = etcFile(bug.outName);
        xmlFileWriter.writeToFile(simple,  file);
    }

    private static class DemoIO {
        private final String inName;
        private final String outName;

        private DemoIO(String inName, String outName) {
            this.inName = inName;
            this.outName = outName;
        }
    }
}
