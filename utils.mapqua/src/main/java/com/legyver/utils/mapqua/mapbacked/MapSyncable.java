package com.legyver.utils.mapqua.mapbacked;

import com.legyver.core.exception.CoreException;

/**
 * POJO can be synced to the backing Map by invoking the sync method.
 */
public interface MapSyncable extends RawMapAware {
	/**
	 * Syncs the POJO to the internal Map
	 * @throws CoreException if there is an error marshalling to/from JSON
	 */
	void sync() throws CoreException;
}
