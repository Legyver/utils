# utils-extractex
Extract a specified exception by type from wrapped exception hell.

```gradle
implementation group: 'com.legyver', name: 'utils-extractex', version: '2.0.0.0-alpha'
```
## Features
Null-safe and InvocationTargetException handling.

```java
Exception EXCEPTION = new ExceptionC(new ExceptionB(new CoreException(MESSAGE)));
CoreException extracted = new ExceptionExtractor<>(CoreException.class).extractException(EXCEPTION);
assertThat(extracted.getMessage(), is(MESSAGE));
```

Since 1.0