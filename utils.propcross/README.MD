# Propcross
A graph utility for evaluating cross-referencing properties

```gradle
implementation group: 'com.legyver', name: 'utils-propcross', version: '3.2.1'
```

## Usage
1. Create a property graph of all your properties you want to evaluate.
    1. You may wish to use PropertyMap.of(Properties...) to construct this
1. Declare an OperationContext to use.  This says what variables look like and identify the suffix for variables to be
    1. For example, you may want to have the result of an operation on a property be injected into another property by dropping the property suffix (Ex: subject.format being stored on a property called subject; see example below)
1. Run the graph with that OperationContext

### Example
#### Code

```java
PropertyMap propertyMap = PropertyMap.of(
		adjectives,
		directObject,
		nouns,
		prepositions,
		sentence,
		subject,
		verbs);
PropertyGraph propertyGraph = new PropertyGraph(propertyMap);
propertyGraph.runGraph(new SlelOperationContext(".format"));
		
String sentence = (String) propertyMap.get("sentence");
assertEquals("The quick red fox jumped over the lazy brown dog", sentence);
```
#### Resources
adjective.properties
```properties
subject.adjective1=quick
subject.adjective2=red
direct.object.adjective1=lazy
direct.object.adjective2=brown
```
noun.properties
```properties
subject.noun=fox
direct.object.noun=dog
```
preposition.properties
```properties
preposition=over
```
verb.properties
```properties
verb=jumped
```
subject.properties
```properties
subject.format=${subject.adjective1} ${subject.adjective2} ${subject.noun}
```
directobject.properties
```properties
direct.object.format=${direct.object.adjective1} ${direct.object.adjective2} ${direct.object.noun}
```
sentence.properties
```properties
sentence.format=The ${subject} ${verb} ${preposition} the ${direct.object}
```