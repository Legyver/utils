package com.legyver.util.mapqua.mapbacked;

import java.util.Map;

@FunctionalInterface
public interface EntityInstantiator<T> {
	T newInstance(Map m);
}
