package com.legyver.utils.propl;

public class PropertyComment implements PropertyValue {
    private final String value;

    public PropertyComment(String value) {
        this.value = value;
    }

    @Override
    public String getKey() {
        return value;
    }

    public String asLine() {
        return value;
    }

}
