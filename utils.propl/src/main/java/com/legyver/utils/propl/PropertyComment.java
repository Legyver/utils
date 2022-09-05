package com.legyver.utils.propl;

/**
 * A comment or otherwise non-property specifying line
 */
public class PropertyComment implements PropertyValue {
    private final String value;

    /**
     * Construct a PropertyComment with the specified text value
     * @param value the value of the comment
     */
    public PropertyComment(String value) {
        this.value = value;
    }

    @Override
    public String getKey() {
        return value;
    }

    @Override
    public String asLine() {
        return value;
    }

}
