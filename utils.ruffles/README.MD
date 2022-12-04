# utils-ruffles
Utility to set/get a field's value by guessing its method name prior to resorting to reflection.

Uses Apache's MethodUtils/FieldUtils as a backup to cut down on number of targets in modular ***opens*** directives.

```groovy
implementation group: 'com.legyver', name: 'utils-ruffles', version: '3.4.0-alpha.4'
```

## Features
### Setting a value
Set a value of a field via a method prior to resorting to reflection
1. If the value is not a Collection
   1. Try set + (capitalized) field name
   2. Same but with commons-lang3 in case the module of the bean opens package to org.apache.commons.lang3
2. If the value is a Collection
   1. Try add + (capitalized) field name
   2. Same but with commons-lang3 in case the module of the bean opens package to org.apache.commons.lang3
   3. Try add + (capitalized and singularized) fieldName
   4. Same but with commons-lang3 in case the module of the bean opens package to org.apache.commons.lang3
3. If none of the above work
   1. Try setting the value on the field by reflection directly
   2. Same but with commons-lang3 in case the module of the bean opens package to org.apache.commons.lang3 

### Getting a value
Get a value of a field via the method
1. Try by method name
   1. Try get + (capitalized) field name
   2. Same but with commons-lang3 in case the module of the bean opens package to org.apache.commons.lang3
2. If neither above work, 
   1. Try getting the value of the field by reflection directly
   2. Same but with commons-lang3 in case the module of the bean opens package to org.apache.commons.lang3

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