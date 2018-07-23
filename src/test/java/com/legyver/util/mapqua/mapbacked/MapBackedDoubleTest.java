package com.legyver.util.mapqua.mapbacked;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MapBackedDoubleTest {

	@Test
	public void addValue() {
		Map map = new LinkedHashMap();
		MapBackedDouble mb = new MapBackedDouble(map, "field");
		assertThat(mb.get(), is(0.0));
		assertThat(map.get("field"), is(0.0));
		mb.set(2.0);
		assertThat(mb.get(), is(2.0));
	}
	
	@Test
	public void changeValue() {
		Map map = new LinkedHashMap();
		MapBackedDouble mb = new MapBackedDouble(map, "field");
		mb.set(2.0);
		assertThat(mb.get(), is(2.0));
		mb.set(2.3);
		assertThat(mb.get(), is(2.3));
	}
	
	@Test
	public void entityMap() {
		Entity entity = new Entity();
		entity.setDouble1(1.1);
		entity.setDouble2(1.2);
		assertThat(entity.getDouble1(), is(1.1));
		assertThat(entity.getDouble2(), is(1.2));
	}
	
	private class Entity {
		private Map sourceMap = new LinkedHashMap();
		private MapBackedDouble mb1 =  new MapBackedDouble(sourceMap, "field1");
		private MapBackedDouble mb2 =  new MapBackedDouble(sourceMap, "field2");
		
		Double getDouble1() {
			return mb1.get();
		}
		
		Double getDouble2() {
			return mb2.get();
		}
		
		public void setDouble1(Double integer) {
			mb1.set(integer);
		}
		
		public void setDouble2(Double integer) {
			mb2.set(integer);
		}
	}
}
