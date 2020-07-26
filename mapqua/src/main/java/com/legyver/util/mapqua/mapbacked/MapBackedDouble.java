package com.legyver.util.mapqua.mapbacked;

import com.legyver.util.mapqua.MapQuery;
import java.util.Map;
import java.util.Optional;

public class MapBackedDouble extends MapBackedProperty<Double> {

	public MapBackedDouble(Map sourceMap, String property, Double valueIfMissing) {
		super(sourceMap, property, valueIfMissing);
	}
	
	public MapBackedDouble(Map sourceMap, String property) {
		this(sourceMap, property, 0.0);
	}

	@Override
	protected Optional<Double> queryOption() {
		return new MapQuery.Query().floatingPoint(property).execute(sourceMap);
	}

}
