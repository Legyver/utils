# utils
Handy Java solutions to common problems.

## Nippe: 
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
