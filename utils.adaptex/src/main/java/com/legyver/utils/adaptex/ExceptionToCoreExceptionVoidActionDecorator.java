package com.legyver.utils.adaptex;

import com.legyver.core.exception.CoreException;
import com.legyver.core.function.ThrowingVoidAction;

/**
 * Decorate a ThrowingVoidAction so that the Exception type will always be wrapped in a {@link CoreException}
 */
public class ExceptionToCoreExceptionVoidActionDecorator {
	private final ThrowingVoidAction action;

	/**
	 * Construct an ExceptionToCoreExceptionVoidActionDecorator with an action
	 * @param action the action to decorate
	 */
	public ExceptionToCoreExceptionVoidActionDecorator(ThrowingVoidAction action) {
		this.action = action;
	}

	/**
	 * Execute an action
	 * @throws CoreException any thrown Exception wrapped in a CoreException
	 */
	public void execute() throws CoreException {
		try {
			action.execute();
		} catch (Exception ex) {
			throw new CoreException(ex);
		}
	}

	/**
	 * A function that throws an Exception and returns nothing
	 */
	@FunctionalInterface
	public interface ThrowingVoidAction {
		/**
		 * Retrieve a value
		 * @throws Exception any exception thrown by the action
		 */
		void execute() throws Exception;
	}
}