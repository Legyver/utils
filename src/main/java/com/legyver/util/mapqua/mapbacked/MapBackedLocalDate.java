package com.legyver.util.mapqua.mapbacked;

import com.legyver.util.mapqua.MapQuery;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

public class MapBackedLocalDate extends MapBackedProperty<LocalDate> {

	public MapBackedLocalDate(Map sourceMap, String property, LocalDate valueIfMissing) {
		super(sourceMap, property, valueIfMissing);
	}
	
	public MapBackedLocalDate(Map sourceMap, String property) {
		this(sourceMap, property, LocalDate.now());
	}

	@Override
	protected Optional<LocalDate> queryOption() {
		return new MapQuery.Query().localDate(property).execute(sourceMap);
	}

}
