package com.legyver.utils.httpclient.internal;

import com.legyver.core.exception.CoreException;
import com.legyver.core.function.ThrowingSupplier;
import com.legyver.utils.httpclient.exception.ResponseCodeException;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Pure unit tests for the retry loop itself, driven with a hand-rolled scripted
 * {@link ThrowingSupplier} - no real HTTP server, no JPMS involvement. Uses tiny
 * Retry-After/backoff values throughout so tests stay fast, except for the one test
 * that specifically proves the sleep path is honored.
 */
class RetryAfterHandlerTest {

    @Test
    void constructorRejectsInvalidArguments() {
        assertThatThrownBy(() -> new RetryAfterHandler(429, -1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new RetryAfterHandler(429, 1, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new RetryAfterHandler(429, 1, -1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void handlesTrueForMatchingStatusRegardlessOfHeader() {
        RetryAfterHandler handler = new RetryAfterHandler(429);
        assertThat(handler.handles(responseCodeException(429, "0"))).isTrue();
        assertThat(handler.handles(new ResponseCodeException("msg", 429))).isTrue();
        assertThat(handler.handles(new ResponseCodeException("msg", 500))).isFalse();
    }

    @Test
    void explicitRetryAfterDelayIsHonoredAndRetrySucceeds() throws Exception {
        RetryAfterHandler handler = new RetryAfterHandler(429, 3, 1);
        ScriptedSupplier supplier = ScriptedSupplier.succeedsImmediately();
        InputStream result = handler.retry(responseCodeException(429, "0"), supplier);
        assertThat(result).isNotNull();
        assertThat(supplier.invocations()).isEqualTo(1);
    }

    @Test
    void noRetryAfterHeaderFallsBackToDefaultBackoffAndSucceeds() throws Exception {
        RetryAfterHandler handler = new RetryAfterHandler(429, 3, 1);
        ResponseCodeException initialFailure = new ResponseCodeException("msg", 429); //no headers at all
        ScriptedSupplier supplier = ScriptedSupplier.succeedsImmediately();
        InputStream result = handler.retry(initialFailure, supplier);
        assertThat(result).isNotNull();
        assertThat(supplier.invocations()).isEqualTo(1);
    }

    @Test
    void multipleSequentialFailuresRetriedUpToMaxUsingLatestFailureEachTime() throws Exception {
        RetryAfterHandler handler = new RetryAfterHandler(429, 3, 1);
        ScriptedSupplier supplier = ScriptedSupplier.failsNTimesThenSucceeds(2, 429, "0");
        InputStream result = handler.retry(responseCodeException(429, "0"), supplier);
        assertThat(result).isNotNull();
        assertThat(supplier.invocations()).isEqualTo(3);
    }

    @Test
    void exceedingMaxRetriesSurfacesLatestFailureNotStaleOriginal() {
        RetryAfterHandler handler = new RetryAfterHandler(429, 2, 1);
        ResponseCodeException initialFailure = responseCodeException(429, "0", "initial-failure");
        ScriptedSupplier supplier = ScriptedSupplier.alwaysFails(429, "0");
        assertThatThrownBy(() -> handler.retry(initialFailure, supplier))
                .isInstanceOf(ResponseCodeException.class)
                .isNotSameAs(initialFailure)
                .hasMessage("attempt-1");
        assertThat(supplier.invocations()).isEqualTo(2);
    }

    @Test
    void nonRetryableStatusFromRetryAttemptSurfacesThatFailureImmediately() {
        RetryAfterHandler handler = new RetryAfterHandler(429, 3, 1);
        ResponseCodeException initialFailure = responseCodeException(429, "0");
        ScriptedSupplier supplier = ScriptedSupplier.failsOnceWithDifferentStatus(500);
        assertThatThrownBy(() -> handler.retry(initialFailure, supplier))
                .isInstanceOf(ResponseCodeException.class)
                .extracting(ex -> ((ResponseCodeException) ex).getResponseCode())
                .isEqualTo(500);
        assertThat(supplier.invocations()).isEqualTo(1);
    }

    @Test
    void interruptedDuringWaitRestoresInterruptFlagAndRethrowsLatestFailure() throws Exception {
        RetryAfterHandler handler = new RetryAfterHandler(429, 3, 5000); //long backoff so there's time to interrupt
        ResponseCodeException initialFailure = new ResponseCodeException("msg", 429); //no header -> uses backoff
        ScriptedSupplier supplier = ScriptedSupplier.succeedsImmediately();
        AtomicReference<Throwable> caught = new AtomicReference<>();
        Thread worker = new Thread(() -> {
            try {
                handler.retry(initialFailure, supplier);
            } catch (Throwable t) {
                caught.set(t);
            }
        });
        worker.start();
        Thread.sleep(200); //give it time to enter Thread.sleep before interrupting
        worker.interrupt();
        worker.join(2000);
        assertThat(worker.isAlive()).isFalse();
        assertThat(caught.get()).isSameAs(initialFailure);
        assertThat(supplier.invocations()).isEqualTo(0);
    }

    @Test
    void actualRetryAfterDelayIsHonored() throws Exception {
        RetryAfterHandler handler = new RetryAfterHandler(429);
        ScriptedSupplier supplier = ScriptedSupplier.succeedsImmediately();
        long start = System.nanoTime();
        handler.retry(responseCodeException(429, "1"), supplier);
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;
        assertThat(elapsedMs).isGreaterThanOrEqualTo(900); //~1s, allow slack
    }

    private static ResponseCodeException responseCodeException(int statusCode, String retryAfter) {
        return responseCodeException(statusCode, retryAfter, "msg");
    }

    private static ResponseCodeException responseCodeException(int statusCode, String retryAfter, String message) {
        Map<String, List<String>> headers = retryAfter == null ? null : Map.of(ResponseCodeException.RETRY_AFTER, Arrays.asList(retryAfter));
        return new ResponseCodeException(message, statusCode, headers);
    }

    /**
     * A scripted {@link ThrowingSupplier} that returns/throws a pre-programmed sequence of
     * outcomes, one per invocation, and counts how many times it was invoked.
     */
    private static class ScriptedSupplier implements ThrowingSupplier<InputStream> {
        private final Deque<Outcome> script;
        private int invocations = 0;

        private ScriptedSupplier(Deque<Outcome> script) {
            this.script = script;
        }

        @Override
        public InputStream get() throws CoreException {
            invocations++;
            Outcome outcome = script.poll();
            if (outcome == null) {
                throw new IllegalStateException("Script exhausted after " + invocations + " invocations");
            }
            return outcome.apply();
        }

        int invocations() {
            return invocations;
        }

        static ScriptedSupplier succeedsImmediately() {
            Deque<Outcome> script = new ArrayDeque<>();
            script.add(Outcome.success());
            return new ScriptedSupplier(script);
        }

        static ScriptedSupplier failsNTimesThenSucceeds(int failCount, int statusCode, String retryAfter) {
            Deque<Outcome> script = new ArrayDeque<>();
            for (int i = 0; i < failCount; i++) {
                script.add(Outcome.failure(statusCode, retryAfter, "attempt-" + i));
            }
            script.add(Outcome.success());
            return new ScriptedSupplier(script);
        }

        static ScriptedSupplier alwaysFails(int statusCode, String retryAfter) {
            Deque<Outcome> script = new ArrayDeque<>();
            for (int i = 0; i < 100; i++) { //plenty of entries for any maxRetries used in tests
                script.add(Outcome.failure(statusCode, retryAfter, "attempt-" + i));
            }
            return new ScriptedSupplier(script);
        }

        static ScriptedSupplier failsOnceWithDifferentStatus(int statusCode) {
            Deque<Outcome> script = new ArrayDeque<>();
            script.add(Outcome.failure(statusCode, null, "different-status"));
            return new ScriptedSupplier(script);
        }

        private interface Outcome {
            InputStream apply() throws CoreException;

            static Outcome success() {
                return () -> new ByteArrayInputStream("ok".getBytes(StandardCharsets.UTF_8));
            }

            static Outcome failure(int statusCode, String retryAfter, String message) {
                return () -> {
                    Map<String, List<String>> headers = retryAfter == null ? null : Map.of(ResponseCodeException.RETRY_AFTER, Arrays.asList(retryAfter));
                    throw new ResponseCodeException(message, statusCode, headers);
                };
            }
        }
    }
}
