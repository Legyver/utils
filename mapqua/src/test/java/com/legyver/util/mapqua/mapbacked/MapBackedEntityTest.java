package com.legyver.util.mapqua.mapbacked;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class MapBackedEntityTest {

	@Test
	public void entityMap() {
		Map rootMap = new LinkedHashMap();
		OuterEntity outerEntity = new OuterEntity(rootMap);
		InnerEntity innerEntity = outerEntity.entity.get();
		assertNotNull(innerEntity);
		assertThat(innerEntity.getNumber(), is(0));
		assertNull(innerEntity.getString());

		innerEntity.setNumber(1);
		innerEntity.setText("One");
		assertThat(innerEntity.getNumber(), is(1));
		assertThat(innerEntity.getString(), is("One"));
		{
			Map innerMap = innerEntity.getRawMap();
			assertNotNull(innerMap);
			assertThat(innerMap.get("field1"), is(1));
			assertThat(innerMap.get("field2"), is("One"));
		}
		Map outerMap = outerEntity.getRawMap();
		{
			Map innerMap = (Map) outerMap.get("entity");
			assertNotNull(innerMap);
			assertThat(innerMap.get("field1"), is(1));
			assertThat(innerMap.get("field2"), is("One"));
		}
	}

	private class OuterEntity implements RawMapAware {
		private final Map sourceMap;
		private final MapBackedEntity<InnerEntity> entity;

		public OuterEntity(Map sourceMap) {
			this.sourceMap = sourceMap;
			entity = new MapBackedEntity(sourceMap, "entity", map -> new InnerEntity(map));
		}

		@Override
		public Map getRawMap() {
			return sourceMap;
		}
	}

	private class InnerEntity implements MapSyncable {
		private final Map sourceMap;
		private final MapBackedInteger number;
		private final MapBackedString text;

		public InnerEntity(Map map) {
			sourceMap = map;
			number =  new MapBackedInteger(sourceMap, "field1");
			text =  new MapBackedString(sourceMap, "field2");
		}

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

		@Override
		public Map getRawMap() {
			return sourceMap;
		}

		@Override
		public void sync() {
			//noop.
		}
	}
}
