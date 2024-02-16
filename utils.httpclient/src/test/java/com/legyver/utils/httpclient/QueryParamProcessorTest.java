package com.legyver.utils.httpclient;

import com.legyver.utils.httpclient.internal.QueryParamProcessor;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class QueryParamProcessorTest {

    @Test
    void noQueryParams() {
        {
            QueryParamProcessor queryParamProcessor = new QueryParamProcessor(null);
            assertThat(queryParamProcessor.process("https://my-prescious/inventory")).isEqualTo("https://my-prescious/inventory");
        }
        {
            QueryParamProcessor queryParamProcessor = new QueryParamProcessor(Collections.EMPTY_MAP);
            assertThat(queryParamProcessor.process("https://my-prescious/inventory")).isEqualTo("https://my-prescious/inventory");
        }
    }

    @Test
    void appendQueryParams() {
        Map<String, String> orderedMap = orderedMap(new Tuple("gem", "ruby"), new Tuple("color", "red"));
        QueryParamProcessor queryParamProcessor = new QueryParamProcessor(orderedMap);
        assertThat(queryParamProcessor.process("https://my-prescious/inventory")).isEqualTo("https://my-prescious/inventory?gem=ruby&color=red");
    }

    @Test
    void appendToExistingQueryParams() {
        QueryParamProcessor queryParamProcessor = new QueryParamProcessor(Map.of( "color","red"));
        assertThat(queryParamProcessor.process("https://my-prescious/inventory?gem=ruby")).isEqualTo("https://my-prescious/inventory?gem=ruby&color=red");
    }

    @Test
    void overrideQueryParams() {
        Map<String, String> orderedMap = orderedMap(new Tuple("gem", "ruby"), new Tuple("color", "red"));
        QueryParamProcessor queryParamProcessor = new QueryParamProcessor(orderedMap);
        assertThat(queryParamProcessor.process("https://my-prescious/inventory?gem=saphire")).isEqualTo("https://my-prescious/inventory?gem=ruby&color=red");
    }

    private static class Tuple {
        final String key; final String value;

        private Tuple(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    private Map<String, String> orderedMap(Tuple... tuples) {
        Map linkedHashMap = new LinkedHashMap();
        if (tuples != null) {
            for (Tuple tuple: tuples) {
                linkedHashMap.put(tuple.key, tuple.value);
            }
        }
        return linkedHashMap;
    }

}
