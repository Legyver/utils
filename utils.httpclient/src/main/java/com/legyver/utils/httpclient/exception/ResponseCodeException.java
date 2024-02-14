package com.legyver.utils.httpclient.exception;

import com.legyver.core.exception.CoreException;

/**
 * An exception thrown when a response code other than 200 hundred is received
 */
public class ResponseCodeException extends CoreException {
    private final int responseCode;

    /**
     * Construct a response code exception
     * @param aMessage a message about the exception
     * @param responseCode the response code returned
     */
    public ResponseCodeException(String aMessage, int responseCode) {
        super(aMessage);
        this.responseCode = responseCode;
    }

    public int getResponseCode() {
        return responseCode;
    }
}
