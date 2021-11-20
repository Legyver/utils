package com.legyver.utils.propcross;

import com.legyver.utils.graphrunner.PropertyMap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PropertyGraphTest {
	private static PropertyMap propertyMap;

	@BeforeAll
	public static void loadProperties() throws IOException {
		Properties adjectives = loadProperties(PropertyGraphTest.class.getResourceAsStream("adjective.properties"));
		Properties directObject = loadProperties(PropertyGraphTest.class.getResourceAsStream("directobject.properties"));
		Properties nouns = loadProperties(PropertyGraphTest.class.getResourceAsStream("noun.properties"));
		Properties prepositions = loadProperties(PropertyGraphTest.class.getResourceAsStream("preposition.properties"));
		Properties sentence = loadProperties(PropertyGraphTest.class.getResourceAsStream("sentence.properties"));
		Properties subject = loadProperties(PropertyGraphTest.class.getResourceAsStream("subject.properties"));
		Properties verbs = loadProperties(PropertyGraphTest.class.getResourceAsStream("verb.properties"));
		propertyMap = PropertyMap.of(adjectives,
				directObject,
				nouns,
				prepositions,
				sentence,
				subject,
				verbs);
	}

	private static Properties loadProperties(InputStream inputStream) throws IOException {
		Properties properties = new Properties();
		properties.load(inputStream);
		return properties;
	}

	@Test
	public void quickRedFox() throws Exception {
		String sentence = (String) propertyMap.get("sentence");
		assertNull(sentence);

		PropertyGraph propertyGraph = new PropertyGraph(propertyMap);
		propertyGraph.runGraph(new SlelOperationContext(".format"));
		sentence = (String) propertyMap.get("sentence");
		assertEquals("The quick red fox jumped over the lazy brown dog", sentence);
	}
}
