package com.legyver.utils.mapqua.mapbacked;

import com.legyver.core.exception.CoreException;
import com.legyver.utils.mapqua.MapQuery;
import java.util.Map;
import java.util.Optional;

/**
 * A Double variable that can be represented as a key-value pair in the POJO internal Map
 */
public class MapBackedDouble extends MapBackedProperty<Double> {

	/**
	 * Construct a MapBackedDouble with a certain property name and default value
	 * @param sourceMap Map to store the Double value in
	 * @param property Key name of the property
	 * @param valueIfMissing value to set if missing
	 */
	public MapBackedDouble(Map sourceMap, String property, Double valueIfMissing) {
		super(sourceMap, property, valueIfMissing);
	}

	/**
	 * Construct a MapBackedDouble with a certain property name and default value of 0.0
	 * @param sourceMap Map to store the Double value in
	 * @param property Key name of the property
	 */
	public MapBackedDouble(Map sourceMap, String property) {
		this(sourceMap, property, 0.0);
	}

	@Override
	protected Optional<Double> queryOption() throws CoreException {
		return new MapQuery.Query().floatingPoint(property).execute(sourceMap);
	}

}
