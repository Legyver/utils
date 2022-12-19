# utils-mapadapt
An adapter for typed retrieval of values from a map

```groovy
implementation group: 'com.legyver', name: 'utils-mapadapt', version: '3.4.1'
```
## Features
Adapter for typed value retrieval
 ```java
TypedMapAdapter adapter = new TypedMapAdapter(map);
Object object = adapter.getObject("an object");

Boolean aBoolean = adapter.getBoolean("a boolean");
//if the object can be converted to a boolean (like string literal "true"), you'll get the boolean representation
map.put("a boolean string", "true");
Boolean parsedBoolean = adapter.getBoolean("a boolean string");


String aString = adapter.getString("a string");
//if the object can be converted to a string, you'll get the string representation
map.put("a date", LocalDate.now());
map.put("a number", 1.0);
String parsedDateString = adapter.getString("a date string");
String parsedNumberString = adapter.getString("a number string");


Long aLong = adapter.getLong("a long");
//if the object can be converted to a long, this will happen
map.put("a long string", Long.valueOf(100L).toString());
map.put("an integer", 1);
map.put("a big integer", BigInteger.valueOf(1000));
Long parsedStringLong = adapter.getLong("a long string");
Long convertedIntegerLong = adapter.getLong("an integer");
Long convertedBigIntegerLong = adapter.getLong("a big integer");


Integer anInteger = adapter.getInteger("an integer");
//if the object can be converted to an integer, this will happen
map.put("a string", "100");
map.put("a long", 200L);
map.put("a big integer", BigInteger.valueOf(1000));
Integer parsedStringInteger = adapter.getInteger("a long string");
Integer convertedLongInteger = adapter.getInteger("an integer");
Integer convertedBigIntegerInteger = adapter.getInteger("a big integer");


Double aDouble = adapter.getDouble("a double");
//if the object can be converted to a double, this will happen
map.put("a string", "6.5");
map.put("a long", 7L);
map.put("an integer", 6);
map.put("a big integer", BigInteger.valueOf(1000));
map.put("a big decimal", BigDecimal.valueOf(1000.23));
Double parsedDouble = adapter.getDouble("a string");
Double convertedLongDouble = adapter.getDouble("a long");
Double convertedIntegerDouble = adapter.getDouble("an integer");
Double convertedBigIntegerDouble = adapter.getDouble("a big integer");
Double convertedBigDecimalDouble = adapter.getDouble("a big decimal");


BigInteger aBigInteger = adapter.getBigInteger("a big int");
//if the object can be converted to a BigInteger, this will happen
map.put("a string", "5");
map.put("a long", 7L);
map.put("an integer", 6);
BigInteger parsedBigInt = adapter.getBigInteger("a string");
BigInteger convertedLongBigInt = adapter.getBigInteger("a long");
BigInteger convertedIntegerBigInt = adapter.getBigInteger("an integer");


BigDecimal aBigDecimal = adapter.getBigDecimal("a big decimal");
//if the object can be converted to a BigInteger, this will happen
map.put("a string", "6.5");
map.put("a long", 7L);
map.put("an integer", 6);
map.put("a double", 11.2);
map.put("a big integer", BigInteger.valueOf(1200));
BigDecimal parsedBigDecimal= adapter.getBigDecimal("a string");
BigDecimal convertedLongBigDecimal = adapter.getBigDecimal("a long");
BigDecimal convertedIntegerBigDecimal = adapter.getBigDecimal("an integer");
BigDecimal convertedDoubleBigDecimal = adapter.getBigDecimal("a double");
BigDecimal convertedBigIntegerBigDecimal = adapter.getBigDecimal("a big integer");


LocalDate aLocalDate = adapter.getLocalDate("a local date");
//if the object can be converted to a LocalDate, this will happen
map.put("a string date", "2012-12-12");
map.put("a string date time", "2012-12-12T15:00");
map.put("a local date time", LocalDateTime.now());
LocalDate parsedLocalDate = adapter.getLocalDate("a local date time");
LocalDate parsedLocalDateTimeAsLocalDate = adapter.getLocalDate("a string date time");
LocalDate convertedLocalDateTimeToLocalDate = adapter.getLocalDate("a local date time");


LocalDateTime aLocalDateTime = adapter.getLocalDateTime("a local date time");
//if the object can be converted to a LocalDateTime, this will happen
map.put("a string date time", "2012-12-12T15:00");
map.put("a string date", "2012-12-12");//this will be adjusted to beginning of day
map.put("a local date", LocalDate.now());//this will be adjusted to beginning of day
LocalDateTime parsedLocalDateTime = adapter.getLocalDateTime("a string date time");
LocalDateTime parsedLocalDateAsLocalDateTime = adapter.getLocalDateTime("a string date");
LocalDateTime convertedLocalDate = adapter.getLocalDateTime("a local date");


LocalTime aLocalTime = adapter.getLocalTime("a local date time");
//if the object can be converted to a LocalTime, this will happen
map.put("a string time", "08:00");
map.put("a string date time", "2012-12-12T15:00");
map.put("a local date time", LocalDateTime.now());
LocalTime parsedLocalTime = adapter.getLocalTime("a string time");
LocalTime parsedLocalDateTimeAsLocalTime = adapter.getLocalTime("a string date time");
LocalTime convertedLocalDateTime = adapter.getLocalTime("a local date");
```
Since 2.1.2.0