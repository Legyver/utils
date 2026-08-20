# Releases
## 3.8.0
### Bug fixes
- HttpClient
  - autoHandleErrorResponse/autoHandleRetryAfter threw NullPointerException because the builder's retry-handler map was never initialized
  - Any non-200 response threw NullPointerException even for clients that never configured retry handling
  - Retries reused the already-connected HttpURLConnection from the failed attempt instead of issuing a fresh HTTP request
  - Error response logging called getInputStream() instead of getErrorStream(), causing an IOException before ResponseCodeException could be thrown for non-200 responses
  - ResponseCodeException header lookups were case-sensitive; Retry-After (and other headers) sent with different casing were not recognized
  - Module name collided with utils.cli, preventing both modules from being used together on the module path
- Ruffles
  - ClassInstantiator built the constructor parameter types from parameters[0] instead of parameters[i], so multi-argument constructor lookups failed even when a matching constructor existed
  - GetByMethod's direct-field-read fallback passed the reflected Field where the target instance was expected, breaking the fallback whenever no getter method was found
  - SetByMethod silently left a collection field unchanged when setting an empty collection and no bulk setter existed, instead of falling through to the direct field write
- MapQua
  - MapQuery's typed-read type check had its operands reversed, causing every typed read to run an unnecessary (and, for date types, potentially lossy) JSON round-trip
  - MapQuery.set()/add() threw an undocumented NoSuchElementException instead of a CoreException when an intermediate path segment was missing from the source map
  - MapBackedMap.put()/get(key) could NPE if called before the no-arg get() had lazily initialized the backing map
- GraphJXml
  - AbstractInputReader's leading-newline handling could throw ArrayIndexOutOfBoundsException (or otherwise corrupt the stream) when the XML input began with a bare "\n"; replaced with a correct implementation
- PropCross
  - PropertyGraph's constructor accepted a null map, deferring the failure to a confusing NullPointerException deep inside graph traversal instead of failing fast
- Slel
  - ExpressionInterpreter's constructor accepted a null context, deferring the failure to a NullPointerException only when a variable expression was encountered
- GraphRunner
  - SharedMapCtx's constructor accepted a null sharedContextMap, deferring the failure to a NullPointerException on first read/write
- CLI
  - Menu silently swallowed a failure constructing the built-in --help option, leaving it null and surfacing as a confusing NPE on first use instead of failing fast at construction
### New functionality
- HttpClient
  - RetryAfterHandler and Builder#autoHandleRetryAfter now support a configurable maximum retry count (default 3) instead of retrying exactly once
  - When Retry-After is absent on a 429/503, a default exponential backoff is now used instead of giving up immediately
### Known issues / follow-up
AI-assisted review of the httpclient fixes above prompted a wider sweep for similar defects across the other modules; a few findings were flagged but intentionally left unfixed here because addressing them changes a public method's signature or behavior:
- GraphJXml: XmlFileWriter#writeToFile and AbstractInputReader#parse swallow IOException/XMLStreamException from the primary read/write operation (logging only) and return void/null instead of surfacing the failure to the caller
- JsonMigration: VersionSelector mutates the caller-supplied list in place instead of taking a defensive copy
- JsonMigration: JSONPathInputAdapter#getAdaptedValue silently truncates a collection at the first non-Map element instead of raising an error for a mixed-type collection
- MapAdapt: TypedMapAdapter's lazy registerTransformers() guard can skip registering the built-in transformers if a subclass registers a custom transformer before the first get() call
- Cross-module: every generated license/LicenseServiceImpl#load NPEs via Properties.load(null) if license.properties is missing from the classpath at runtime (packaging-time issue, same generated boilerplate in every module)
## 3.7.0
### New functionality
- HttpClient
  - ResponseCodeException should allow response headers to be retrieved
  - Handle Retry-After response header automatically if configured
### Refactoring
- Adaptex
  - Decorators now implement the com.legyver.core.function interfaces
## 3.6.1
### Bug fixes
- HttpClient
  - Query Param processing can lead to malformed URLs
- Jackiso
  - JacksonObjectMapper should be able to read from file
## 3.6.0
### Bug fixes
- HttpClient
  - ResponseCodeException should accept a response code
  - Query parameters should be included in request url
### New functionality
- HttpClient
  - Builder and client should accept path variables
- Jackiso
  - Expand JacksonObjectMapper functionality to include writing to file/output stream
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