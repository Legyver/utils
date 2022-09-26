package com.legyver.utils.graphrunner;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Convert properties into a Map
 */
public class PropertyMap implements Map<String, Object> {
	private final Map<String, Object> internalMap = new HashMap<>();

	@Override
	public int size() {
		return internalMap.size();
	}

	@Override
	public boolean isEmpty() {
		return internalMap.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return internalMap.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return internalMap.containsValue(value);
	}

	@Override
	public Object get(Object key) {
		return internalMap.get(key);
	}

	@Override
	public Object put(String key, Object value) {
		return internalMap.put(key, value);
	}

	@Override
	public Object remove(Object key) {
		return internalMap.remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ?> m) {
		internalMap.putAll(m);
	}

	@Override
	public void clear() {
		internalMap.clear();
	}

	@Override
	public Set<String> keySet() {
		return internalMap.keySet();
	}

	@Override
	public Collection<Object> values() {
		return internalMap.values();
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		return internalMap.entrySet();
	}

	/**
	 * Utility method to create a PropertyMap from one or more {@link java.util.Properties} files
	 * @param properties to merge into a common Map
	 * @return the resultant PropertyMap
	 */
	public static PropertyMap of(Properties...properties) {
		PropertyMap propertyMap = new PropertyMap();
		if (properties != null) {
			for (Properties p: properties)
				p.stringPropertyNames().forEach(s -> propertyMap.internalMap.put(s, p.get(s)));
		}
		return propertyMap;
	}
}
