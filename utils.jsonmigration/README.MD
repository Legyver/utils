# utils-jsonpathv
Annotation-based marshalling of JsonPath defined fields.  T

```groovy
implementation group: 'com.legyver', name: 'utils-jsonpathv', version: '3.4.0'
```
## Features
- Allows **reading** from multiple versions of a spec.
- Supported versions must comply with [SemVer 1.0.0](https://semver.org/spec/v1.0.0.html)
- Paths must comply with [JSONPath](https://github.com/json-path/JsonPath)
- Annotate a field or with @Migration to use a specific migration for a specific version

```java
public static class Spec1 {
    private String version = "1.0.0";
    private String name;
}
```

```json
{
  "version": "1.0.0",
  "name": "My Name"
}
```

```java
public static class Spec2 {
    private String version = "2.0.0";
    @Migration(pre = "2.0.0", path = "$.name")
    private String renamed;
}
```
```json
{
  "version": "2.0.0",
  "renamed": "My Name"
}
```

```java
//in pre-3.0.0 after 2.x json, the value was called 'renamed'
@Migration(pre = "3.0.0", path = "$.renamed")
//in pre-2.0.0 json, the value was called 'name'
@Migration(pre = "2.0.0", path = "$.name")
String named;
```
```json
{
  "version": "3.0.0",
  "data": {
    "name": "My Name" 
  }
}
```

Since 3.4