# utils-wrapadapt
An if-instanceof-then-execute utility

```gradle
implementation group: 'com.legyver', name: 'utils-wrapadapt', version: '2.0.0.0-alpha'
```
## Features
Get rid of the if-then mess
```java
Object value = ...
if (value instanceof String) {
	execute(name, (String) value)
} else if (value instanceof Integer) {
	execute(name, (Integer) value)
} else ...
```
 into this:
 ```java
 new WrapAdapter(ctx, value).execute(new TypedCommand<String>(){
    @Override
    public void executeString(String ctx, String value) {
    	//execute block if string
    }
    @Override
    public void executeInteger(String ctx, Integer value) {
    	//execute block if integer
    }
});
```
Since 2.0