package com.legyver.util.mapqua;

import java.util.HashMap;
import java.util.Map;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Test;

import static org.junit.Assert.assertThat;

public class MapMergeTest {

	@Test
	public void mapMergeAdd() throws Exception {
		Map map = new HashMap<>();
		Map map2 = new HashMap<>();
		map2.put("value", 1);
		runMerge(map2, map);
		
		Object value = map.get("value");
		assertThat(value, is(1));
	}
	
	@Test 
	public void nestedMapMergeAdd() throws Exception {
		Map map = new HashMap<>();
		Map map2 = new HashMap<>();
		Map childMap1 = new HashMap<>();
		childMap1.put("a value", 1);
		Map childMap2 = new HashMap<>();
		childMap2.put("another value", 3);
		map.put("child", childMap1);
		map2.put("child", childMap2);
		
		runMerge(map2, map);
		Map result = (Map) map.get("child");
		assertThat(result.get("a value"), is(1));
		assertThat(result.get("another value"), is(3));
	}

	private void runMerge(Map map2, Map map) {
		for (Object key: map2.keySet()) {
			Object newValue = map2.get(key);
			map.merge(key, newValue, new MapMerge());
		}
	}
		

}
