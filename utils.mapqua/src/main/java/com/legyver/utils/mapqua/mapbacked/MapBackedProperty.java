package com.legyver.utils.mapqua.mapbacked;

import com.legyver.core.exception.CoreException;
import com.legyver.utils.mapqua.MapQuery;
import java.util.Map;
import java.util.Optional;

/**
 * Abstract superclass for MapBacked anything.
 *
 * @param <T> Type of Object that this will be backing (in an internal map)
 */
public abstract class MapBackedProperty<T> {
	/**
	 * The internal map in which all member values will be stored
	 */
	protected final Map sourceMap;
	/**
	 * The key that the value will be associated with.  This is analogous to the variable name in a standard POJO
	 */
	protected final String property;
	/**
	 * The value to use if the value has not been set.
	 * This is largely derived from the extending class so it can be a List for a collection, 0.0 for a double, etc
	 * Although it is analogous to a boolean being false by default, or an integer being -1 if it wasn't otherwise set,
	 * an Object will not be null by default unless that was specifically specified.
	 */
	protected final T valueIfMissing;

	/**
	 * Construct a MapBackedProperty with a certain property name and default value
	 * @param sourceMap Map to store the value in
	 * @param property Key name of the property
	 * @param valueIfMissing value to set if missing
	 */
	public MapBackedProperty(Map sourceMap, String property, T valueIfMissing) {
		this.sourceMap = sourceMap;
		this.property = property;
		this.valueIfMissing = valueIfMissing;
	}

	/**
	 * Return the value of the the property.  This is analogous to the standard getter on a POJO.
	 * If the value has not been set, it will be defaulted to the {@link #valueIfMissing}.
	 * @return the property
	 * @throws CoreException if there is a problem marshalling to/from JSON
	 */
	public T get() throws CoreException {
		Optional<T> option = queryOption();
		T value;
		if (option.isPresent()) {
			value = option.get();
		} else {
			value = valueIfMissing;
			set(value);
		}
		return value;
	}

	/**
	 * Set the value of the property. This is analogous to a standard setter on a POJO.
	 * @param value the value to set
	 * @throws CoreException if there is a problem marshalling to/from JSON
	 */
	public void set(T value) throws CoreException {
		if (value == null) {
			sourceMap.remove(property);
		} else {
			new MapQuery.Query().set(property, value).execute(sourceMap);
		}
	}

	/**
	 * Hook to allow the property to be queryable
	 * @return the query option to use for the Property
	 * @throws CoreException if there is a problem marshalling to/from JSON
	 */
	protected abstract Optional<T> queryOption() throws CoreException;
	
}
