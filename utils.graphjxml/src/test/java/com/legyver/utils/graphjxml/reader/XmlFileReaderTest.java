package com.legyver.utils.graphjxml.reader;

import com.legyver.utils.graphjxml.XmlGraph;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that {@link AbstractInputReader}'s leading-line-terminator handling (via its
 * package-private {@code LineFeedReader}) tolerates XML content that begins with a bare
 * "\n", a bare "\r", or a "\r\n" pair, without corrupting or losing the XML that follows.
 */
public class XmlFileReaderTest {

    @Test
    public void parsesXmlWithLeadingLineFeed(@TempDir Path tempDir) throws Exception {
        assertParses(tempDir, "\n<root><child>value</child></root>");
    }

    @Test
    public void parsesXmlWithLeadingCarriageReturn(@TempDir Path tempDir) throws Exception {
        assertParses(tempDir, "\r<root><child>value</child></root>");
    }

    @Test
    public void parsesXmlWithLeadingCarriageReturnLineFeed(@TempDir Path tempDir) throws Exception {
        assertParses(tempDir, "\r\n<root><child>value</child></root>");
    }

    @Test
    public void parsesXmlWithNoLeadingWhitespace(@TempDir Path tempDir) throws Exception {
        assertParses(tempDir, "<root><child>value</child></root>");
    }

    private void assertParses(Path tempDir, String xml) throws Exception {
        File file = tempDir.resolve("test.xml").toFile();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(xml);
        }

        XmlFileReader xmlFileReader = new XmlFileReader(new GraphXmlReader());
        XmlGraph xmlGraph = xmlFileReader.parse(file);

        assertThat(xmlGraph).isNotNull();
        assertThat(xmlGraph.getName()).isEqualTo("root");
        assertThat(xmlGraph.getChildren()).hasSize(1);
        XmlGraph child = xmlGraph.getChildren().get(0);
        assertThat(child.getName()).isEqualTo("child");
        assertThat(child.getValue()).isEqualTo("value");
    }
}
