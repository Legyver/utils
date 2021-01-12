package com.legyver.utils.mapqua.mapbacked;

import com.legyver.utils.mapqua.MapQuery;
import java.util.Map;
import java.util.Optional;

public abstract class MapBackedProperty<T> {
	protected final Map sourceMap;
	protected final String property;
	protected final T valueIfMissing;

	public MapBackedProperty(Map sourceMap, String property, T valueIfMissing) {
		this.sourceMap = sourceMap;
		this.property = property;
		this.valueIfMissing = valueIfMissing;
	}
	
	public T get() {
		Optional<T> option = queryOption();
		T value;
		if (option.isPresent()) {
			value = option.get();
		} else {
			value = valueIfMissing;
			set(value);
		}
		return value;
	}
	
	public void set(T value) {
		if (value == null) {
			sourceMap.remove(property);
		} else {
			new MapQuery.Query().set(property, value).execute(sourceMap);
		}
	}
	
	protected abstract Optional<T> queryOption();
	
}
