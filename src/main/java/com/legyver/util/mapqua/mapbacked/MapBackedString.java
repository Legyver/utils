package com.legyver.util.mapqua.mapbacked;

import com.legyver.util.mapqua.MapQuery;
import java.util.Map;
import java.util.Optional;

public class MapBackedString extends MapBackedProperty<String> {
	private static final String EMPTY = "";
	
	public MapBackedString(Map sourceMap, String property, String valueIfMissing) {
		super(sourceMap, property, valueIfMissing);
	}
	
	public MapBackedString(Map sourceMap, String property) {
		this(sourceMap, property, EMPTY);
	}

	@Override
	protected Optional<String> queryOption() {
		return new MapQuery.Query().string(property).execute(sourceMap);
	}
	
}
