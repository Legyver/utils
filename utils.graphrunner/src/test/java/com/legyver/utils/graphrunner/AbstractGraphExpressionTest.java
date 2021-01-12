package com.legyver.utils.graphrunner;

import com.legyver.core.exception.CoreException;
import com.legyver.utils.graphrunner.ctx.shared.SharedMapCtx;
import org.junit.Test;

import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public abstract class AbstractGraphExpressionTest {
	private final String testPropertySuffix;
	protected final Pattern varPattern;

	protected AbstractGraphExpressionTest(String testPropertySuffix, Pattern varPattern) {
		this.testPropertySuffix = testPropertySuffix;
		this.varPattern = varPattern;
	}

	protected void evaluateGraph(Map<String, Object> map) throws CoreException {
		VariableExtractionOptions variableExtractionOptions = new VariableExtractionOptions(varPattern, 1);
		VariableTransformationRule variableTransformationRule = new VariableTransformationRule(Pattern.compile("\\.format$"), TransformationOperation.upToLastIndexOf(".format"));
		PropertyGraphFactory factory = new PropertyGraphFactory(variableExtractionOptions, variableTransformationRule);
		Graph<SharedMapCtx> contextGraph = factory.make(map, (s, o) -> new SharedMapCtx(s, map));
		evaluateGraph(contextGraph, variableTransformationRule, map);
	}

	abstract protected void evaluateGraph(Graph<SharedMapCtx> contextGraph, VariableTransformationRule variableTransformationRule, Map<String, Object> map) throws CoreException;

	@Test
	public void evaluateSimpleJexl() throws Exception {
		Properties buildProperties = new Properties();
		buildProperties.load(JexlExpressionGraphTest.class.getResourceAsStream("build.properties"));

		Map<String, Object> map = PropertyMap.of(buildProperties);
		evaluateGraph(map);
		assertExpectedDateProperties(map);
	}

	private void assertExpectedDateProperties(Map<String, Object> map) {
		assertThat(map.get("build.date.day"), is("11"));
		assertThat(map.get("build.date.month"), is("April"));
		assertThat(map.get("build.date.year"), is("2020"));
		assertThat(map.get("build.date" + testPropertySuffix), is("11 April 2020"));
		//putting this here to ensure the format wasn't overwritten
		String expected = "${build.date.day} ${build.date.month} ${build.date.year}";
		if (".jexl".equals(testPropertySuffix)) {
			expected = "`" + expected + "`";
		}
		assertThat(map.get("build.date" + testPropertySuffix + ".format"), is(expected));
	}

	/**
	 * build.properties:
	 *   major.version=1
	 *   minor.version=0
	 *   patch.number=0
	 *
	 *   build.number=0000
	 *   build.date.day=11
	 *   build.date.month=April
	 *   build.date.year=2020
	 *
	 *   build.date.format=`${build.date.day} ${build.date.month} ${build.date.year}`
	 *   build.version.format=`${major.version}.${minor.version}.${patch.number}.${build.number}`
	 *   build.message.format=`Build ${build.version}, built on ${build.date}`
	 * copyright.properties
	 *   copyright.format=`Copyright © Legyver 2020-${build.date.year}.`
	 s	 */
	@Test
	public void evaluateGraphExpression() throws Exception {
		Properties buildProperties = new Properties();
		buildProperties.load(JexlExpressionGraphTest.class.getResourceAsStream("build.properties"));
		Properties copyrightProperties = new Properties();
		copyrightProperties.load(JexlExpressionGraphTest.class.getResourceAsStream("copyright.properties"));

		Map<String, Object> map = PropertyMap.of(buildProperties, copyrightProperties);
		evaluateGraph(map);
		assertExpectedDateProperties(map);
		assertExpectedVersionProperties(map);
		assertExpectedCopyrightProperties(map);
	}

	private void assertExpectedCopyrightProperties(Map<String, Object> map) {
		assertThat(map.get("copyright" + testPropertySuffix), is("Copyright © Legyver 2020-2020."));
		String expected = "Copyright © Legyver 2020-${build.date.year}.";
		if (".jexl".equals(testPropertySuffix)) {
			expected = "`" + expected + "`";
		}
		assertThat(map.get("copyright"+ testPropertySuffix + ".format"), is(expected));
	}

	private void assertExpectedVersionProperties(Map<String, Object> map) {
		assertThat(map.get("major.version"), is("1"));
		assertThat(map.get("minor.version"), is("0"));
		assertThat(map.get("patch.number"), is("0"));
		assertThat(map.get("build.number"), is("0000"));
		assertThat(map.get("build.version" + testPropertySuffix), is("1.0.0.0000"));
		assertThat(map.get("build.message" + testPropertySuffix), is("Build 1.0.0.0000, built on 11 April 2020"));

		//putting these here to ensure the format wasn't overwritten
		String expectedVersionFormat = "${major.version}.${minor.version}.${patch.number}.${build.number}";
		String expectedMessageFormat = "Build ${build.version" + testPropertySuffix + "}, built on ${build.date" + testPropertySuffix + "}";
		if (".jexl".equals(testPropertySuffix)) {
			expectedVersionFormat = "`" + expectedVersionFormat + "`";
			expectedMessageFormat = "`" + expectedMessageFormat + "`";
		}

		assertThat(map.get("build.version" + testPropertySuffix + ".format"), is(expectedVersionFormat));
		assertThat(map.get("build.message" + testPropertySuffix + ".format"), is(expectedMessageFormat));
	}


}
