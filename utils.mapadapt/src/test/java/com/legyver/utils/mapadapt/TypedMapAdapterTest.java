package com.legyver.utils.mapadapt;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class TypedMapAdapterTest {
    private static final Map<String, Object> map = new HashMap<>();
    //keys
    public static final String ANULL = "anull";
    public static final String ANOBJECT = "anobject";
    public static final String ABOOLEAN = "aboolean";
    public static final String ASTRING = "astring";
    public static final String ALONG = "along";
    public static final String ADOUBLE = "adouble";
    public static final String ANINTEGER = "aninteger";
    public static final String ABIGINTEGER = "abiginteger";
    public static final String ABIGDECIMAL = "abigdecimal";
    public static final String ALOCALDATE = "alocaldate";
    public static final String ALOCALDATETIME = "alocaldatetime";
    public static final String ALOCALTIME = "alocaltime";
    //values
    public static final Object VALUE_OBJECT = new TestObject();
    public static final Boolean VALUE_BOOLEAN = true;
    public static final String VALUE_STRING = "A String";
    public static final Long VALUE_LONG = 1L;
    public static final Double VALUE_DOUBLE = 1.3;
    public static final Integer VALUE_INTEGER = 2;
    public static final BigInteger VALUE_BIGINTEGER = BigInteger.valueOf(3);
    public static final BigDecimal VALUE_BIGDECIMAL = BigDecimal.valueOf(4.5);
    public static final LocalDate VALUE_LOCALDATE = LocalDate.now().minusDays(1);
    public static final LocalDateTime VALUE_LOCALDATETIME = LocalDateTime.now();
    public static final LocalTime VALUE_LOCALTIME = LocalTime.now().plusHours(1);

    @BeforeAll
    static void initValues() {
        map.put(ANULL, null);
        map.put(ANOBJECT, VALUE_OBJECT);
        map.put(ABOOLEAN, VALUE_BOOLEAN);
        map.put(ASTRING, VALUE_STRING);
        map.put(ALONG, VALUE_LONG);
        map.put(ADOUBLE, VALUE_DOUBLE);
        map.put(ANINTEGER, VALUE_INTEGER);
        map.put(ABIGINTEGER, VALUE_BIGINTEGER);
        map.put(ABIGDECIMAL, VALUE_BIGDECIMAL);
        map.put(ALOCALDATE, VALUE_LOCALDATE);
        map.put(ALOCALDATETIME, VALUE_LOCALDATETIME);
        map.put(ALOCALTIME, VALUE_LOCALTIME);
    }

    @Test
    public void getObject() {
        TypedMapAdapter adapter = new TypedMapAdapter(map);
        assertThat(adapter.getObject(ANULL)).isNull();
        assertThat(adapter.getObject(ANOBJECT)).isEqualTo(VALUE_OBJECT);
        assertThat(adapter.getObject(ABOOLEAN)).isEqualTo(VALUE_BOOLEAN);
        assertThat(adapter.getObject(ASTRING)).isEqualTo(VALUE_STRING);
        assertThat(adapter.getObject(ALONG)).isEqualTo(VALUE_LONG);
        assertThat(adapter.getObject(ADOUBLE)).isEqualTo(VALUE_DOUBLE);
        assertThat(adapter.getObject(ANINTEGER)).isEqualTo(VALUE_INTEGER);
        assertThat(adapter.getObject(ABIGINTEGER)).isEqualTo(VALUE_BIGINTEGER);
        assertThat(adapter.getObject(ABIGDECIMAL)).isEqualTo(VALUE_BIGDECIMAL);
        assertThat(adapter.getObject(ALOCALDATE)).isEqualTo(VALUE_LOCALDATE);
        assertThat(adapter.getObject(ALOCALDATETIME)).isEqualTo(VALUE_LOCALDATETIME);
        assertThat(adapter.getObject(ALOCALTIME)).isEqualTo(VALUE_LOCALTIME);
    }

    @Test
    public void getString() {
        TypedMapAdapter adapter = new TypedMapAdapter(map);
        assertThat(adapter.getString(ANULL)).isNull();
        //everything else should be converted toString()
        assertThat(adapter.getString(ANOBJECT)).isEqualTo(VALUE_OBJECT.toString());
        assertThat(adapter.getString(ABOOLEAN)).isEqualTo(VALUE_BOOLEAN.toString());
        assertThat(adapter.getString(ASTRING)).isEqualTo(VALUE_STRING);
        assertThat(adapter.getString(ALONG)).isEqualTo(VALUE_LONG.toString());
        assertThat(adapter.getString(ADOUBLE)).isEqualTo(VALUE_DOUBLE.toString());
        assertThat(adapter.getString(ANINTEGER)).isEqualTo(VALUE_INTEGER.toString());
        assertThat(adapter.getString(ABIGINTEGER)).isEqualTo(VALUE_BIGINTEGER.toString());
        assertThat(adapter.getString(ABIGDECIMAL)).isEqualTo(VALUE_BIGDECIMAL.toString());
        assertThat(adapter.getString(ALOCALDATE)).isEqualTo(VALUE_LOCALDATE.toString());
        assertThat(adapter.getString(ALOCALDATETIME)).isEqualTo(VALUE_LOCALDATETIME.toString());
        assertThat(adapter.getString(ALOCALTIME)).isEqualTo(VALUE_LOCALTIME.toString());
    }

    @Test
    public void getBoolean() {
        TypedMapAdapter adapter = new TypedMapAdapter(map);
        map.put("string_boolean", Boolean.TRUE.toString());
        assertThat(adapter.getBoolean(ABOOLEAN)).isEqualTo(VALUE_BOOLEAN);
        assertThat(adapter.getBoolean("string_boolean")).isEqualTo(Boolean.TRUE);

        //everything else null
        assertThat(adapter.getBoolean(ANULL)).isNull();
        assertThat(adapter.getBoolean(ANOBJECT)).isNull();
        assertThat(adapter.getBoolean(ASTRING)).isNull();
        assertThat(adapter.getBoolean(ALONG)).isNull();
        assertThat(adapter.getBoolean(ADOUBLE)).isNull();
        assertThat(adapter.getBoolean(ANINTEGER)).isNull();
        assertThat(adapter.getBoolean(ABIGINTEGER)).isNull();
        assertThat(adapter.getBoolean(ABIGDECIMAL)).isNull();
        assertThat(adapter.getBoolean(ALOCALDATE)).isNull();
        assertThat(adapter.getBoolean(ALOCALDATETIME)).isNull();
        assertThat(adapter.getBoolean(ALOCALTIME)).isNull();
    }

    @Test
    public void getLong() {
        TypedMapAdapter adapter = new TypedMapAdapter(map);
        //these we can handle
        map.put("a string number" , "1");
        map.put("a string decimal" , "2.0");
        assertThat(adapter.getLong(ALONG)).isEqualTo(VALUE_LONG);
        assertThat(adapter.getLong(ANINTEGER)).isEqualTo(Long.valueOf(VALUE_INTEGER));
        assertThat(adapter.getLong(ABIGINTEGER)).isEqualTo(VALUE_BIGINTEGER.longValue());
        assertThat(adapter.getLong("a string number")).isEqualTo(1L);

        //everything else null
        assertThat(adapter.getLong("a string decimal")).isNull();
        assertThat(adapter.getLong(ADOUBLE)).isNull();
        assertThat(adapter.getLong(ABIGDECIMAL)).isNull();
        assertThat(adapter.getLong(ANULL)).isNull();
        assertThat(adapter.getLong(ANOBJECT)).isNull();
        assertThat(adapter.getLong(ABOOLEAN)).isNull();
        assertThat(adapter.getLong(ASTRING)).isNull();
        assertThat(adapter.getLong(ALOCALDATE)).isNull();
        assertThat(adapter.getLong(ALOCALDATETIME)).isNull();
        assertThat(adapter.getLong(ALOCALTIME)).isNull();
    }

    @Test
    public void getInteger() {
        TypedMapAdapter adapter = new TypedMapAdapter(map);
        //these we can handle
        map.put("a string number" , "1");
        map.put("a string decimal" , "2.0");
        assertThat(adapter.getInteger(ALONG)).isEqualTo(VALUE_LONG.intValue());
        assertThat(adapter.getInteger(ANINTEGER)).isEqualTo(VALUE_INTEGER);
        assertThat(adapter.getInteger(ABIGINTEGER)).isEqualTo(VALUE_BIGINTEGER.intValue());
        assertThat(adapter.getInteger("a string number")).isEqualTo(1);

        //everything else null
        assertThat(adapter.getInteger("a string decimal")).isNull();
        assertThat(adapter.getInteger(ADOUBLE)).isNull();
        assertThat(adapter.getInteger(ABIGDECIMAL)).isNull();
        assertThat(adapter.getInteger(ANULL)).isNull();
        assertThat(adapter.getInteger(ANOBJECT)).isNull();
        assertThat(adapter.getInteger(ABOOLEAN)).isNull();
        assertThat(adapter.getInteger(ASTRING)).isNull();
        assertThat(adapter.getInteger(ALOCALDATE)).isNull();
        assertThat(adapter.getInteger(ALOCALDATETIME)).isNull();
        assertThat(adapter.getInteger(ALOCALTIME)).isNull();
    }

    @Test
    public void getDouble() {
        TypedMapAdapter adapter = new TypedMapAdapter(map);
        map.put("a string number" , "1");
        map.put("a string decimal" , "2.0");
        //these we can handle
        assertThat(adapter.getDouble(ALONG)).isEqualTo(VALUE_LONG.doubleValue());
        assertThat(adapter.getDouble(ANINTEGER)).isEqualTo(VALUE_INTEGER.doubleValue());
        assertThat(adapter.getDouble(ABIGINTEGER)).isEqualTo(VALUE_BIGINTEGER.doubleValue());
        assertThat(adapter.getDouble(ADOUBLE)).isEqualTo(VALUE_DOUBLE);
        assertThat(adapter.getDouble(ABIGDECIMAL)).isEqualTo(VALUE_BIGDECIMAL.doubleValue());
        assertThat(adapter.getDouble("a string number")).isEqualTo(1.0);
        assertThat(adapter.getDouble("a string decimal")).isEqualTo(2.0);

        //everything else null
        assertThat(adapter.getDouble(ANULL)).isNull();
        assertThat(adapter.getDouble(ANOBJECT)).isNull();
        assertThat(adapter.getDouble(ABOOLEAN)).isNull();
        assertThat(adapter.getDouble(ASTRING)).isNull();
        assertThat(adapter.getDouble(ALOCALDATE)).isNull();
        assertThat(adapter.getDouble(ALOCALDATETIME)).isNull();
        assertThat(adapter.getDouble(ALOCALTIME)).isNull();
    }

    @Test
    public void getBigDecimal() {
        TypedMapAdapter adapter = new TypedMapAdapter(map);
        //these we can handle
        assertThat(adapter.getBigDecimal(ALONG)).isEqualTo(BigDecimal.valueOf(VALUE_LONG));
        assertThat(adapter.getBigDecimal(ANINTEGER)).isEqualTo(BigDecimal.valueOf(VALUE_INTEGER.doubleValue()));
        assertThat(adapter.getBigDecimal(ABIGINTEGER)).isEqualTo(BigDecimal.valueOf(VALUE_BIGINTEGER.doubleValue()));
        assertThat(adapter.getBigDecimal(ADOUBLE)).isEqualTo(BigDecimal.valueOf(VALUE_DOUBLE));
        assertThat(adapter.getBigDecimal(ABIGDECIMAL)).isEqualTo(VALUE_BIGDECIMAL);

        //everything else null
        assertThat(adapter.getBigDecimal(ANULL)).isNull();
        assertThat(adapter.getBigDecimal(ANOBJECT)).isNull();
        assertThat(adapter.getBigDecimal(ABOOLEAN)).isNull();
        assertThat(adapter.getBigDecimal(ASTRING)).isNull();
        assertThat(adapter.getBigDecimal(ALOCALDATE)).isNull();
        assertThat(adapter.getBigDecimal(ALOCALDATETIME)).isNull();
        assertThat(adapter.getBigDecimal(ALOCALTIME)).isNull();
    }

    @Test
    public void getBigInteger() {
        TypedMapAdapter adapter = new TypedMapAdapter(map);
        //these we can handle
        map.put("a string number" , "1");
        map.put("a string decimal" , "2.0");
        assertThat(adapter.getBigInteger(ALONG)).isEqualTo(BigInteger.valueOf(VALUE_LONG));
        assertThat(adapter.getBigInteger(ANINTEGER)).isEqualTo(BigInteger.valueOf(VALUE_INTEGER));
        assertThat(adapter.getBigInteger(ABIGINTEGER)).isEqualTo(VALUE_BIGINTEGER);
        assertThat(adapter.getBigInteger("a string number")).isEqualTo(BigInteger.valueOf(1));

        //everything else null
        assertThat(adapter.getBigInteger("a string decimal")).isNull();
        assertThat(adapter.getBigInteger(ADOUBLE)).isNull();
        assertThat(adapter.getBigInteger(ABIGDECIMAL)).isNull();
        assertThat(adapter.getBigInteger(ANULL)).isNull();
        assertThat(adapter.getBigInteger(ANOBJECT)).isNull();
        assertThat(adapter.getBigInteger(ABOOLEAN)).isNull();
        assertThat(adapter.getBigInteger(ASTRING)).isNull();
        assertThat(adapter.getBigInteger(ALOCALDATE)).isNull();
        assertThat(adapter.getBigInteger(ALOCALDATETIME)).isNull();
        assertThat(adapter.getBigInteger(ALOCALTIME)).isNull();
    }


    @Test
    public void getLocalDate() {
        TypedMapAdapter adapter = new TypedMapAdapter(map);
        LocalDate now = LocalDate.now();
        map.put("string_local", now.toString());
        map.put("string_localdatetime", LocalDateTime.now().toString());
        map.put("string_localtime", LocalTime.now().toString());
        //these we can handle
        assertThat(adapter.getLocalDate(ALOCALDATE)).isEqualTo(VALUE_LOCALDATE);
        assertThat(adapter.getLocalDate(ALOCALDATETIME)).isEqualTo(VALUE_LOCALDATETIME.toLocalDate());
        assertThat(adapter.getLocalDate("string_local")).isEqualTo(now);
        assertThat(adapter.getLocalDate("string_localdatetime")).isEqualTo(now);

        //everything else null
        assertThat(adapter.getLocalDate("string_localtime")).isNull();
        assertThat(adapter.getLocalDate(ALOCALTIME)).isNull();
        assertThat(adapter.getLocalDate(ANULL)).isNull();
        assertThat(adapter.getLocalDate(ANOBJECT)).isNull();
        assertThat(adapter.getLocalDate(ABOOLEAN)).isNull();
        assertThat(adapter.getLocalDate(ASTRING)).isNull();
        assertThat(adapter.getLocalDate(ALONG)).isNull();
        assertThat(adapter.getLocalDate(ADOUBLE)).isNull();
        assertThat(adapter.getLocalDate(ANINTEGER)).isNull();
        assertThat(adapter.getLocalDate(ABIGINTEGER)).isNull();
        assertThat(adapter.getLocalDate(ABIGDECIMAL)).isNull();
    }

    @Test
    public void getLocalDateTime() {
        TypedMapAdapter adapter = new TypedMapAdapter(map);
        LocalDateTime now = LocalDateTime.now();
        map.put("string_local", LocalDate.now().toString());
        map.put("string_localdatetime", now);
        map.put("string_localtime", LocalTime.now().toString());
        //these we can handle

        assertThat(adapter.getLocalDateTime(ALOCALDATETIME)).isEqualTo(VALUE_LOCALDATETIME);
        assertThat(adapter.getLocalDateTime("string_localdatetime")).isEqualTo(now);
        assertThat(adapter.getLocalDateTime("string_local")).isEqualTo(LocalDate.now().atStartOfDay());
        assertThat(adapter.getLocalDateTime(ALOCALDATE)).isEqualTo(VALUE_LOCALDATE.atStartOfDay());

        //everything else null
        assertThat(adapter.getLocalDateTime("string_localtime")).isNull();
        assertThat(adapter.getLocalDateTime(ALOCALTIME)).isNull();
        assertThat(adapter.getLocalDateTime(ANULL)).isNull();
        assertThat(adapter.getLocalDateTime(ANOBJECT)).isNull();
        assertThat(adapter.getLocalDateTime(ABOOLEAN)).isNull();
        assertThat(adapter.getLocalDateTime(ASTRING)).isNull();
        assertThat(adapter.getLocalDateTime(ALONG)).isNull();
        assertThat(adapter.getLocalDateTime(ADOUBLE)).isNull();
        assertThat(adapter.getLocalDateTime(ANINTEGER)).isNull();
        assertThat(adapter.getLocalDateTime(ABIGINTEGER)).isNull();
        assertThat(adapter.getLocalDateTime(ABIGDECIMAL)).isNull();
    }

    @Test
    public void getLocalTime() {
        TypedMapAdapter adapter = new TypedMapAdapter(map);
        LocalTime now = LocalTime.now();
        LocalDateTime nowLDT = LocalDateTime.now();
        map.put("string_local", LocalDate.now().toString());
        map.put("string_localdatetime", nowLDT.toString());
        map.put("string_localtime", now);

        //these we can handle
        assertThat(adapter.getLocalTime(ALOCALDATETIME)).isEqualTo(VALUE_LOCALDATETIME.toLocalTime());
        assertThat(adapter.getLocalTime("string_localdatetime")).isEqualTo(nowLDT.toLocalTime());
        assertThat(adapter.getLocalTime("string_localtime")).isEqualTo(now);
        assertThat(adapter.getLocalTime(ALOCALTIME)).isEqualTo(VALUE_LOCALTIME);

        //everything else null
        assertThat(adapter.getLocalTime("string_local")).isNull();
        assertThat(adapter.getLocalTime(ALOCALDATE)).isNull();
        assertThat(adapter.getLocalTime(ANULL)).isNull();
        assertThat(adapter.getLocalTime(ANOBJECT)).isNull();
        assertThat(adapter.getLocalTime(ABOOLEAN)).isNull();
        assertThat(adapter.getLocalTime(ASTRING)).isNull();
        assertThat(adapter.getLocalTime(ALONG)).isNull();
        assertThat(adapter.getLocalTime(ADOUBLE)).isNull();
        assertThat(adapter.getLocalTime(ANINTEGER)).isNull();
        assertThat(adapter.getLocalTime(ABIGINTEGER)).isNull();
        assertThat(adapter.getLocalTime(ABIGDECIMAL)).isNull();
    }


    private static class TestObject {
        private String value = "test";

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestObject that = (TestObject) o;

            return Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return value != null ? value.hashCode() : 0;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
