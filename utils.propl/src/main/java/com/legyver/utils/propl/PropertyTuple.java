package com.legyver.utils.propl;

/**
 * A key-value pair representation of the property
 */
public class PropertyTuple implements PropertyValue {
    private final String key;
    private final String value;

    /**
     * Construct a key value pair with a specified key and value
     * @param key the property key
     * @param value the property value
     */
    public PropertyTuple(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getKey() {
        return key;
    }

    /**
     * Get the property value
     * @return the property value
     */
    public String getValue() {
        return value;
    }

    @Override
    public String asLine() {
        return key + "=" + value;
    }

}
