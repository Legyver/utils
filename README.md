# utils
Handy Java solutions to common problems.

## Extractex
Extract a specified exception by type from wrapped exception hell.
```java
Exception EXCEPTION = new ExceptionC(new ExceptionB(new CoreException(MESSAGE)));
CoreException extracted = new ExceptionExtractor<>(CoreException.class).extractException(EXCEPTION);
assertThat(extracted.getMessage(), is(MESSAGE));
```
Null safe, and InvocationTargetException handling.

## Nippe
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
