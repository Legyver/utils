package com.legyver.util.mapqua;

import java.util.Map;
import java.util.function.BiFunction;

public class MapMerge implements BiFunction {

	@Override
	public Object apply(Object oldValue, Object newValue) {
		Object result;
		if (oldValue instanceof Map && newValue instanceof Map) {
			Map oldMap = (Map) oldValue;
			Map newMap = (Map) newValue;
			for (Object key: newMap.keySet()) {
				Object nv = newMap.get(key);
				oldMap.merge(key, nv, this::apply);
			}
			return oldMap;
		} else {
			result = newValue;
		}
		return result;
	}

}
