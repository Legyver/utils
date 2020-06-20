# utils
Handy Java solutions to common problems.

## Features
### Adaptex
Adapt a checked java.lang.Exception into a com.legyver.core.exception.CoreException.  Basic support covers any function that can be inferred as any of the following common patterns but throw exceptions:
- Consumer
- Function
- Predicate
- Supplier

See the tests in the adaptex package for examples.

Since 1.0.1
### Extractex
Extract a specified exception by type from wrapped exception hell.
```java
Exception EXCEPTION = new ExceptionC(new ExceptionB(new CoreException(MESSAGE)));
CoreException extracted = new ExceptionExtractor<>(CoreException.class).extractException(EXCEPTION);
assertThat(extracted.getMessage(), is(MESSAGE));
```
Null-safe and InvocationTargetException handling.

Since 1.0
### Mapqua
Map-backed object representation to solve the problem of forward and backward compatibility.

Allows any of the following to be represented as Maps
- POJO
  - Any POJO that can be represented as a map of other supported types
- Text
  - java.lang.String
- Numbers
  - java.lang.Double
  - java.lang.Integer
- Collections
  - java.util.List
  - java.util.Set
- Date/Time
  - java.time.LocalDate
  - java.time.LocalDateTime
  
Functionality
- MapMerge
    - BiFunction for merging maps
- MapQuery
    - Getter/Setter abstraction for Maps
    
See the MapQueryTest/MapMergeTest for examples.

Since 1.0
### Nippe
Collapse this:
```java
String value = null;
if (myObject != null) {
	ClassA classA = myObject.getClassA();
	if (classA != null) {
		ClassB classB = classA.getClassB();
		if (classB != null) {
			value = classB.getText();
		}
	}
}
```
 into this:
 ```java
 String value = new Step<>(new Step<>(new Step<>(new Base<>(myObject),
				myObject -> myObject.getClassA()),
				classA -> classA.getClassB()),
				classB -> classB.getText()).execute();
```
Since 1.0
## Versioning
Release.Breaking.Feature.Fix
- Release: Used for major milestone releases.
- Breaking: Used when the change breaks backward compatibility.
- Feature: Used when introducing features that do not break backward compatability.
- Fix: Used for small bug fixes

## Releases
* [Release Notes](https://github.com/Legyver/utils/blob/master/RELEASE.MD)
