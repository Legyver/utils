package com.legyver.utils.httpclient.exception;

import com.legyver.core.function.ThrowingSupplier;

import java.io.InputStream;

/**
 * Handle a {@link ResponseCodeException}
 */
public interface ErrorResponseCodeRetryHandler {
    /**
     * Check if the handler handles the specific exception
     * @param responseCodeException the response code exception to be handled
     * @return true if the response code exception can be handled
     */
    boolean handles(ResponseCodeException responseCodeException);

    /**
     * Handle the response code exception.
     * @param responseCodeException the exception to be handled
     * @param action the action to be executed
     * @return the result of the action
     * @throws ResponseCodeException if there is an error of any type, the original exception is re-thrown
     */
    InputStream retry(ResponseCodeException responseCodeException, ThrowingSupplier<InputStream> action) throws ResponseCodeException;
}
