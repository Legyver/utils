package com.legyver.utils.mapqua.mapbacked;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class MapBackedMapTest {

	@Test
	public void addRemovePrimitiveValueList() throws Exception {
		Map rootMap = new LinkedHashMap();
		MapBackedMap mb = new MapBackedMap(rootMap, "field");
		Map mapMap = mb.get();
		assertThat(mapMap.size()).isEqualTo(0);

		mb.put("2012.12", 200);
		mb.put("2013.01", 150);

		assertThat(mb.get("2012.12")).isEqualTo(200);
		assertThat(mb.get("2013.01")).isEqualTo(150);

		assertThat(mapMap.get("2012.12")).isEqualTo(200);
		assertThat(mapMap.get("2013.01")).isEqualTo(150);

		mapMap = (Map) rootMap.get("field");
		assertThat(mapMap.get("2012.12")).isEqualTo(200);
		assertThat(mapMap.get("2013.01")).isEqualTo(150);
	}
}
