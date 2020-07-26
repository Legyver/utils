#utils-graphrunner
Process directed graph relations to guarantee all node operations are run in correct order
```gradle
implementation group: 'com.legyver', name: 'utils-graphrunner', version: '2.0.0.0-alpha'
```

## Features
The below example evaluates JEXL expressions on properties to allow for cross-referencing of java properties.

<p>build.properties</p>

```properties
major.version=1
minor.version=0
patch.number=0

build.number=0000
build.date.day=11
build.date.month=April
build.date.year=2020

build.date.format=`${build.date.day} ${build.date.month} ${build.date.year}`
#Result: build.date=11 April 2020

build.version.format=`${major.version}.${minor.version}.${patch.number}.${build.number}`
#Result: build.version=1.0.0.0000

build.message.format=`Build ${build.version}, built on ${build.date}`
#Result: build.message=Build 1.0.0.0000, built on 11 May 2020
```
copyright.properties
```properties
copyright.format=`Copyright © Legyver 2020-${build.date.year}.`
#Result: copyright=Copyright © Legyver 2020-2020.
```

MyApplication.java
```java
Pattern jexlVar = Pattern.compile(JEXL_VARIABLE);
Properties buildProperties = new Properties();
buildProperties.load(MyApplication.class.getResourceAsStream("build.properties"));
Properties copyrightProperties = new Properties();
copyrightProperties.load(MyApplication.class.getResourceAsStream("copyright.properties"));

//how to get the extract the variable for evaluation
VariableExtractionOptions variableExtractionOptions = new VariableExtractionOptions(jexlVar, 1);
//any aliasing that may occur (for instance, to prevent overwriting the format)
VariableTransformationRule variableTransformationRule = new VariableTransformationRule(Pattern.compile("\\.format$"),
    TransformationOperation.upToLastIndexOf(".format"));

ContextGraphFactory factory = new ContextGraphFactory(variableExtractionOptions, variableTransformationRule);
ContextGraph contextGraph = factory.make(PropertyMap.of(buildProperties, copyrightProperties));
...
GraphRunner runner = new GraphRunner(map);
runner.setCommand((nodeName, currentValue) -> {
   //operation you want to run.  See GraphRunnerJexlExpressionTest for the Jexl example
});
runner.runGraph(contextGraph);
```
Output
```java
assertThat(map.get("build.version"), is("1.0.0.0000"));
assertThat(map.get("build.message"), is("Build 1.0.0.0000, built on 11 April 2020"));
assertThat(map.get("copyright"), is("Copyright © Legyver 2020-2020."));
```
See GraphRunnerJexlExpressionTest for more information.
