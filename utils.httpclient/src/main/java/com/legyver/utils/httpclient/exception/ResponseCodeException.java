package com.legyver.utils.httpclient.exception;

import com.legyver.core.exception.CoreException;

/**
 * An exception thrown when a response code other than 200 hundred is received
 */
public class ResponseCodeException extends CoreException {

    /**
     * Construct a response code exception
     * @param aMessage a message about the exception
     */
    public ResponseCodeException(String aMessage) {
        super(aMessage);
    }
}
