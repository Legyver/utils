package com.legyver.utils.mapqua.mapbacked;

import com.google.gson.Gson;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class MapBackedStringTest {

	@Test
	public void addValue() {
		Map map = new LinkedHashMap();
		MapBackedString mb = new MapBackedString(map, "field");
		assertThat(mb.get(), is(nullValue()));
		assertThat(map.get("field"), is(nullValue()));
		mb.set("my value");
		assertThat(mb.get(), is("my value"));
	}
	
	@Test
	public void changeValue() {
		Map map = new LinkedHashMap();
		MapBackedString mb = new MapBackedString(map, "field");
		mb.set("my value");
		assertThat(mb.get(), is("my value"));
		mb.set("my new value");
		assertThat(mb.get(), is("my new value"));
	}
	
	@Test
	public void toJson() throws Exception {
		Map map = new LinkedHashMap();
		MapBackedString mb = new MapBackedString(map, "field");
		mb.set("my value");
		String result = new Gson().toJson(map);
		assertThat(result, is("{\"field\":\"my value\"}"));
	}
	
	@Test
	public void entityMap() {
		Entity entity = new Entity();
		entity.setString1("first value");
		entity.setString2("second value");
		assertThat(entity.getString1(), is("first value"));
		assertThat(entity.getString2(), is("second value"));
	}
	
	@Test
	public void entityToJson() {
		Entity entity = new Entity();
		entity.setString1("first value");
		entity.setString2("second value");
		String result = new Gson().toJson(entity.sourceMap);
		assertThat(result, is("{\"field1\":\"first value\",\"field2\":\"second value\"}"));
	}
	
	private class Entity {
		private Map sourceMap = new LinkedHashMap();
		private MapBackedString mb1 =  new MapBackedString(sourceMap, "field1");
		private MapBackedString mb2 =  new MapBackedString(sourceMap, "field2");
		
		String getString1() {
			return mb1.get();
		}
		
		String getString2() {
			return mb2.get();
		}
		
		public void setString1(String string) {
			mb1.set(string);
		}
		
		public void setString2(String string) {
			mb2.set(string);
		}
	}
}
