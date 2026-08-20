package com.legyver.utils.httpclient.internal;

import com.legyver.core.exception.CoreException;
import com.legyver.core.function.ThrowingSupplier;
import com.legyver.utils.httpclient.exception.ErrorResponseCodeRetryHandler;
import com.legyver.utils.httpclient.exception.ResponseCodeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Retry an action, honoring a 'Retry-After' response header when present and falling back to a
 * default exponential backoff otherwise, up to a bounded number of additional attempts.
 */
public class RetryAfterHandler implements ErrorResponseCodeRetryHandler {
    private static final Logger logger = LogManager.getLogger(RetryAfterHandler.class);

    /**
     * Default number of retry attempts (in addition to the initial failed attempt) before giving up.
     */
    public static final int DEFAULT_MAX_RETRIES = 3;

    /**
     * Default base delay (in milliseconds) for the exponential backoff used when no 'Retry-After' value is present.
     */
    public static final long DEFAULT_BASE_BACKOFF_MILLIS = 1000L;

    private final int statusCode;
    private final int maxRetries;
    private final long baseBackoffMillis;

    /**
     * Construct a Retry-After handler for the status code, using {@link #DEFAULT_MAX_RETRIES} and {@link #DEFAULT_BASE_BACKOFF_MILLIS}
     * @param statusCode the status code for which to apply the handler
     */
    public RetryAfterHandler(int statusCode) {
        this(statusCode, DEFAULT_MAX_RETRIES);
    }

    /**
     * Construct a Retry-After handler for the status code with a bounded number of retries, using {@link #DEFAULT_BASE_BACKOFF_MILLIS}
     * @param statusCode the status code for which to apply the handler
     * @param maxRetries maximum number of additional attempts (beyond the first failed attempt) before giving up. Must be &gt;= 0
     */
    public RetryAfterHandler(int statusCode, int maxRetries) {
        this(statusCode, maxRetries, DEFAULT_BASE_BACKOFF_MILLIS);
    }

    /**
     * Construct a Retry-After handler for the status code with a bounded number of retries and a configurable backoff base
     * @param statusCode the status code for which to apply the handler
     * @param maxRetries maximum number of additional attempts (beyond the first failed attempt) before giving up. Must be &gt;= 0
     * @param baseBackoffMillis the base delay (in milliseconds) for the exponential backoff used when no 'Retry-After' value is present. Must be &gt; 0
     */
    public RetryAfterHandler(int statusCode, int maxRetries, long baseBackoffMillis) {
        if (maxRetries < 0) {
            throw new IllegalArgumentException("maxRetries must be >= 0");
        }
        if (baseBackoffMillis <= 0) {
            throw new IllegalArgumentException("baseBackoffMillis must be > 0");
        }
        this.statusCode = statusCode;
        this.maxRetries = maxRetries;
        this.baseBackoffMillis = baseBackoffMillis;
    }

    /**
     * Tests if the response code is handled. Specifically, the response code must match.
     * A 'Retry-After' response header is used to determine the delay if present; otherwise a default
     * exponential backoff is applied.
     * @param responseCodeException the response code exception to be handled
     * @return true if the response code is handled.
     */
    @Override
    public boolean handles(ResponseCodeException responseCodeException) {
        return responseCodeException.getResponseCode() == statusCode;
    }

    /**
     * Wait for the appropriate amount of time and retry the action, up to {@code maxRetries} additional
     * times. Each retry recomputes its wait from the most recent failure, and if the retries are
     * exhausted (or a non-retryable failure is encountered), the most recent failure is thrown -
     * not the original one, since it carries the most up-to-date information.
     * @param responseCodeException the exception to be handled
     * @param action the action to be executed
     * @return the result of the actions execution
     * @throws ResponseCodeException if retries are exhausted or a non-retryable failure is encountered.
     * If a retry attempt fails with a non-ResponseCodeException error (wrapped as a CoreException),
     * there is no new ResponseCodeException to surface, so the most recent known ResponseCodeException is thrown instead.
     */
    @Override
    public InputStream retry(ResponseCodeException responseCodeException, ThrowingSupplier<InputStream> action) throws ResponseCodeException {
        ResponseCodeException currentFailure = responseCodeException;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            long sleepMillis = sleepMillisFor(currentFailure, attempt);
            try {
                logger.debug("Retrying after {}ms (attempt {}/{})", sleepMillis, attempt, maxRetries);
                if (sleepMillis > 60000) {
                    logger.warn("Computed retry delay is a high value: {}s", sleepMillis / 1000);
                }
                Thread.sleep(sleepMillis);
                return action.get();
            } catch (InterruptedException ex) {
                logger.error("Interrupted waiting to retry command.  Rethrowing most recent known failure", ex);
                Thread.currentThread().interrupt();
                throw currentFailure;
            } catch (CoreException ex) {
                if (ex instanceof ResponseCodeException) {
                    ResponseCodeException newFailure = (ResponseCodeException) ex;
                    if (attempt < maxRetries && handles(newFailure)) {
                        logger.debug("Retry attempt {} failed with a retryable response code {}.  Retrying.", attempt, newFailure.getResponseCode());
                        currentFailure = newFailure;
                        continue;
                    }
                    logger.error("Retry attempt {} failed and is not retryable (or max retries reached).  Rethrowing latest failure", attempt, ex);
                    throw newFailure;
                }
                //the retried action failed with something other than a ResponseCodeException (e.g. a wrapped
                //IOException) - there is no new ResponseCodeException to surface, so fall back to the most
                //recent one we do have
                logger.error("Non-retryable exception retrying command.  Rethrowing most recent known response failure", ex);
                throw currentFailure;
            }
        }
        throw currentFailure;
    }

    private long sleepMillisFor(ResponseCodeException failure, int attempt) {
        Long delaySeconds = failure.getRetryAfterDelaySeconds();
        ZonedDateTime httpDate = failure.getRetryAfterHttpDate();
        if (delaySeconds != null && delaySeconds > -1) {
            return delaySeconds * 1000;
        } else if (httpDate != null && httpDate.isAfter(ZonedDateTime.now())) {
            return ChronoUnit.MILLIS.between(ZonedDateTime.now(), httpDate);
        }
        //no (usable) Retry-After value - fall back to a default exponential backoff
        return baseBackoffMillis * (1L << (attempt - 1));
    }
}
