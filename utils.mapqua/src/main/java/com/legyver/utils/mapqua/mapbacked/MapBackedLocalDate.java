package com.legyver.utils.mapqua.mapbacked;

import com.legyver.utils.mapqua.MapQuery;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

/**
 * A LocalDate variable that can be represented as a key-value pair in the POJO internal Map
 */
public class MapBackedLocalDate extends MapBackedProperty<LocalDate> {

	/**
	 * Construct a MapBackedLocalDate with a certain property name and default value
	 * @param sourceMap Map to store the LocalDate value in
	 * @param property Key name of the property
	 * @param valueIfMissing value to set if missing
	 */
	public MapBackedLocalDate(Map sourceMap, String property, LocalDate valueIfMissing) {
		super(sourceMap, property, valueIfMissing);
	}

	/**
	 * Construct a MapBackedLocalDate with a certain property name. The default value will be the current local date.
	 * @param sourceMap Map to store the LocalDate value in
	 * @param property Key name of the property
	 */
	public MapBackedLocalDate(Map sourceMap, String property) {
		this(sourceMap, property, LocalDate.now());
	}

	@Override
	protected Optional<LocalDate> queryOption() {
		return new MapQuery.Query().localDate(property).execute(sourceMap);
	}

}
