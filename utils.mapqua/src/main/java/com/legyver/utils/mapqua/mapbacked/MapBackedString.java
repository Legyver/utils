package com.legyver.utils.mapqua.mapbacked;

import com.legyver.core.exception.CoreException;
import com.legyver.utils.mapqua.MapQuery;
import java.util.Map;
import java.util.Optional;

/**
 * A String variable that can be represented as a key-value pair in the POJO internal Map
 */
public class MapBackedString extends MapBackedProperty<String> {

	/**
	 * Construct a MapBackedString with a certain property name and default value
	 * @param sourceMap Map to store the String value in
	 * @param property Key name of the property
	 * @param valueIfMissing value to set if missing
	 */
	public MapBackedString(Map sourceMap, String property, String valueIfMissing) {
		super(sourceMap, property, valueIfMissing);
	}

	/**
	 * Construct a MapBackedString with a certain property name.  The default value will be a new ArrayList.
	 * @param sourceMap Map to store the String value in
	 * @param property Key name of the property
	 */
	public MapBackedString(Map sourceMap, String property) {
		this(sourceMap, property, null);
	}

	/**
	 * Hook that allows the field to be queryable
	 * @return the supported query option
	 */
	@Override
	protected Optional<String> queryOption() throws CoreException {
		return new MapQuery.Query().string(property).execute(sourceMap);
	}
	
}
