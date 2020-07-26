package com.legyver.util.graphrunner;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class ContextGraphFactoryTest {
	public static final String JEXL_VARIABLE = "\\$\\{(([a-z\\.-])*)\\}";

	/**
	 * example: build.date.format=`${build.date.day} ${build.date.month} ${build.date.year}`
	 */
	@Test
	public void testRegexForJexlExample() throws Exception {
		Pattern pattern = Pattern.compile(JEXL_VARIABLE);
		Matcher m = pattern.matcher("`${build.date.day} ${build.date.month} ${build.date.year}`");
		List<String> matches = new ArrayList<>();
		while(m.find()) {
			String group = m.group(1);
			matches.add(group);
		}
		assertThat(matches.size(), is(3));
		assertThat(matches, containsInAnyOrder("build.date.day", "build.date.month", "build.date.year"));
	}

	@Test
	public void createSimpleGraph() throws Exception {
		Properties p = new Properties();
		p.setProperty("build.date.format","`${build.date.day} ${build.date.month} ${build.date.year}`");
		p.setProperty("build.date.day","12");
		p.setProperty("build.date.month","April");
		p.setProperty("build.date.year","2020");

		ContextGraphFactory factory = new ContextGraphFactory(Pattern.compile(JEXL_VARIABLE), 1);
		ContextGraph contextGraph = factory.make(PropertyMap.of(p));
		assertThat(contextGraph.graph.size(), is(3));
		assertThat(contextGraph.graph.keySet(), containsInAnyOrder("build.date.day", "build.date.month", "build.date.year"));
		for (String key : contextGraph.graph.keySet()) {
			ContextGraph node = contextGraph.graph.get(key);
			assertThat(node.graph.size(), is(1));
			String depends = node.graph.keySet().iterator().next();
			assertThat(depends, is("build.date.format"));
		}
	}

	@Test
	public void createTwoLevelGraph() throws Exception {
		Properties p = new Properties();
		p.setProperty("build.date.format","`${build.date.day} ${build.date.month} ${build.date.year}`");
		p.setProperty("build.date.day","12");
		p.setProperty("build.date.month","April");
		p.setProperty("build.date.year","2020");

		p.setProperty("build.version.format","`${major.version}.${minor.version}.${patch.number}.${build.number}`");
		p.setProperty("major.version","1");
		p.setProperty("minor.version","0");
		p.setProperty("patch.number","0");
		p.setProperty("build.number","0000");

		p.setProperty("build.message.format","`Build ${build.version}, built on ${build.date}`");

		VariableExtractionOptions extractionOptions = new VariableExtractionOptions(Pattern.compile(JEXL_VARIABLE), 1);
		VariableTransformationRule transformationRule = new VariableTransformationRule(Pattern.compile("\\.format$"),
				TransformationOperation.upToLastIndexOf(".format"));
		ContextGraphFactory factory = new ContextGraphFactory(extractionOptions, transformationRule);
		ContextGraph contextGraph = factory.make(PropertyMap.of(p));
		assertNode(contextGraph, 0);
	}

	private void assertNode(ContextGraph contextGraph, int level) {
		for (String key : contextGraph.graph.keySet()) {
			switch (key) {
				case "build.date.day":
				case "build.date.month":
				case "build.date.year":
					assertThat(level, is(level));
					ContextGraph dateNode = contextGraph.graph.get(key);
					assertThat(dateNode.graph.keySet(), containsInAnyOrder("build.date.format"));
					assertNode(dateNode, level+1);
					break;
				case "major.version":
				case "minor.version":
				case "patch.number":
				case "build.number":
					assertThat(level, is(level));
					ContextGraph versionNode = contextGraph.graph.get(key);
					assertThat(versionNode.graph.keySet(), containsInAnyOrder("build.version.format"));
					assertNode(versionNode, level+1);
					break;
				case "build.date.format":
					assertThat(level, is(level));
					ContextGraph dateFormatNode = contextGraph.graph.get(key);
					assertThat(dateFormatNode.graph.keySet(), containsInAnyOrder("build.date"));
					assertNode(dateFormatNode, level+1);
					break;
				case "build.version.format":
					assertThat(level, is(level));
					ContextGraph versionFormatNode = contextGraph.graph.get(key);
					assertThat(versionFormatNode.graph.keySet(), containsInAnyOrder("build.version"));
					assertNode(versionFormatNode, level+1);
					break;
				case "build.date":
				case "build.version":
					assertThat(level, is(level));
					ContextGraph formatNode = contextGraph.graph.get(key);
					assertThat(formatNode.graph.keySet(), containsInAnyOrder("build.message.format"));
					break;
				case "build.message.format":
					assertThat(level, is(level));
					ContextGraph messageFormatNode = contextGraph.graph.get(key);
					assertThat(messageFormatNode.graph.keySet(), containsInAnyOrder("build.message"));
					break;
				default:
					Assert.fail("Unexpected node: " + key);
			}
		}
	}


}
