package com.legyver.utils.mapqua.mapbacked;

import java.util.Map;

/**
 * Lambda to specify how an entity is to be instantiated.
 * @param <T> the type of the entity
 */
@FunctionalInterface
public interface EntityInstantiator<T> {
	/**
	 * Instantiator for an entity.
	 * @param m the internal map to instantiate the entity in.
	 * @return the new instance of the entity
	 */
	T newInstance(Map m);
}
