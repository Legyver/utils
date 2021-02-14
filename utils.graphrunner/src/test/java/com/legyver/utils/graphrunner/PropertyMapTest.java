package com.legyver.utils.graphrunner;

import org.junit.Test;

import java.util.Map;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PropertyMapTest {

	@Test
	public void mixedPropertiesConvert() throws Exception {
		Properties p = new Properties();
		p.load(PropertyMapTest.class.getResourceAsStream("PropertyMapTest.properties"));

		assertThat(p.get("an.integer"), is("1"));
		assertThat(p.get("a.real.number"), is("1.25"));
		assertThat(p.get("a.string"), is("blah"));
		assertThat(p.get("a.date"), is("11 April 2020"));
		assertThat(p.get("a.boolean"), is("true"));

		Map<String, Object> propertyMap = PropertyMap.of(p);
		assertThat(propertyMap.get("an.integer"), is("1"));
		assertThat(propertyMap.get("a.real.number"), is("1.25"));
		assertThat(propertyMap.get("a.string"), is("blah"));
		assertThat(propertyMap.get("a.date"), is("11 April 2020"));
		assertThat(propertyMap.get("a.boolean"), is("true"));
	}
}
