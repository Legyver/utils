package com.legyver.util.mapqua.mapbacked;

import java.util.Collection;
import java.util.Map;

/**
 * For Collections of Entities
 * @param <U> Type of Collection (ie: List/Set)
 * @param <T> Type of Entity in the Collection
 */
public class MapBackedEntityCollection<U extends Collection<T>, T extends RawMapAware> extends MapBackedCollection<Collection<T>, T> {

	public MapBackedEntityCollection(Map sourceMap, String property, Collection valueIfMissing, EntityInstantiator entityInstantiator) {
		super(sourceMap, property, valueIfMissing, entityInstantiator);
	}

	public MapBackedEntityCollection(Map sourceMap, String property, EntityInstantiator entityInstantiator) {
		super(sourceMap, property, entityInstantiator);
	}

	@Override
	protected Object toMap(T o) {
		return o.getRawMap();
	}

}
