package com.legyver.util.graphrunner;

import com.legyver.core.exception.CoreException;
import org.apache.commons.jexl3.*;
import org.junit.Test;

import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Including this test as a POC.
 * jexl3 is a testCompile to avoid introducing a compile dependency on jexl3
 */
public class JexlExpressionGraphTest {
	public static final String JEXL_VARIABLE = "\\$\\{(([a-z\\.-])*)\\}";

	/**
	 * example:
	 *   build.date.day=11
	 *   build.date.month=April
	 *   build.date.year=2020
	 *
	 *   build.date.format=`${build.date.day} ${build.date.month} ${build.date.year}`
	 */
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
		assertThat(map.get("build.date"), is("11 April 2020"));
		//putting this here to ensure the format wasn't overwritten
		assertThat(map.get("build.date.format"), is("`${build.date.day} ${build.date.month} ${build.date.year}`"));
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
	public void evaluateGraphJexl() throws Exception {
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
		assertThat(map.get("copyright"), is("Copyright © Legyver 2020-2020."));
		assertThat(map.get("copyright.format"), is("`Copyright © Legyver 2020-${build.date.year}.`"));
	}

	private void assertExpectedVersionProperties(Map<String, Object> map) {
		assertThat(map.get("major.version"), is("1"));
		assertThat(map.get("minor.version"), is("0"));
		assertThat(map.get("patch.number"), is("0"));
		assertThat(map.get("build.number"), is("0000"));
		assertThat(map.get("build.version"), is("1.0.0.0000"));
		assertThat(map.get("build.message"), is("Build 1.0.0.0000, built on 11 April 2020"));

		//putting these here to ensure the format wasn't overwritten
		assertThat(map.get("build.version.format"), is("`${major.version}.${minor.version}.${patch.number}.${build.number}`"));
		assertThat(map.get("build.message.format"), is("`Build ${build.version}, built on ${build.date}`"));
	}

	private void evaluateGraph(Map<String, Object> map) throws CoreException {
		Pattern jexlVar = Pattern.compile(JEXL_VARIABLE);

		VariableExtractionOptions variableExtractionOptions = new VariableExtractionOptions(jexlVar, 1);
		VariableTransformationRule variableTransformationRule = new VariableTransformationRule(Pattern.compile("\\.format$"), TransformationOperation.upToLastIndexOf(".format"));
		PropertyGraphFactory factory = new PropertyGraphFactory(variableExtractionOptions, variableTransformationRule);
		Graph contextGraph = factory.make(map, (s, o) -> new SharedMapCtx(s, map));

		JexlEngine jexl = new JexlBuilder().create();
		JexlContext context = new MapContext(map);

		contextGraph.executeStrategy((nodeName, currentValue) -> {
			try {
				if (currentValue instanceof SharedMapCtx) {
					SharedMapCtx keyValue = (SharedMapCtx) currentValue;
					if (keyValue.getValue() != null) {
						Matcher m = jexlVar.matcher((CharSequence) keyValue.getValue());
						if (m.find()) {
							JexlExpression expression = jexl.createExpression((String) keyValue.getValue());
							String value = (String) expression.evaluate(context);
							//update the map with the value
							String key = nodeName;
							//avoid overwriting the .format property
							if (variableTransformationRule.matches(nodeName)) {
								key = variableTransformationRule.transform(nodeName);
							}
							map.put(key, value);
						}
					}
				}
			} catch (RuntimeException ex) {
				throw new CoreException("Error evaluating expression: " + currentValue, ex);
			}
		});
	}
}
