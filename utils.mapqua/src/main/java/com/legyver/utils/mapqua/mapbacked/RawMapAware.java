package com.legyver.utils.mapqua.mapbacked;

import com.legyver.core.exception.CoreException;

import java.util.Map;

/**
 * POJO can be represented by an internal Map
 */
public interface RawMapAware {
	/**
	 * Retrieve the internal Map from the POJO
	 * @return the internal Map
	 * @throws CoreException if there is a problem marshalling to/from JSON
	 */
	Map getRawMap() throws CoreException;
}
