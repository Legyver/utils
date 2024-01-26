# Releases
## 3.5.2
- No code change.  3.5.1 was built with JDK 17 by mistake.  Rebuilding with JDK 11
## 3.5.1
### Security
- Upgrade json-path dependencies to 2.9
### Refactoring
- Ruffles/JSONMigration
  - Use automatic modules
## 3.5.0
### New stuff
- CLI
  - Java Command Line Menu support
- HttpClient
  - An HTTP client wrapper around Http(s)URLConnection with GZIP content handling
### Refactoring
- Dependency bumps
## 3.4.1
### Refactoring
- Migrated license.properties to v2
## 3.4.0
### New stuff
- JSONMigration
  - Version annotation-based JsonPath parsing
- Ruffles
  - Reflection utilities to set a value on a field
### Refactoring
- Dependency bumps
- AdaptEx
  - Limit number of same-type exceptions wrapped
## 3.3.1
### Refactoring
- graphjxml
  - now can read from both File and InputStream
## 3.2.1
### Bug fixes
- Propl
  - parsing updated to handle/respect blank lines

## 3.2
### Refactoring
- Propl
  - Added remove(String) functionality
### Bug fixes
- Propl
  - put(String, String) should replace existing value

## 3.1
### New Stuff
- Propl
  - An ordered alternative to java.util.Properties
## 3.0
### Minimum JDK
Built on JDK 11 for LTS
### Versioning
Updated to SemVer 2.0
## 2.1.2.1
### Security
- Upgrade Jackson to 2.13.3
## 2.1.2.0
### New Stuff
- mapadapt
  - New library for retrieving typed values from a map
## 2.1.1.6
### Minimum JDK
Built on JDK 17 for LTS
### New Stuff
- Jackiso
  - Added ability to read from InputStream
## 2.1.1.5
### Security
- Update log4j version to 2.17.1
## 2.1.1.4
### Security
- Update log4j version to 2.17.0
## 2.1.1.3
### Security
- Update log4j version to 2.15.0
## 2.1.1.2
### Refactoring
- GraphJXML
  - Fixed bug pretty-printing XML with empty elements.
  - Fixed bug parsing XML files starting with blank lines.
  - Fixed bug parsing XML that has HTML tags embedded in the characters text.
  - @depreacted XmlGraph.pop() as it serves no purpose given the empty root has been eliminated
## 2.1.1.1
### Refactoring
- GraphJXML
  - Should pretty-print graph output
## 2.1.1
### New Stuff
- GraphJXML: A Java utility to represent a graph as XML.
## 2.1.0.3
- Addressed lint issues with generics
  - ExtractEx
  - Graphrunner
  - Jackiso
  - PropCross
## 2.1.0.2
- Jackiso: Fixed bug with dates being truncated
- Nippe: reverted the change in 2.1.0.1 as it makes the API unnecessarily messy.  It's preferable to handle exections the same way as we do with the Java Streaming API.
## 2.1.0.1
- Jackiso: Add pretty print option when writing JSON
- Nippe: The step method now throws CoreException
## 2.1
Breaking change due to gson library replacement effecting **utils.mapqua** module.  Other modules not effected.
- Dates and Times will now be stored in ISO-8601 format replacing the gson custom format.
### New stuff
- Jackiso: A JSON utility for with support for marshalling java.time API to/from ISO-8601 format
- Adaptex: Added support for more @FunctionalInterface's
  - ExceptionToCoreExceptionActionDecorator
  - ExceptionToCoreExceptionVoidActionDecorator
  - ExceptionToCoreExceptionBiConsumerDecorator
  - ExceptionToCoreExceptionBiFunctionDecorator
  - ExceptionToCoreExceptionBiPredicateDecorator
### Refactoring
- Mapqua: Replaced the gson library with Jackson, since gson is in maintenance mode and Moshi is not Java 15 friendly yet.
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