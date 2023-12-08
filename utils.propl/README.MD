# Propl
Ordered property alternative to java.util.Properties

```groovy
implementation group: 'com.legyver', name: 'utils-propl', version: '3.5.0'
```

## Use cases
Preserve order and comments in property files

## Ported methods
- load(InputStream): void
- stringPropertyNames(): List\<String\>
- getProperty(String): String
- put(String, Object): void
- remove(String): String

## Additional methods
- load(File): void
- save(File): void
- size(): int

Example:
```java
try (InputStream inputStream = PropertyListTest.class.getResourceAsStream("sample.properties")){
    PropertyList propertyList = new PropertyList();
    propertyList.load(inputStream);
    
    PropertyList copyNoComments = new PropertyList();
    //since this does not include comments, this will effectively preserve order, but remove all comments
    for (String propertyName : propertyList.stringPropertyNames()) {
        copyNoComments.put(propertyList.getProperty(propertyName));
    }
    
    PropertyList copyWithComments = new PropertyList();
    //since this includes comments, this will effectively preserve order including comments
    for (String propertyName : propertyList.stringPropertyNames(true)) {
        copyWithComments.put(propertyList.getProperty(propertyName));
    }
    
}
```