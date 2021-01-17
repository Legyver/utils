package com.legyver.utils.mapqua.mapbacked;

import com.legyver.utils.mapqua.MapQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Abstract superclass for MapBacked Collections.
 *
 * @param <U> Type of Collection (ie: List/Set)
 * @param <T> Type of Entity in the Collection
 */
public abstract class MapBackedCollection<U extends Collection<T>, T> extends MapBackedProperty<Collection> {
	private final EntityInstantiator entityInstantiator;
	private U collectionReference;

	/**
	 * Construct a MapBackedCollection with a certain property name and default value
	 * @param sourceMap Map to store the Collection value in
	 * @param property Key name of the property
	 * @param valueIfMissing value to set if missing
	 * @param entityInstantiator function to use to instantiate a new entry in the collection
	 */
	protected MapBackedCollection(Map sourceMap, String property, Collection valueIfMissing, EntityInstantiator entityInstantiator) {
		super(sourceMap, property, valueIfMissing);
		this.entityInstantiator = entityInstantiator;
	}

	/**
	 * Construct a MapBackedEntityCollection with a certain property name.
	 * @param sourceMap Map to store the Collection value in
	 * @param property Key name of the property
	 * @param valueIfMissing  The default value.
	 */
	protected MapBackedCollection(Map sourceMap, String property, Collection valueIfMissing) {
		this(sourceMap, property, valueIfMissing, null);
	}

	/**
	 * Construct a MapBackedCollection with a certain property name.  The default value will be a new ArrayList
	 * @param sourceMap Map to store the Collection value in
	 * @param property Key name of the property
	 * @param entityInstantiator function to use to instantiate a new entry in the collection
	 */
	protected MapBackedCollection(Map sourceMap, String property, EntityInstantiator entityInstantiator) {
		this(sourceMap, property, new ArrayList<>(), entityInstantiator);
	}

	/**
	 * Construct a MapBackedCollection with a certain property name.
	 * The default value will be a new ArrayList
	 * The entityInstantiator will be null.
	 * @param sourceMap Map to store the Collection value in
	 * @param property Key name of the property
	 */
	protected MapBackedCollection(Map sourceMap, String property) {
		this(sourceMap, property, new ArrayList<>(), null);
	}

	/**
	 * Add a value to the collection
	 * @param value the value to be added
	 */
	public void add(T value) {
		get().add(value);
	}

	/**
	 * Remove a value from the collection
	 * @param value the value to be removed
	 */
	public void remove(T value) {
		get().remove(value);
	}

	/**
	 * Sync's the collection to the Map
	 */
	public void sync() {
		set(deTransform(get()));
	}

	/**
	 * Return the internal collection.
	 * If the internal collection is null, it will create it via the {@link #transform(Collection)} method using super.get() as the parameter
	 * @return the internal collection
	 */
	@Override
	public U get() {
		if (collectionReference == null) {
			collectionReference = transform(super.get());
		}
		return collectionReference;
	}

	/**
	 * Hook to allow the Collection to be queryable
	 * @return the query option to use for the Collection
	 */
	@Override
	protected Optional<Collection> queryOption() {
		return new MapQuery.Query().collection(property).execute(sourceMap);
	}

	/**
	 * Replace the internal collectionReference with the source collection transformed via the {@link #map(Object)} method
	 * @param source the source collection
	 * @return the internal collectionReference
	 */
	protected U transform(Collection source) {
		if (source instanceof List) {
			collectionReference = (U) source.stream().map(this::map).collect(Collectors.toList());
		} else {
			collectionReference = (U) source.stream().map(this::map).collect(Collectors.toSet());
		}
		return collectionReference;
	}

	/**
	 * Reverses the transformation of the {@link #transform(Collection)} method.  The internal collection reference is not updated.
	 * @param source the collection to deTransform via the {@link #toMap(Object)} method
	 * @return the source collection de-transformed
	 */
	protected Collection deTransform(Collection<T> source) {
		Collection result;
		if (source instanceof List) {
			result = (Collection) source.stream().map(this::toMap).collect(Collectors.toList());
		} else {
			result = (Collection) source.stream().map(this::toMap).collect(Collectors.toSet());
		}
		return result;
	}

	/**
	 * Return the internal map where the MapBackedCollection is stored
	 * @param o the object whose map will be returned
	 * @return the raw map for the passed in Object
	 */
	protected abstract Object toMap(T o);

	/**
	 * Converts a map into an entity via the {@link #entityInstantiator}.  If the entityInstantiator is null, any Map will not be converted
	 * @param o object to be converted
	 * @return the object or a map converted to an entity
	 */
	protected final Object map(Object o) {
		Object result = o;
		if (o instanceof Map && entityInstantiator != null) {
			result = entityInstantiator.newInstance((Map) o);
		}
		return result;
	}

}
