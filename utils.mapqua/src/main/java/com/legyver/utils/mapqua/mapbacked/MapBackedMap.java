package com.legyver.utils.mapqua.mapbacked;

import com.legyver.utils.mapqua.MapQuery;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A Map variable that can be represented as a key-value pair in the POJO internal Map
 */
public class MapBackedMap extends MapBackedProperty<Map> {
	private Map map;

	/**
	 * Construct a MapBackedMap with a certain property name and default value
	 * @param sourceMap Map to store the Map value in
	 * @param property Key name of the property
	 * @param valueIfMissing value to set if missing
	 */
	public MapBackedMap(Map sourceMap, String property, Map valueIfMissing) {
		super(sourceMap, property, valueIfMissing);
	}

	/**
	 * Construct a MapBackedMap with a certain property name.  THe default value will be a LinkedHashMap
	 * @param sourceMap Map to store the Map value in
	 * @param property Key name of the property
	 */
	public MapBackedMap(Map sourceMap, String property) {
		super(sourceMap, property, new LinkedHashMap<>());
	}

	/**
	 * Retrieve the Map value from the POJO internal Map
	 * @return the Map associated with the property name specified in the constructor
	 */
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

	/**
	 * Put a value in the internal map
	 * @param key key to use
	 * @param value value to use
	 */
	public void put(String key, Object value) {
		map.put(key, value);
	}

	/**
	 * Retrieve a value from the internal map
	 * @param key vey name of the value
	 * @return the value associated with the key
	 */
	public Object get(String key) {
		return map.get(key);
	}

	/**
	 * Hook to allow the value to be queryable
	 * @return Optional.empty() as this is not supported
	 */
	@Override
	protected Optional<Map> queryOption() {
		return Optional.empty();
	}

}
