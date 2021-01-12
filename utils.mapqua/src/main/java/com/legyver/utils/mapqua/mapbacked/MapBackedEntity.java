package com.legyver.utils.mapqua.mapbacked;

import com.legyver.utils.mapqua.MapQuery;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class MapBackedEntity<T extends MapSyncable> extends MapBackedProperty<T>{
	private final EntityInstantiator<T> entityInstantiator;
	private T entity;

	public MapBackedEntity(Map sourceMap, String property, EntityInstantiator<T> entityInstantiator) {
		super(sourceMap, property, entityInstantiator.newInstance(sourceMap));
		this.entityInstantiator = entityInstantiator;
	}

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
