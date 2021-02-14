# Releases
## 2.0
### New stuff
- Graphrunner: Run operations in order specified by a directed graph.
- Wrapadapt: A wrapper for executing typed command with an un-typed parameter
- slEl: Super-light Expression Language
- Propcross: A graph utility for evaluating cross-referencing properties
### Refactoring
- Split out into separate libraries to limit classpath contamination
    - utils.adaptex
    - utils.extractex
    - utils.graphrunner
    - utils.mapqua
    - utils.nippe
    - utils.propcross
    - utils.slel
    - utils.wrapadapt
- Modularized all sub projects
- Changed root package from com.legyver.util to com.legyver.utils to match the module definition
- Reset @since 2.0 for all modules since the package name changed
## 1.0.1
### New stuff
- Adaptex: Adapt checked exceptions to CoreException
    - ExceptionToCoreExceptionConsumerDecorator
    - ExceptionToCoreExceptionFunctionDecorator
    - ExceptionToCoreExceptionPredicateDecorator
    - ExceptionToCoreExceptionSupplierDecorator
### Refactoring
- Extractex
    - Now supports depth-first typed exception extraction.
## 1.0
### New stuff
- Extractex: A wrapped-exception extractor
    - ExceptionExtractor
- Mapqua: Map representation of objects.
  - Objects
    - MapBackedCollection
    - MapBackedDouble
    - MapBackedEntity
    - MapBackedEntityCollection
    - MapBackedInteger
    - MapBackedLocalDate
    - MapBackedLocalDateTime
    - MapBackedMap
    - MapBackedNativeCollection
    - MapBackedProperty
    - MapBackedString
  - functions
    - MapMerge
    - MapQuery
- Nippe: Null-pointer exception handling
    - Base
    - Step