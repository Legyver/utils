# utils-graphrunner
Process directed graph relations to guarantee all node operations run in correct order
```groovy
implementation group: 'com.legyver', name: 'utils-graphrunner', version: '3.5.0'
```

## Features
### Create a directed graph.
```
       preInit --> init --> postInit  //preInit always runs before init, init before postInit, etc
      /
start 
      \
       shutdown //shutdown can be triggered at any time 
```
The below code makes the graph denoted above

```java
Node preInit = new Node(LifecyclePhase.PRE_INIT);
Node init = new Node(LifecyclePhase.INIT);
Node postInit = new Node(LifecyclePhase.POST_INIT);
Node shutdown = new Node(LifecyclePhase.SHUTDOWN);

Graph graph = new Graph.Builder()
    .nodes(preInit, init, postInit, shutdown)
    .connect(new Graph.Connection()
    	.from(preInit.getNodeName())
    	.to(init.getNodeName()))
    .connect(new Graph.Connection()
    	.from(init.getNodeName())
    	.to(postInit.getNodeName()))
    .build();
```
### Execute up to a specific node (inclusive)
```java
//below will run 'preInit', 'init' and 'postInit' in order
graph.setStrategy(new RunWithDependentsStrategy(postInit.getNodeName()));
graph.executeStrategy((GraphExecutedCommand<Node>) (nodeName, phaseNode) -> {
	//operation to run on node
});
```
### Execute the full graph in order
```java
//below will run ordered nodes 'preInit', 'init' and 'postInit' in order,
//and also 'shutdown' in the order it comes to it which can be either before or after
graph.setStrategy(new RunAllStrategy());
graph.executeStrategy((GraphExecutedCommand<Node>) (nodeName, phaseNode) -> {
	//operation to run on node
});
```
### Reset the graph
```java
graph.reset(new Graph.ResetOptions()
		.evaluatedFlag()
		.evaluationProgress());
```
or 
```java
graph.resetEvaluated();//does the same as the above
```
## Examples
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
#Result: build.message=Build 1.0.0.0000, built on 11 April 2020
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
PropertyMap map = PropertyMap.of(buildProperties, copyrightProperties);

//how to get the extract the variable for evaluation
VariableExtractionOptions variableExtractionOptions = new VariableExtractionOptions(jexlVar, 1);
//any aliasing that may occur (for instance, to prevent overwriting the format)
VariableTransformationRule variableTransformationRule = new VariableTransformationRule(Pattern.compile("\\.format$"),
    TransformationOperation.upToLastIndexOf(".format"));

PropertyGraphFactory factory = new PropertyGraphFactory(variableExtractionOptions, variableTransformationRule);
Graph contextGraph = factory.make(map, (s, o) -> new SharedMapCtx(s, map));
...
contextGraph.executeStrategy((nodeName, currentValue) -> {
   //operation you want to run.  See JexlExpressionGraphTest for the Jexl example
});
```

Output
```java
assertThat(map.get("build.version"), is("1.0.0.0000"));
assertThat(map.get("build.message"), is("Build 1.0.0.0000, built on 11 April 2020"));
assertThat(map.get("copyright"), is("Copyright © Legyver 2020-2020."));
```
See JexlExpressionGraphTest for more information.
