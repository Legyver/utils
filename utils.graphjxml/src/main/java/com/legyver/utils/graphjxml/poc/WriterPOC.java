package com.legyver.utils.graphjxml.poc;

import com.legyver.utils.graphjxml.XmlGraph;
import com.legyver.utils.graphjxml.writer.GraphXmlWriter;
import com.legyver.utils.graphjxml.writer.XmlFileWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.legyver.utils.graphjxml.poc.POCContext.etcFile;

/**
 * POC to demonstrate exporting a graph into XML.
 * The exported XML is checked into the /etc directory in the top-level of this git project
 */
public class WriterPOC {

    /**
     * POC demonstrating writing graph to XML
     * @param args ignored
     */
    public static void main(String[] args) {
        XmlFileWriter xmlFileWriter = new XmlFileWriter(new GraphXmlWriter());

        XmlElementBuilder builder = new XmlElementBuilder(null, "root")
                .startElement("course")
                        .startElement("reg_num")
                                .value("10577")
                        .endElement()
                        .startElement("subj")
                            .value("ANTH")
                        .endElement()
                        .startElement("crs")
                            .value("211")
                        .endElement()
                        .startElement("sect")
                            .value("F01")
                        .endElement()
                        .startElement("section")
                            .startElement("instructor")
                                .value("Brightman")
                            .endElement()
                        .endElement()
                .endElement();

        XmlGraph xmlGraph = builder.build(null);
        File file = etcFile("poc.xml");
        xmlFileWriter.writeToFile(xmlGraph,  file);

    }

    private static class XmlElementBuilder {
        private final XmlElementBuilder parentBuilder;
        private final String name;
        private String value;
        private final List<XmlElementBuilder> xmlElementBuilderList = new ArrayList<>();

        private XmlElementBuilder(XmlElementBuilder parentBuilder, String name) {
            this.parentBuilder = parentBuilder;
            this.name = name;
        }

        public XmlElementBuilder startElement(String name) {
            XmlElementBuilder xmlElementBuilder = new XmlElementBuilder(this, name);
            xmlElementBuilderList.add(xmlElementBuilder);
            return xmlElementBuilder;
        }

        public XmlElementBuilder value(String value) {
            this.value = value;
            return this;
        }

        public XmlElementBuilder endElement() {
            return parentBuilder;
        }

        public XmlGraph build(XmlGraph parent) {
            XmlGraph xmlGraph = new XmlGraph(name, parent, XmlGraph.NodeType.ELEMENT);
            xmlGraph.setValue(value);
            for (XmlElementBuilder xmlElementBuilder : xmlElementBuilderList) {
                XmlGraph graph = xmlElementBuilder.build(xmlGraph);
                xmlGraph.accept(graph);
            }
            return xmlGraph;
        }
    }
}
