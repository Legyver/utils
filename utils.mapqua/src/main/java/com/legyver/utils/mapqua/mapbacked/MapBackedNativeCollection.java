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

	/**
	 * Construct a MapBackedNativeCollection with a certain property name and default value
	 * @param sourceMap Map to store the Collection in
	 * @param property Key name of the property
	 * @param valueIfMissing value to set if missing
	 */
	public MapBackedNativeCollection(Map sourceMap, String property, Collection valueIfMissing) {
		super(sourceMap, property, valueIfMissing);
	}

	/**
	 * Construct a MapBackedNativeCollection with a certain property name
	 * @param sourceMap Map to store the Collection in
	 * @param property Key name of the property
	 */
	public MapBackedNativeCollection(Map sourceMap, String property) {
		super(sourceMap, property);
	}

	@Override
	protected Object toMap(T o) {
		return o;
	}

}
