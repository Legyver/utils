package com.legyver.utils.mapqua.mapbacked;

import java.util.Collection;
import java.util.Map;

/**
 * For Java Primitives and things like Strings (ie: NOT entities)
 *
 * @param <U> Type of Collection (ie: List, Set)
 * @param <T> Type of component (ie: Java Primitives and things like Strings; NOT entities)
 */
public class MapBackedNativeCollection<U extends Collection<T>, T> extends MapBackedCollection<Collection<T>, T> {

	public MapBackedNativeCollection(Map sourceMap, String property, Collection valueIfMissing) {
		super(sourceMap, property, valueIfMissing);
	}

	public MapBackedNativeCollection(Map sourceMap, String property) {
		super(sourceMap, property);
	}

	@Override
	protected Object toMap(T o) {
		return o;
	}

}
