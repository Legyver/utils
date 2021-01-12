package com.legyver.utils.mapqua.mapbacked;

import com.legyver.utils.mapqua.MapQuery;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class MapBackedMap extends MapBackedProperty<Map> {
	private Map map;

	public MapBackedMap(Map sourceMap, String property, Map valueIfMissing) {
		super(sourceMap, property, valueIfMissing);
	}

	public MapBackedMap(Map sourceMap, String property) {
		super(sourceMap, property, new LinkedHashMap<>());
	}

	@Override
	public Map get() {
		if (map == null) {
			Optional<Object> option = new MapQuery.Query().object(property).execute(sourceMap);
			if (option.isPresent()) {
				map = (Map) option.get();
			} else {
				map = valueIfMissing;
				sourceMap.put(property, map);
			}
		}
		return map;
	}

	public void put(String key, Object value) {
		map.put(key, value);
	}

	public Object get(String key) {
		return map.get(key);
	}

	@Override
	protected Optional<Map> queryOption() {
		return Optional.empty();
	}

}
