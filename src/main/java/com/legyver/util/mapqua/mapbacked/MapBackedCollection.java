package com.legyver.util.mapqua.mapbacked;

import com.legyver.util.mapqua.MapQuery;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MapBackedCollection<U extends Collection<T>, T> extends MapBackedProperty<Collection> {
	private final EntityInstantiator entityInstantiator;
	private U collectionReference;
	
	public MapBackedCollection(Map sourceMap, String property, Collection valueIfMissing, EntityInstantiator entityInstantiator) {
		super(sourceMap, property, valueIfMissing);
		this.entityInstantiator = entityInstantiator;
	}
	
	public MapBackedCollection(Map sourceMap, String property, Collection valueIfMissing) {
		this(sourceMap, property, valueIfMissing, null);
	}
	
	public MapBackedCollection(Map sourceMap, String property, EntityInstantiator entityInstantiator) {
		this(sourceMap, property, new ArrayList<>(), entityInstantiator);
	}
	
	public MapBackedCollection(Map sourceMap, String property) {
		this(sourceMap, property, new ArrayList<>(), null);
	}
	
	public void add(T value) {
		if (collectionReference == null) {
			collectionReference = get();
		}
		collectionReference.add(value);
		sync();
	}
	
	public void remove(T value) {
		if (collectionReference == null) {
			collectionReference = get();
		}
		collectionReference.remove(value);
		sync();
	}
	
	/**
	 * Sync's the collection to the Map
	 * Useful if you are doing operations on the raw collection directly
	 */
	public void sync() {
		set(collectionReference);
	}
	
	@Override
	public U get() {
		return transform(super.get());
	}

	@Override
	protected Optional<Collection> queryOption() {
		return new MapQuery.Query().collection(property).execute(sourceMap);
	}
	
	protected U transform(Collection source) {
		if (source instanceof List) {
			collectionReference = (U) source.stream().map(this::map).collect(Collectors.toList());
		} else {
			collectionReference = (U) source.stream().map(this::map).collect(Collectors.toSet());
		}
		return collectionReference;
	}
	
	private Object map(Object o) {
		Object result = o;
		if (o instanceof Map && entityInstantiator != null) {
			result = entityInstantiator.newInstance((Map) o);
		}
		return result;
	}
	
}
