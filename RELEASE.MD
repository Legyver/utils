# Releases
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