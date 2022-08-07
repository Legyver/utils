# utils-mapqua
Map-backed object representation to solve the problem of forward and backward compatibility.

```groovy
implementation group: 'com.legyver', name: 'utils-mapqua', version: '3.0.0'
```
## Features
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