package com.legyver.utils.mapqua.mapbacked;

import com.legyver.utils.mapqua.MapQuery;
import java.util.Map;
import java.util.Optional;

public class MapBackedInteger extends MapBackedProperty<Integer> {

	public MapBackedInteger(Map sourceMap, String property, Integer valueIfMissing) {
		super(sourceMap, property, valueIfMissing);
	}
	
	public MapBackedInteger(Map sourceMap, String property) {
		this(sourceMap, property, 0);
	}
	
	@Override
	protected Optional<Integer> queryOption() {
		return new MapQuery.Query().integer(property).execute(sourceMap);
	}

}
