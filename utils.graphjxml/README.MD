# utils-graphjxml
A Java Graph to XML conversion utility
```gradle
implementation group: 'com.legyver', name: 'utils-graphjxml', version: '2.1.1.0'
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