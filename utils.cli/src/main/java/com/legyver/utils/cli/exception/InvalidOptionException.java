package com.legyver.utils.cli.exception;

import com.legyver.core.exception.CoreException;

/**
 * Exception thrown when an invalid menu option is configured or requested.
 *
 */
public class InvalidOptionException extends CoreException {

    /**
     * Construct an InvalidOptionException with a given message
     * @param aMessage the message to display
     */
    public InvalidOptionException(String aMessage) {
        super(aMessage);
    }
}
