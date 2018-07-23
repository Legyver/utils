package com.legyver.util.mapqua.mapbacked;

import com.legyver.util.mapqua.MapQuery;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MapBackedCollection<U extends Collection, T> extends MapBackedProperty<Collection> {
	private final EntityInstantiator entityInstantiator;
	private Collection collectionReference;
	
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
	protected Optional<Collection> queryOption() {
		return new MapQuery.Query().collection(property).execute(sourceMap);
	}
	
	@Override
	protected Collection transform(Collection source) {
		Stream<T> stream = source.stream().map(this::map);
		collectionReference =  source instanceof List ? stream.collect(Collectors.toList())
				: stream.collect(Collectors.toSet());
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
