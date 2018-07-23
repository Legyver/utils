package com.legyver.util.mapqua.mapbacked;

import com.legyver.util.mapqua.MapQuery;
import java.util.Map;
import java.util.Optional;

public abstract class MapBackedProperty<T> {
	protected final Map sourceMap;
	protected final String property;
	private final T valueIfMissing;

	public MapBackedProperty(Map sourceMap, String property, T valueIfMissing) {
		this.sourceMap = sourceMap;
		this.property = property;
		this.valueIfMissing = valueIfMissing;
	}
	
	public T get() {
		Optional<T> option = queryOption();
		T value;
		if (option.isPresent()) {
			value = transform(option.get());
		} else {
			value = valueIfMissing;
			set(value);
		}
		return value;
	}
	
	public void set(T value) {
		new MapQuery.Query().set(property, value).execute(sourceMap);
	}
	
	protected T transform(T source) {
		return source;
	}
	
	protected abstract Optional<T> queryOption();
	
}
