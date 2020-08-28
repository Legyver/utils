package com.legyver.util.graphrunner;

import com.legyver.util.graphrunner.ctx.shared.SharedMapCtx;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class PropertyGraphFactoryTest {
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
		PropertyMap propertyMap = PropertyMap.of(p);

		PropertyGraphFactory factory = new PropertyGraphFactory(Pattern.compile(JEXL_VARIABLE), 1);

		Graph contextGraph = factory.make(propertyMap, (s, o) -> new SharedMapCtx(s, propertyMap));
		assertThat(contextGraph.graph.size(), is(3));
		Set<String> keySet = contextGraph.graph.keySet();
		assertThat(keySet, containsInAnyOrder("build.date.day", "build.date.month", "build.date.year"));

		Map<String, Graph.GraphNode> rootGraphNodeChildMap = contextGraph.getChildMap();
		for (String key : rootGraphNodeChildMap.keySet()) {
			Graph.GraphNode node = (Graph.GraphNode) contextGraph.graph.get(key);
			assertThat(node.graph.size(), is(1));
			Map<String, Graph.GraphNode> childGraphNodeChildMap = node.getChildMap();
			String depends = childGraphNodeChildMap.keySet().iterator().next();
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
		PropertyMap propertyMap = PropertyMap.of(p);

		VariableExtractionOptions extractionOptions = new VariableExtractionOptions(Pattern.compile(JEXL_VARIABLE), 1);
		VariableTransformationRule transformationRule = new VariableTransformationRule(Pattern.compile("\\.format$"),
				TransformationOperation.upToLastIndexOf(".format"));
		PropertyGraphFactory factory = new PropertyGraphFactory(extractionOptions, transformationRule);
		Graph contextGraph = factory.make(propertyMap, (s, o) -> new SharedMapCtx(s, propertyMap));
		assertNode(contextGraph, 0);
	}

	private void assertNode(Graph contextGraph, int level) {
		Map<String, Graph> childMap = contextGraph.getChildMap();
		for (String key : childMap.keySet()) {
			switch (key) {
				case "build.date.day":
				case "build.date.month":
				case "build.date.year": { //the brackets are for scoping
					assertThat(level, is(level));
					Graph dateNode = childMap.get(key);
					Map<String, Graph> nodeChildMap = dateNode.getChildMap();
					assertThat(nodeChildMap.keySet(), containsInAnyOrder("build.date.format"));
					assertNode(dateNode, level + 1);
				} break;
				case "major.version":
				case "minor.version":
				case "patch.number":
				case "build.number": {
					assertThat(level, is(level));
					Graph versionNode = childMap.get(key);
					Map<String, Graph> nodeChildMap = versionNode.getChildMap();
					assertThat(nodeChildMap.keySet(), containsInAnyOrder("build.version.format"));
					assertNode(versionNode, level + 1);
				} break;
				case "build.date.format": {
					assertThat(level, is(level));
					Graph dateFormatNode = childMap.get(key);
					Map<String, Graph> nodeChildMap = dateFormatNode.getChildMap();
					assertThat(nodeChildMap.keySet(), containsInAnyOrder("build.date"));
					assertNode(dateFormatNode, level + 1);
				} break;
				case "build.version.format": {
					assertThat(level, is(level));
					Graph versionFormatNode = childMap.get(key);
					Map<String, Graph> nodeChildMap = versionFormatNode.getChildMap();
					assertThat(nodeChildMap.keySet(), containsInAnyOrder("build.version"));
					assertNode(versionFormatNode, level + 1);
				} break;
				case "build.date":
				case "build.version": {
					assertThat(level, is(level));
					Graph formatNode = childMap.get(key);
					Map<String, Graph> nodeChildMap = formatNode.getChildMap();
					assertThat(nodeChildMap.keySet(), containsInAnyOrder("build.message.format"));
				} break;
				case "build.message.format":
					assertThat(level, is(level));
					Graph messageFormatNode = childMap.get(key);
					Map<String, Graph> nodeChildMap = messageFormatNode.getChildMap();
					assertThat(nodeChildMap.keySet(), containsInAnyOrder("build.message"));
					break;
				default:
					Assert.fail("Unexpected node: " + key);
			}
		}
	}


}
