package com.legyver.utils.mapqua.mapbacked;

import com.legyver.core.exception.CoreException;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class MapBackedCollectionTest extends AbstractJacksonSupportTest {

	@Test
	public void addRemovePrimitiveValueList() throws CoreException {
		Map map = new LinkedHashMap();
		MapBackedCollection<List<String>, String> mb = new MapBackedNativeCollection(map, "field");
		Collection coll = (Collection) mb.get();
		assertThat(coll.size()).isEqualTo(0);
		List rawValues = (List) map.get("field");
		assertThat(rawValues.size()).isEqualTo(0);
		coll.add("new value");
		mb.set(coll);
		coll = mb.get();
		assertThat(coll.size()).isEqualTo(1);
		assertThat(coll).isInstanceOf(List.class);
		assertThat(((List) coll).get(0)).isEqualTo("new value");
		mb.add("another value");
		coll = mb.get();
		assertThat(coll.size()).isEqualTo(2);
		assertThat(((List) coll).get(1)).isEqualTo("another value");

		mb.remove("another value");
		coll = mb.get();
		assertThat(coll.size()).isEqualTo(1);
		assertThat(((List) coll).get(0)).isEqualTo("new value");

		coll.remove("new value");
		mb.sync();
		coll = mb.get();
		assertThat(coll.size()).isEqualTo(0);
	}

	@Test
	public void addRemoveEntityValueList() throws CoreException {
		Map map = new LinkedHashMap();
		MapBackedCollection<List<Entity>, Entity> mb = new MapBackedEntityCollection(map, "field", m -> new Entity(m));

		Entity entityOne = new Entity();
		entityOne.setNumber(1);
		entityOne.setText("Number One");
		mb.add(entityOne);

		Entity entityTwo = new Entity();
		entityTwo.setNumber(2);
		entityTwo.setText("Number Two");
		mb.get().add(entityTwo);
		mb.sync();

		assertThat(mb.get().size()).isEqualTo(2);

		mb.remove(entityOne);
		Collection<Entity> coll = mb.get();
		assertThat(coll.size()).isEqualTo(1);

		coll.remove(entityTwo);
		mb.sync();

		assertThat(mb.get().size()).isEqualTo(0);
		assertThat(coll.size()).isEqualTo(0);
	}

	@Test
	public void entityListToJson() throws Exception {
		ListEntity listEntity = new ListEntity();
		Entity entity = new Entity();
		entity.setNumber(2);
		entity.setText("number two");
		listEntity.entityList.add(entity);
		String result = getJson(listEntity.getRawMap());
		assertThat(result).isEqualTo("{\"entityList\":[{\"field1\":2,\"field2\":\"number two\"}]}");
	}

	private class ListEntity implements RawMapAware {
		private Map sourceMap = new LinkedHashMap();
		private MapBackedCollection<List<Entity>, Entity> entityList = new MapBackedEntityCollection(sourceMap, "entityList", m -> new Entity(m));

		@Override
		public Map getRawMap() throws CoreException {
			entityList.sync();
			return sourceMap;
		}
	}

	private class Entity implements RawMapAware {
		private Map sourceMap;
		private MapBackedInteger number;
		private MapBackedString text;

		private Entity(Map sourceMap) {
			this.sourceMap = sourceMap;
			number =  new MapBackedInteger(sourceMap, "field1");
			text =  new MapBackedString(sourceMap, "field2");
		}

		private Entity() {
			this(new LinkedHashMap());
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
		public int hashCode() {
			int hash = 3;
			hash = 29 * hash + Objects.hashCode(this.number);
			hash = 29 * hash + Objects.hashCode(this.text);
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final Entity other = (Entity) obj;
			if (!Objects.equals(this.number, other.number)) {
				return false;
			}
			if (!Objects.equals(this.text, other.text)) {
				return false;
			}
			return true;
		}

	}
}
