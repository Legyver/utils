package com.legyver.utils.httpclient;

import com.legyver.utils.httpclient.internal.PathVariableProcessor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PathVariableProcessorTest {

    @Test
    void noPathVariablesInUrl() {
        PathVariableProcessor pathVariableProcessor = new PathVariableProcessor("william", "harry");
        assertThat(pathVariableProcessor.process("https://example.com/prince/abert")).isEqualTo("https://example.com/prince/abert");
    }
    @Test
    void noPathVariablesProvided() {
        PathVariableProcessor pathVariableProcessor = new PathVariableProcessor();
        assertThat(pathVariableProcessor.process("https://example.com/prince/abert")).isEqualTo("https://example.com/prince/abert");
    }

    @Test
    void pathVariablesProvided() {
        PathVariableProcessor pathVariableProcessor = new PathVariableProcessor("william", "harry");
        assertThat(pathVariableProcessor.process("https://example.com/prince/{nextKing}")).isEqualTo("https://example.com/prince/william");
    }

    @Test
    void pathVariablesProvidedWithHyphens() {
        PathVariableProcessor pathVariableProcessor = new PathVariableProcessor("william", "harry");
        assertThat(pathVariableProcessor.process("https://example.com/prince/{my-liege}")).isEqualTo("https://example.com/prince/william");
    }
    @Test
    void multiplePathVariablesProvidedWithNumbers() {
        PathVariableProcessor pathVariableProcessor = new PathVariableProcessor("william", "harry","harriet");
        assertThat(pathVariableProcessor.process("https://example.com/order/{1st}/then/{2nd}")).isEqualTo("https://example.com/order/william/then/harry");
    }
    @Test
    void multiplePathVariablesSameNumberVariablesAsProvided() {
        PathVariableProcessor pathVariableProcessor = new PathVariableProcessor("william", "harry","harriet");
        assertThat(pathVariableProcessor.process("https://example.com/order/{1st}/then/{2nd}/never/{last}")).isEqualTo("https://example.com/order/william/then/harry/never/harriet");
    }

    @Test
    void multiplePathVariablesMoreVariablesThanProvided() {
        PathVariableProcessor pathVariableProcessor = new PathVariableProcessor("william", "harry");
        assertThat(pathVariableProcessor.process("https://example.com/order/{1st}/then/{2nd}/never/{last}")).isEqualTo("https://example.com/order/william/then/harry/never/{last}");
    }
}
