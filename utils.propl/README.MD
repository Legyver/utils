# Propl
Ordered property alternative to java.util.Properties

```groovy
implementation group: 'com.legyver', name: 'utils-propl', version: '3.1.0'
```

## Use cases
Preserve order and comments in property files

## Ported methods
- load(InputStream)
- getPropertyNames(): List\<String\>
- getProperty(String): String
- put(String, Object)

## Additional methods
- load(File)
- save(File)
- size(): int

Example:
```java
try (InputStream inputStream = PropertyListTest.class.getResourceAsStream("sample.properties")){
    PropertyList propertyList = new PropertyList();
    propertyList.load(inputStream);
    PropertyList propertyList2 = new PropertyList();
    //since this includes comments, this will effectively preserve order, and all comments
    for (String propertyName : propertyList.getPropertyNames()) {
        propertyList2.put(propertyList.getProperty(propertyName));
    }
}
```