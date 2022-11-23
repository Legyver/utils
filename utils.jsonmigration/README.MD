# utils-jsonpathv
Annotation-based marshalling of JsonPath defined fields.  T

```groovy
implementation group: 'com.legyver', name: 'utils-jsonmigration', version: '3.4.0-alpha.3'
```
## Features
- Allows **reading** from multiple versions of a spec.
- Supported versions must comply with [SemVer 1.0.0](https://semver.org/spec/v1.0.0.html)
- Paths must comply with [JSONPath](https://github.com/json-path/JsonPath)
- Annotate a field or with @Migration to use a specific migration for a specific version
- Supports marshalling the following field types
  - String
  - Boolean
  - Long
  - Integer
  - Double
  - BigInteger
  - BigDecimal
  - LocalDate
  - LocalDateTime
  - LocalTime

## Example
### Version 1
In versions one we have a simply POJO that is converted to JSON
```java
public static class Spec1 {
    private String version = "1.0.0";
    private String name;
}
```
Version 1 JSON.
```json
{
  "version": "1.0.0",
  "name": "My Name"
}
```

### Version 2
Suppose we rename a field in the POJO.  This changes the JSON for version 2.

Version 2 JSON
```json
{
  "version": "2.0.0",
  "renamed": "My Name"
}
```

We can then ensure that we can read the version 1 file by annotating the renamed field with @Migration
```java
public class Spec2 {
    private String version = "2.0.0";
    @Migration(pre = "2.0.0", path = "$.name")
    private String renamed;
}
```


### Version 3
In version 3, we decide to implement a container object called data.
The version 3 JSON is shown below.
```json
{
  "version": "3.0.0",
  "data": {
    "name": "My Name" 
  }
}
```

We can populate this container object from v1 and v2 json with @Migration annotations

```java
public class Spec3 {
    private String version = "3.0.0";
    private Data3 data;
}

public class Data3 {
    @Migration(pre = "3.0.0", path = "$.renamed")
    @Migration(pre = "2.0.0", path = "$.named")
    private String name;
}
```

Since 3.4