package com.legyver.utils.mapqua.mapbacked;

import com.legyver.utils.mapqua.MapQuery;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

public class MapBackedLocalDateTime extends MapBackedProperty<LocalDateTime> {

	public MapBackedLocalDateTime(Map sourceMap, String property, LocalDateTime valueIfMissing) {
		super(sourceMap, property, valueIfMissing);
	}
	
	public MapBackedLocalDateTime(Map sourceMap, String property) {
		this(sourceMap, property, LocalDateTime.now());
	}

	@Override
	protected Optional<LocalDateTime> queryOption() {
		return new MapQuery.Query().localDateTime(property).execute(sourceMap);
	}

}
