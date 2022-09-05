package com.legyver.utils.propl;

/**
 * A value in a property file.
 * This could either be a property or a comment or blank line.
 */
public interface PropertyValue {
    /**
     * Get the key component of the line
     * @return the key
     */
    String getKey();

    /**
     * Get the whole line
     * @return the line
     */
    String asLine();

}
