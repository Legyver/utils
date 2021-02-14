package com.legyver.utils.mapqua.mapbacked;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MapBackedIntegerTest {

	@Test
	public void addValue() {
		Map map = new LinkedHashMap();
		MapBackedInteger mb = new MapBackedInteger(map, "field");
		assertThat(mb.get(), is(0));
		assertThat(map.get("field"), is(0));
		mb.set(2);
		assertThat(mb.get(), is(2));
	}
	
	@Test
	public void changeValue() {
		Map map = new LinkedHashMap();
		MapBackedInteger mb = new MapBackedInteger(map, "field");
		mb.set(2);
		assertThat(mb.get(), is(2));
		mb.set(5);
		assertThat(mb.get(), is(5));
	}
	
	@Test
	public void entityMap() {
		Entity entity = new Entity();
		entity.setInteger1(2);
		entity.setInteger2(4);
		assertThat(entity.getInteger1(), is(2));
		assertThat(entity.getInteger2(), is(4));
	}
	
	private class Entity {
		private Map sourceMap = new LinkedHashMap();
		private MapBackedInteger mb1 =  new MapBackedInteger(sourceMap, "field1");
		private MapBackedInteger mb2 =  new MapBackedInteger(sourceMap, "field2");
		
		Integer getInteger1() {
			return mb1.get();
		}
		
		Integer getInteger2() {
			return mb2.get();
		}
		
		public void setInteger1(Integer integer) {
			mb1.set(integer);
		}
		
		public void setInteger2(Integer integer) {
			mb2.set(integer);
		}
	}
}
