package com.legyver.utils.mapqua.mapbacked;

import java.util.LinkedHashMap;
import java.util.Map;

import com.legyver.core.exception.CoreException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class MapBackedStringTest extends AbstractJacksonSupportTest {

	@Test
	public void addValue() throws Exception {
		Map map = new LinkedHashMap();
		MapBackedString mb = new MapBackedString(map, "field");
		assertThat(mb.get()).isNull();
		assertThat(map.get("field")).isNull();
		mb.set("my value");
		assertThat(mb.get()).isEqualTo("my value");
	}
	
	@Test
	public void changeValue() throws Exception {
		Map map = new LinkedHashMap();
		MapBackedString mb = new MapBackedString(map, "field");
		mb.set("my value");
		assertThat(mb.get()).isEqualTo("my value");
		mb.set("my new value");
		assertThat(mb.get()).isEqualTo("my new value");
	}
	
	@Test
	public void toJson() throws Exception {
		Map map = new LinkedHashMap();
		MapBackedString mb = new MapBackedString(map, "field");
		mb.set("my value");
		String result = getJson(map);
		assertThat(result).isEqualTo("{\"field\":\"my value\"}");
	}
	
	@Test
	public void entityMap() throws Exception {
		Entity entity = new Entity();
		entity.setString1("first value");
		entity.setString2("second value");
		assertThat(entity.getString1()).isEqualTo("first value");
		assertThat(entity.getString2()).isEqualTo("second value");
	}
	
	@Test
	public void entityToJson() throws Exception {
		Entity entity = new Entity();
		entity.setString1("first value");
		entity.setString2("second value");
		String result = getJson(entity.sourceMap);
		assertThat(result).isEqualTo("{\"field1\":\"first value\",\"field2\":\"second value\"}");
	}
	
	private class Entity {
		private Map sourceMap = new LinkedHashMap();
		private MapBackedString mb1 =  new MapBackedString(sourceMap, "field1");
		private MapBackedString mb2 =  new MapBackedString(sourceMap, "field2");
		
		String getString1() throws CoreException {
			return mb1.get();
		}
		
		String getString2() throws CoreException {
			return mb2.get();
		}
		
		public void setString1(String string) throws CoreException {
			mb1.set(string);
		}
		
		public void setString2(String string) throws CoreException {
			mb2.set(string);
		}
	}
}
