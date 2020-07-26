# utils-adaptex
Adapt a checked java.lang.Exception into a com.legyver.core.exception.CoreException.

```gradle
implementation group: 'com.legyver', name: 'utils-adaptex', version: '2.0.0.0-alpha'
```
## Features
Basic support covers any function that can be inferred as any of the following common patterns but throw exceptions:
- Consumer
- Function
- Predicate
- Supplier

See the tests for examples.

Since 1.0.1