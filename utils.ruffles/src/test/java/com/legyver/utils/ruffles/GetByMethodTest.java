package com.legyver.utils.ruffles;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

public class GetByMethodTest {

    @Test
    public void fallsBackToDirectFieldReadWhenNoGetterExists() throws Exception {
        //regression test: the getter-not-found fallback used to call getByField(field) - passing
        //the Field itself instead of the target instance - which broke the whole fallback path
        Field field = NoGetterHolder.class.getDeclaredField("value");
        NoGetterHolder holder = new NoGetterHolder("field-value");

        Object result = new GetByMethod(field).get(holder);

        assertThat(result).isEqualTo("field-value");
    }

    public static class NoGetterHolder {
        private String value;

        public NoGetterHolder(String value) {
            this.value = value;
        }
    }
}
