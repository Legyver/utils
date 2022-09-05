package com.legyver.utils.propl;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * {@link java.util.Properties} aren't ordered. We want the properties to be in the same order as the original for readability
 */
public class PropertyList {
    private List<PropertyValue> valueList = new ArrayList<>();

    /**
     * Load properties from a file
     * @param file the file to load properties from
     * @throws FileNotFoundException if the file is not found
     */
    public void load(File file) throws FileNotFoundException {
        try (Scanner scanner = new Scanner(file)) {
            load(scanner);
        }
    }

    /**
     * Load properties from an input stream
     * @param inputStream the input stream to load properties from
     */
    public void load(InputStream inputStream) {
        try (Scanner scanner = new Scanner(inputStream)) {
            load(scanner);
        }
    }

    private void load(Scanner scanner) {
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            PropertyValue added;
            if (line.startsWith("#") || line.isBlank() || !line.contains("=")) {
                added = new PropertyComment(line);
            } else {
                int firstEquals = line.indexOf('=');
                String key = line.substring(0, firstEquals);
                String value = line.substring(firstEquals + 1);
                added = new PropertyTuple(key, value);
            }
            valueList.add(added);
        }
    }



    /**
     * Write the properties to file
     * @param file the file to write the properties to
     * @throws IOException if there is an error writing to the file
     */
    public void write(File file) throws IOException {
        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
            for (PropertyValue value : valueList) {
                String line = value.asLine() + "\n";
                byte[] input = line.getBytes(StandardCharsets.UTF_8);
                outputStream.write(input);
            }
        }
    }

    /**
     * Get the ordered list of property names.
     * This does not include comments
     * @return the name of the properties
     */
    public List<String> stringPropertyNames() {
        return stringPropertyNames(false);
    }

    /**
     * Get the ordered list of property names including comments.
     * @param includeComments if comments are to be included
     * @return the name of the properties
     */
    public List<String> stringPropertyNames(boolean includeComments) {
        return valueList.stream()
                .filter(propertyValue -> includeComments || propertyValue instanceof PropertyTuple)
                .map(propertyValue -> propertyValue.getKey())
                .collect(Collectors.toList());
    }

    /**
     * Get the value of the property
     * @param key the property key
     * @return the value of the property
     */
    public String getProperty(String key) {
        return valueList.stream().parallel()
                .filter(propertyValue -> propertyValue instanceof PropertyTuple)
                .filter(propertyValue -> key.equals(propertyValue.getKey()))
                .map(propertyValue -> ((PropertyTuple) propertyValue).getValue())
                .findFirst().orElse(null);
    }

    /**
     * Add a property with a value
     * @param key the property key
     * @param value the property value
     */
    public void put(String key, String value) {
        int index = findIndex(key);
        if (index > -1) {
            valueList.remove(index);
        } else {
            index = valueList.size();
        }
        valueList.add(index, new PropertyTuple(key, value));
    }

    private int findIndex(String key) {
        for (int i = 0; i < valueList.size(); i++) {
            PropertyValue propertyValue = valueList.get(i);
            if (key.equals(propertyValue.getKey())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Remove the specified item from the list
     * @param key the key of the item to remove
     * @return the removed item or null
     */
    public String remove(String key) {
        int index = findIndex(key);
        String result = null;
        if (index > -1) {
            PropertyValue propertyValue = valueList.remove(index);
            if (propertyValue instanceof PropertyTuple) {
                result = ((PropertyTuple) propertyValue).getValue();
            }
        }
        return result;
    }

    /**
     * Get the size of the property properties including comments
     * @return the size of the properties
     */
    public int size() {
        return valueList.size();
    }
}
