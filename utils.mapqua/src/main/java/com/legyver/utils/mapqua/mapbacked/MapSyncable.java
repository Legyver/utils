package com.legyver.utils.mapqua.mapbacked;

/**
 * POJO can be synced to the backing Map by invoking the sync method.
 */
public interface MapSyncable extends RawMapAware {
	/**
	 * Syncs the POJO to the internal Map
	 */
	void sync();
}
