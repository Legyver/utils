package com.legyver.utils.httpclient;

import com.legyver.utils.httpclient.exception.ResponseCodeException;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseCodeExceptionTest {

    @Test
    void retryAfterNoHeaders() {
        ResponseCodeException responseCodeException = new ResponseCodeException("Server unavailable", 503);
        assertThat(responseCodeException.getMessage()).isEqualTo("Server unavailable");
        assertThat(responseCodeException.getResponseCode()).isEqualTo(503);
        assertThat(responseCodeException.getResponseHeader("foo")).isNull();
        assertThat(responseCodeException.getRetryAfterDelaySeconds()).isNull();
        assertThat(responseCodeException.getRetryAfterHttpDate()).isNull();
    }

    @Test
    void retryAfterDelaySeconds() {
        ResponseCodeException responseCodeException = new ResponseCodeException("Server unavailable", 503, Map.of(
                "Retry-After", Arrays.asList(String.valueOf(50))
        ));
        assertThat(responseCodeException.getMessage()).isEqualTo("Server unavailable");
        assertThat(responseCodeException.getResponseCode()).isEqualTo(503);
        assertThat(responseCodeException.getResponseHeader("foo")).isNull();
        assertThat(responseCodeException.getRetryAfterDelaySeconds()).isEqualTo(50);
        assertThat(responseCodeException.getRetryAfterHttpDate()).isNull();
    }

    @Test
    void retryAfterHttpDate() {
        ResponseCodeException responseCodeException = new ResponseCodeException("Server unavailable", 503, Map.of(
                "Retry-After", Arrays.asList("Wed, 06 Mar 2024 11:23:34 GMT")
        ));
        assertThat(responseCodeException.getMessage()).isEqualTo("Server unavailable");
        assertThat(responseCodeException.getResponseCode()).isEqualTo(503);
        assertThat(responseCodeException.getResponseHeader("foo")).isNull();
        assertThat(responseCodeException.getRetryAfterDelaySeconds()).isNull();
        assertThat(responseCodeException.getRetryAfterHttpDate()).isEqualTo(ZonedDateTime.of(
                LocalDate.of(2024, 3, 6),
                LocalTime.of(11,23,34),
                ZoneId.of("GMT")
        ));
    }

}
