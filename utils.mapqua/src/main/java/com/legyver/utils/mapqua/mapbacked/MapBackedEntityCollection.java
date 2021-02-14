package com.legyver.utils.mapqua.mapbacked;

import java.util.Collection;
import java.util.Map;

/**
 * For Collections of Entities
 * @param <U> Type of Collection (ie: List/Set)
 * @param <T> Type of Entity in the Collection
 */
public class MapBackedEntityCollection<U extends Collection<T>, T extends RawMapAware> extends MapBackedCollection<Collection<T>, T> {

	/**
	 * Construct a MapBackedEntityCollection with a certain property name and default value
	 * @param sourceMap Map to store the Collection value in
	 * @param property Key name of the property
	 * @param valueIfMissing value to set if missing
	 * @param entityInstantiator function to use to instantiate a new entry in the collection
	 */
	public MapBackedEntityCollection(Map sourceMap, String property, Collection valueIfMissing, EntityInstantiator entityInstantiator) {
		super(sourceMap, property, valueIfMissing, entityInstantiator);
	}

	/**
	 * Construct a MapBackedEntityCollection with a certain property name.  The default value will be a new ArrayList
	 * @param sourceMap Map to store the Collection value in
	 * @param property Key name of the property
	 * @param entityInstantiator function to use to instantiate a new entry in the collection
	 */
	public MapBackedEntityCollection(Map sourceMap, String property, EntityInstantiator entityInstantiator) {
		super(sourceMap, property, entityInstantiator);
	}

	/**
	 * Return the internal map where the MapBackedEntityCollection is stored
	 * @param o the object whose map will be returned
	 * @return the raw map for the passed in Object
	 */
	@Override
	protected Object toMap(T o) {
		return o.getRawMap();
	}

}
