package com.legyver.utils.graphjxml.poc;

import com.legyver.utils.graphjxml.XmlGraph;
import com.legyver.utils.graphjxml.reader.GraphXmlReader;
import com.legyver.utils.graphjxml.reader.XmlFileReader;
import com.legyver.utils.graphjxml.writer.GraphXmlWriter;
import com.legyver.utils.graphjxml.writer.XmlFileWriter;

import static com.legyver.utils.graphjxml.poc.POCContext.etcFile;


/**
 * POC to demonstrate merging of graphs into a common parent
 */
public class MergePOC {

    /**
     * Run the POC
     * @param args ignored command line args
     */
    public static void main(String[] args) {
        XmlFileReader xmlFileReader = new XmlFileReader(new GraphXmlReader());
        XmlFileWriter xmlFileWriter = new XmlFileWriter(new GraphXmlWriter());

        //original graphs
        XmlGraph first = xmlFileReader.parse(etcFile("simple.xml"));
        XmlGraph second = xmlFileReader.parse(etcFile("uwm.xml"));

        XmlGraph merged = new XmlGraph(null, null, XmlGraph.NodeType.ELEMENT);
        merged.accept(first);
        merged.accept(second);

        xmlFileWriter.writeToFile(merged, etcFile("merged.xml"));
    }
}
