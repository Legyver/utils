package com.legyver.utils.mapqua.mapbacked;

import com.legyver.utils.mapqua.MapQuery;
import java.util.Map;
import java.util.Optional;

/**
 * An Integer variable that can be represented as a key-value pair in the POJO internal Map
 */
public class MapBackedInteger extends MapBackedProperty<Integer> {

	/**
	 * Construct a MapBackedInteger with a certain property name and default value
	 * @param sourceMap Map to store the Integer value in
	 * @param property Key name of the property
	 * @param valueIfMissing value to set if missing
	 */
	public MapBackedInteger(Map sourceMap, String property, Integer valueIfMissing) {
		super(sourceMap, property, valueIfMissing);
	}

	/**
	 * Construct a MapBackedInteger with a certain property name. The default value will be 0.
	 * @param sourceMap Map to store the Integer value in
	 * @param property Key name of the property
	 */
	public MapBackedInteger(Map sourceMap, String property) {
		this(sourceMap, property, 0);
	}

	/**
	 * Hook to allow the Integer to be queryable
	 * @return the query option to use for the Integer
	 */
	@Override
	protected Optional<Integer> queryOption() {
		return new MapQuery.Query().integer(property).execute(sourceMap);
	}

}
