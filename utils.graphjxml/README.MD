# utils-graphjxml
A Java Graph to XML conversion utility
```groovy
implementation group: 'com.legyver', name: 'utils-graphjxml', version: '3.0.0-alpha.1'
```

## Features
### Parse XML into a graph
```java
File file = new File("simple.xml");

XmlFileReader xmlFileReader = new XmlFileReader(new GraphXmlReader());
XmlGraph xmlGraph = xmlFileReader.parse(file);
```
Check out the [demo](src/main/java/com/legyver/utils/graphjxml/poc/ReaderPOC.java)

### Export a directed graph as XML
```java
XmlGraph xmlGraph = createGraph();

XmlFileWriter xmlFileWriter = new XmlFileWriter(new GraphXmlWriter());
File file = etcFile("poc.xml");
xmlFileWriter.writeToFile(xmlGraph,  file);
```
Check out the [demo](src/main/java/com/legyver/utils/graphjxml/poc/WriterPOC.java)

### Merge multiple XML files into a single graph
```java
XmlGraph first = xmlFileReader.parse(etcFile("simple.xml"));
XmlGraph second = xmlFileReader.parse(etcFile("uwm.xml"));

XmlGraph merged = new XmlGraph(null, null, XmlGraph.NodeType.ELEMENT);
merged.accept(first);
merged.accept(second);
```
Check out the [demo](src/main/java/com/legyver/utils/graphjxml/poc/MergePOC.java)
