package com.legyver.utils.mapqua.mapbacked;

import com.legyver.utils.mapqua.MapQuery;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * An Entity variable that can be represented as a key-value pair in the POJO internal Map
 */
public class MapBackedEntity<T extends MapSyncable> extends MapBackedProperty<T>{
	private final EntityInstantiator<T> entityInstantiator;
	private T entity;

	/**
	 * Construct a MapBackedEntity with a certain property name and default value
	 * @param sourceMap Map to store the Entity value in
	 * @param property Key name of the property
	 * @param entityInstantiator function to use when adding a value to the Entity
	 */
	public MapBackedEntity(Map sourceMap, String property, EntityInstantiator<T> entityInstantiator) {
		super(sourceMap, property, entityInstantiator.newInstance(sourceMap));
		this.entityInstantiator = entityInstantiator;
	}

	/**
	 * Retrieve the wrapped entity
	 * @return the existing entity or an new Entity instantiated from the EntityInstantiator
	 */
	@Override
	public T get() {
		if (entity == null) {
			Optional<Object> option = new MapQuery.Query().object(property).execute(sourceMap);
			Map map;
			if (option.isPresent()) {
				map = (Map) option.get();
			} else {
				map = new LinkedHashMap<>();
				sourceMap.put(property, map);
			}
			entity = entityInstantiator.newInstance(map);
		}
		return entity;
	}

	@Override
	protected Optional<T> queryOption() {
		return Optional.empty();
	}

}
