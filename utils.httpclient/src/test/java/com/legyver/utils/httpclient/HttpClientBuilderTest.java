package com.legyver.utils.httpclient;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpClientBuilderTest {

    @Test
    public void addsRequestHeaders() {
        HttpClient client = HttpClient.builder()
                .addHeader("one", "two")
                .addHeader("abc", "123")
                .build();
        assertThat(client.getDefaultHeaders()).contains(
                Assertions.entry("one", "two"),
                Assertions.entry("abc", "123")
        );
        assertThat(client.getDefaultQueryParams()).isNullOrEmpty();
    }

    @Test
    public void addsRequestQueryParameters() {
        HttpClient client = HttpClient.builder()
                .addQueryParameter("one", "two")
                .addQueryParameter("abc", "123")
                .build();
        assertThat(client.getDefaultQueryParams()).contains(
                Assertions.entry("one", "two"),
                Assertions.entry("abc", "123")
        );
        assertThat(client.getDefaultHeaders()).isNullOrEmpty();
    }


}
