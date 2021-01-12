package com.legyver.utils.mapqua.mapbacked;

import com.legyver.utils.mapqua.MapQuery;
import java.util.Map;
import java.util.Optional;

public class MapBackedString extends MapBackedProperty<String> {
	
	public MapBackedString(Map sourceMap, String property, String valueIfMissing) {
		super(sourceMap, property, valueIfMissing);
	}
	
	public MapBackedString(Map sourceMap, String property) {
		this(sourceMap, property, null);
	}

	@Override
	protected Optional<String> queryOption() {
		return new MapQuery.Query().string(property).execute(sourceMap);
	}
	
}
