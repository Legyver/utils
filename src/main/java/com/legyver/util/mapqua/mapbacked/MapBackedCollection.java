package com.legyver.util.mapqua.mapbacked;

import com.legyver.util.mapqua.MapQuery;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class MapBackedCollection<U extends Collection<T>, T> extends MapBackedProperty<Collection> {
	private final EntityInstantiator entityInstantiator;
	private U collectionReference;

	protected MapBackedCollection(Map sourceMap, String property, Collection valueIfMissing, EntityInstantiator entityInstantiator) {
		super(sourceMap, property, valueIfMissing);
		this.entityInstantiator = entityInstantiator;
	}

	protected MapBackedCollection(Map sourceMap, String property, Collection valueIfMissing) {
		this(sourceMap, property, valueIfMissing, null);
	}

	protected MapBackedCollection(Map sourceMap, String property, EntityInstantiator entityInstantiator) {
		this(sourceMap, property, new ArrayList<>(), entityInstantiator);
	}

	protected MapBackedCollection(Map sourceMap, String property) {
		this(sourceMap, property, new ArrayList<>(), null);
	}

	public void add(T value) {
		get().add(value);
	}

	public void remove(T value) {
		get().remove(value);
	}

	/**
	 * Sync's the collection to the Map
	 */
	public void sync() {
		set(deTransform(collectionReference));
	}

	@Override
	public U get() {
		if (collectionReference == null) {
			collectionReference = transform(super.get());
		}
		return collectionReference;
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

	protected Collection deTransform(Collection<T> source) {
		Collection result;
		if (source instanceof List) {
			result = (Collection) source.stream().map(this::toMap).collect(Collectors.toList());
		} else {
			result = (Collection) source.stream().map(this::toMap).collect(Collectors.toSet());
		}
		return result;
	}

	protected abstract Object toMap(T o);

	private Object map(Object o) {
		Object result = o;
		if (o instanceof Map && entityInstantiator != null) {
			result = entityInstantiator.newInstance((Map) o);
		}
		return result;
	}

}
