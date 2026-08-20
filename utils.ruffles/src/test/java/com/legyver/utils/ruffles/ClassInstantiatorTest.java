package com.legyver.utils.ruffles;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ClassInstantiatorTest {

    @Test
    public void instantiatesWithMultipleDifferentlyTypedParameters() throws Exception {
        //regression test: parameterTypes used to be built entirely from parameters[0], so any
        //constructor call with 2+ differently-typed parameters failed to find its own constructor
        ClassInstantiator<TwoArgClass> instantiator = new ClassInstantiator<>(TwoArgClass.class);
        TwoArgClass result = instantiator.getNewInstance("name", 42);
        assertThat(result.name).isEqualTo("name");
        assertThat(result.number).isEqualTo(42);
    }

    @Test
    public void instantiatesWithNoArgs() throws Exception {
        ClassInstantiator<NoArgClass> instantiator = new ClassInstantiator<>(NoArgClass.class);
        NoArgClass result = instantiator.getNewInstance();
        assertThat(result).isNotNull();
    }

    public static class TwoArgClass {
        private final String name;
        private final Integer number;

        public TwoArgClass(String name, Integer number) {
            this.name = name;
            this.number = number;
        }
    }

    public static class NoArgClass {
    }
}
