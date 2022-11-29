# utils-ruffles
Utility to set a field's value by guessing its method name prior to resorting to reflection.

```groovy
implementation group: 'com.legyver', name: 'utils-ruffles', version: '3.4.0-alpha.4'
```


## Features
1. Set a value of a field via a method prior to resorting to reflection
   1. If the value is not a Collection
      1. Try set + (capitalized) field name
   2. If the value is a Collection
      1. try add + (capitalized) field name
      2. try add + (capitalized and singularized) fieldName

## Example
### Field with no setter
Below will set the value by reflection
```java
public class Example {
    private String name;
}
```

### Field with setter
Below will set the value using the setName method

```java
public class Example {
  private String name;

  public void setName(String name) {
    this.name = name;
  }
}
```

### Collection field with mutator
Below will set the value using the addName method

```java
import java.util.ArrayList;
import java.util.List;

public class Example {
   private final List<String> names = new ArrayList<>();

   public void addName(String name) {
      names.add(name);
   }
}
```

Since 3.4