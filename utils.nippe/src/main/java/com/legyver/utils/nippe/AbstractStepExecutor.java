package com.legyver.utils.nippe;

import com.legyver.core.exception.CoreException;

/**
 * Superclass for the Step and Base decorators
 *
 * @since 2.0
 */
public abstract class AbstractStepExecutor<T> {
	/**
	 * Take a step
	 * @return the result of the step
	 */
	public abstract T execute();
}
