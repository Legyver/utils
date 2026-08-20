package com.legyver.utils.httpclient;

import com.legyver.utils.httpclient.auth.Auth;
import com.legyver.utils.httpclient.exception.ResponseCodeException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * End-to-end tests against a real local HTTP server (JDK's built-in com.sun.net.httpserver.HttpServer,
 * no extra dependencies) for the things only a real socket can prove: that a retry genuinely issues a
 * second HTTP request (rather than replaying an already-consumed connection), and that response header
 * casing is handled correctly against real, JDK-normalized headers. The retry loop's own decision logic
 * (max retries, backoff, latest-failure-surfaced, interrupts) is covered by RetryAfterHandlerTest.
 */
class RetryAfterIntegrationTest {

    private HttpServer server;

    @AfterEach
    void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void retryPerformsGenuineFreshRequestAgainstRealServer() throws Exception {
        AtomicInteger hitCount = new AtomicInteger();
        List<String> queriesSeen = new CopyOnWriteArrayList<>();
        List<String> customHeaderSeen = new CopyOnWriteArrayList<>();
        int port = startServer(exchange -> {
            queriesSeen.add(exchange.getRequestURI().getQuery());
            customHeaderSeen.add(exchange.getRequestHeaders().getFirst("X-Custom"));
            int attempt = hitCount.incrementAndGet();
            if (attempt == 1) {
                exchange.getResponseHeaders().set("Retry-After", "0");
                exchange.sendResponseHeaders(429, -1);
            } else {
                byte[] body = "ok".getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, body.length);
                exchange.getResponseBody().write(body);
            }
        });

        HttpClient client = HttpClient.builder()
                .addHeader("X-Custom", "header-value")
                .addQueryParameter("q", "1")
                .auth(Auth.BASIC)
                    .username("user")
                    .password("pass")
                .autoHandleRetryAfter()
                .build();
        InputStream response = client.get("http://localhost:" + port + "/resource");

        assertThat(new String(response.readAllBytes(), StandardCharsets.UTF_8)).isEqualTo("ok");
        assertThat(hitCount.get()).isEqualTo(2); //proves a genuine second HTTP request reached the server
        assertThat(queriesSeen).hasSize(2);
        assertThat(queriesSeen.get(0)).isEqualTo(queriesSeen.get(1)); //query params identical, not duplicated
        assertThat(customHeaderSeen).containsExactly("header-value", "header-value"); //header survives retry
    }

    @Test
    void headerCasingIsHandledCaseInsensitivelyEndToEnd() throws Exception {
        AtomicInteger hitCount = new AtomicInteger();
        int port = startServer(exchange -> {
            int attempt = hitCount.incrementAndGet();
            if (attempt == 1) {
                //com.sun.net.httpserver.Headers normalizes this to "Retry-after" regardless of how it's set here
                exchange.getResponseHeaders().set("retry-after", "0");
                exchange.sendResponseHeaders(429, -1);
            } else {
                byte[] body = "ok".getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, body.length);
                exchange.getResponseBody().write(body);
            }
        });

        HttpClient client = HttpClient.builder().autoHandleRetryAfter().build();
        InputStream response = client.get("http://localhost:" + port + "/resource");

        assertThat(new String(response.readAllBytes(), StandardCharsets.UTF_8)).isEqualTo("ok");
        assertThat(hitCount.get()).isEqualTo(2);
    }

    @Test
    void noRetryHandlerConfiguredThrowsResponseCodeExceptionNotNpe() throws Exception {
        //204 is a non-200 status where getInputStream() would have succeeded even under the old buggy
        //code, isolating this test to the builder/constructor null-map fix specifically
        int port = startServer(exchange -> exchange.sendResponseHeaders(204, -1));

        HttpClient client = HttpClient.builder().build(); //no autoHandleRetryAfter() call at all

        assertThatThrownBy(() -> client.get("http://localhost:" + port + "/resource"))
                .isInstanceOf(ResponseCodeException.class)
                .extracting(ex -> ((ResponseCodeException) ex).getResponseCode())
                .isEqualTo(204);
    }

    @Test
    void errorResponseWithBodyThrowsResponseCodeExceptionNotIOException() throws Exception {
        //a non-200 response with a body would have thrown IOException from getInputStream() under the
        //old buggy logging code, isolating this test to the getErrorStream() fix specifically
        int port = startServer(exchange -> {
            byte[] body = "server exploded".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(500, body.length);
            exchange.getResponseBody().write(body);
        });

        HttpClient client = HttpClient.builder().build(); //no retry handler configured

        assertThatThrownBy(() -> client.get("http://localhost:" + port + "/resource"))
                .isInstanceOf(ResponseCodeException.class)
                .extracting(ex -> ((ResponseCodeException) ex).getResponseCode())
                .isEqualTo(500);
    }

    private int startServer(HttpHandlerThrowingIOException handler) throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/resource", exchange -> {
            try {
                handler.handle(exchange);
            } finally {
                exchange.close();
            }
        });
        server.start();
        return server.getAddress().getPort();
    }

    @FunctionalInterface
    private interface HttpHandlerThrowingIOException {
        void handle(HttpExchange exchange) throws IOException;
    }
}
