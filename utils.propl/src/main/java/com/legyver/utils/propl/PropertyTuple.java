package com.legyver.utils.propl;

public class PropertyTuple implements PropertyValue {
    private final String key;
    private final String value;

    public PropertyTuple(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String asLine() {
        return key + "=" + value;
    }

}
