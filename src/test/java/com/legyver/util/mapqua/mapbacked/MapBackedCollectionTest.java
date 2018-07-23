package com.legyver.util.mapqua.mapbacked;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class MapBackedCollectionTest {
	@Test
	public void addRemovePrimitiveValueList() {
		Map map = new LinkedHashMap();
		MapBackedCollection<List, String> mb = new MapBackedCollection(map, "field");
		Collection coll = (Collection) mb.get();
		assertThat(coll.size(), is(0));
		List rawValues = (List) map.get("field");
		assertThat(rawValues.size(), is(0));
		coll.add("new value");
		mb.set(coll);
		coll = mb.get();
		assertThat(coll.size(), is(1));
		assertThat(coll instanceof List, is(true));
		assertThat(((List) coll).get(0), is ("new value"));
		mb.add("another value");
		coll = mb.get();
		assertThat(coll.size(), is(2));
		assertThat(((List) coll).get(1), is ("another value"));
		
		mb.remove("another value");
		coll = mb.get();
		assertThat(coll.size(), is(1));
		assertThat(((List) coll).get(0), is ("new value"));
		
		coll.remove("new value");
		mb.sync();
		coll = mb.get();
		assertThat(coll.size(), is(0));
	}
	
	@Test
	public void addRemoveEntityValueList() {
		Map map = new LinkedHashMap(); 
		MapBackedCollection<List, Entity> mb = new MapBackedCollection(map, "field");
		
		Entity entityOne = new Entity();
		entityOne.setNumber(1);
		entityOne.setText("Number One");
		mb.add(entityOne);
		
		Entity entityTwo = new Entity();
		entityOne.setNumber(2);
		entityOne.setText("Number Two");
		mb.get().add(entityTwo);
		mb.sync();
		
		assertThat(mb.get().size(), is(2));
		
		mb.remove(entityOne);
		Collection<Entity> coll = mb.get();
		assertThat(coll.size(), is(1));
		
		coll.remove(entityTwo);
		mb.sync();
		
		assertThat(mb.get().size(), is(0));
		assertThat(coll.size(), is(0));
	}
	
	private class Entity {
		private Map sourceMap = new LinkedHashMap();
		private MapBackedInteger number =  new MapBackedInteger(sourceMap, "field1");
		private MapBackedString text =  new MapBackedString(sourceMap, "field2");
		
		Integer getNumber() {
			return number.get();
		}
		
		String getString() {
			return text.get();
		}
		
		public void setNumber(Integer integer) {
			number.set(integer);
		}
		
		public void setText(String text) {
			this.text.set(text);
		}
	}
}
