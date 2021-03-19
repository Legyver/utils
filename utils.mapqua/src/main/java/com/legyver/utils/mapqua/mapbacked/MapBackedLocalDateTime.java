package com.legyver.utils.mapqua.mapbacked;

import com.legyver.core.exception.CoreException;
import com.legyver.utils.mapqua.MapQuery;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * A LocalDateTime variable that can be represented as a key-value pair in the POJO internal Map
 */
public class MapBackedLocalDateTime extends MapBackedProperty<LocalDateTime> {

	/**
	 * Construct a MapBackedLocalDateTime with a certain property name and default value
	 * @param sourceMap Map to store the LocalDateTime value in
	 * @param property Key name of the property
	 * @param valueIfMissing value to set if missing
	 */
	public MapBackedLocalDateTime(Map sourceMap, String property, LocalDateTime valueIfMissing) {
		super(sourceMap, property, valueIfMissing);
	}

	/**
	 * Construct a MapBackedLocalDateTime with a certain property name.  Uses the current LocalDateTime as the default value
	 * @param sourceMap Map to store the LocalDateTime value in
	 * @param property Key name of the property
	 */
	public MapBackedLocalDateTime(Map sourceMap, String property) {
		this(sourceMap, property, LocalDateTime.now());
	}

	/**
	 * Hook to allow the LocalDateTime to be queryable
	 * @return the query option to use
	 */
	@Override
	protected Optional<LocalDateTime> queryOption() throws CoreException {
		return new MapQuery.Query().localDateTime(property).execute(sourceMap);
	}

}
