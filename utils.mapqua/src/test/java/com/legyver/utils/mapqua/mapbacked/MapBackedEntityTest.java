package com.legyver.utils.mapqua.mapbacked;

import java.util.LinkedHashMap;
import java.util.Map;

import com.legyver.core.exception.CoreException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MapBackedEntityTest {

	@Test
	public void entityMap() throws Exception {
		Map rootMap = new LinkedHashMap();
		OuterEntity outerEntity = new OuterEntity(rootMap);
		InnerEntity innerEntity = outerEntity.entity.get();
		assertNotNull(innerEntity);
		assertThat(innerEntity.getNumber()).isEqualTo(0);
		assertNull(innerEntity.getString());

		innerEntity.setNumber(1);
		innerEntity.setText("One");
		assertThat(innerEntity.getNumber()).isEqualTo(1);
		assertThat(innerEntity.getString()).isEqualTo("One");
		{
			Map innerMap = innerEntity.getRawMap();
			assertNotNull(innerMap);
			assertThat(innerMap.get("field1")).isEqualTo(1);
			assertThat(innerMap.get("field2")).isEqualTo("One");
		}
		Map outerMap = outerEntity.getRawMap();
		{
			Map innerMap = (Map) outerMap.get("entity");
			assertNotNull(innerMap);
			assertThat(innerMap.get("field1")).isEqualTo(1);
			assertThat(innerMap.get("field2")).isEqualTo("One");
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

		Integer getNumber() throws CoreException {
			return number.get();
		}

		String getString() throws CoreException {
			return text.get();
		}

		public void setNumber(Integer integer) throws CoreException {
			number.set(integer);
		}

		public void setText(String text) throws CoreException {
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
