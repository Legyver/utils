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
 * Retry an action after waiting for the specified delay
 */
public class RetryAfterHandler implements ErrorResponseCodeRetryHandler {
    private static final Logger logger = LogManager.getLogger(RetryAfterHandler.class);

    private final int statusCode;

    /**
     * Construct a Retry-After handler for the status code
     * @param statusCode the status code for which to apply the handler
     */
    public RetryAfterHandler(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Tests if the response code is handled.  Specifically, the response code must match and the 'Retry-After' response header is specified
     * @param responseCodeException the response code exception to be handled
     * @return true if the response code is handled.
     */
    @Override
    public boolean handles(ResponseCodeException responseCodeException) {
        return responseCodeException.getResponseCode() == statusCode
                && responseCodeException.getResponseHeader(ResponseCodeException.RETRY_AFTER) != null;
    }

    /**
     * Wait for the specified amount of time and then retry the action
     * @param responseCodeException the exception to be handled
     * @param action the action to be executed
     * @return the result of the actions execution
     * @throws ResponseCodeException if any error is encountered, the original exception is re-thrown
     */
    @Override
    public InputStream retry(ResponseCodeException responseCodeException, ThrowingSupplier<InputStream> action) throws ResponseCodeException {
        long sleepMillis = -1;
        Long delaySeconds = responseCodeException.getRetryAfterDelaySeconds();
        ZonedDateTime httpDate = responseCodeException.getRetryAfterHttpDate();
        if (delaySeconds != null && delaySeconds > -1) {
            sleepMillis = delaySeconds * 1000;
        } else if (httpDate != null && httpDate.isAfter(ZonedDateTime.now())) {
            sleepMillis = ChronoUnit.MILLIS.between(ZonedDateTime.now(), httpDate);
        }
        if (sleepMillis > -1) {
            try {
                logger.debug("Retry-After specified.  Sleeping for {}ms", sleepMillis);
                if (sleepMillis > 60000) {
                    logger.warn("Specified 'Retry-After' set for high value : {}s", sleepMillis/1000);
                }
                Thread.sleep(sleepMillis);
                return action.get();
            } catch (InterruptedException ex) {
                logger.error("Interrupted waiting to retry command.  Rethrowing original exception", ex);
                Thread.currentThread().interrupt();
                throw responseCodeException;
            } catch (CoreException ex) {
                logger.error("Exception retrying command.  Rethrowing original exception", ex);
                throw responseCodeException;
            }
        } else {
            throw responseCodeException;
        }
    }
}
