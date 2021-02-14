package com.legyver.utils.mapqua.mapbacked;

import java.util.Map;

/**
 * POJO can be represented by an internal Map
 */
public interface RawMapAware {
	/**
	 * Retrieve the internal Map from the POJO
	 * @return the internal Map
	 */
	Map getRawMap();
}
