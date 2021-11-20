package com.legyver.utils.mapqua.mapbacked;

import java.util.LinkedHashMap;
import java.util.Map;

import com.legyver.core.exception.CoreException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class MapBackedIntegerTest {

	@Test
	public void addValue() throws Exception {
		Map map = new LinkedHashMap();
		MapBackedInteger mb = new MapBackedInteger(map, "field");
		assertThat(mb.get()).isEqualTo(0);
		assertThat(map.get("field")).isEqualTo(0);
		mb.set(2);
		assertThat(mb.get()).isEqualTo(2);
	}
	
	@Test
	public void changeValue() throws Exception {
		Map map = new LinkedHashMap();
		MapBackedInteger mb = new MapBackedInteger(map, "field");
		mb.set(2);
		assertThat(mb.get()).isEqualTo(2);
		mb.set(5);
		assertThat(mb.get()).isEqualTo(5);
	}
	
	@Test
	public void entityMap() throws Exception {
		Entity entity = new Entity();
		entity.setInteger1(2);
		entity.setInteger2(4);
		assertThat(entity.getInteger1()).isEqualTo(2);
		assertThat(entity.getInteger2()).isEqualTo(4);
	}
	
	private class Entity {
		private Map sourceMap = new LinkedHashMap();
		private MapBackedInteger mb1 =  new MapBackedInteger(sourceMap, "field1");
		private MapBackedInteger mb2 =  new MapBackedInteger(sourceMap, "field2");
		
		Integer getInteger1() throws CoreException {
			return mb1.get();
		}
		
		Integer getInteger2() throws CoreException {
			return mb2.get();
		}
		
		public void setInteger1(Integer integer) throws CoreException {
			mb1.set(integer);
		}
		
		public void setInteger2(Integer integer) throws CoreException {
			mb2.set(integer);
		}
	}
}
