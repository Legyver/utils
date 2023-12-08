package com.legyver.utils.cli.exception;

import com.legyver.core.exception.CoreException;

/**
 * Exception thrown when menu options are utilized improperly
 */
public class InvalidUsageException extends CoreException {

    /**
     * Construct an Invalid Usage Exception with a hint as to how the user should use the menu item
     * @param hint the hint on how to use the menu item
     */
    public InvalidUsageException(String hint) {
        super(hint);
    }
}
