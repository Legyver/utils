package com.legyver.utils.adaptex;

import com.legyver.core.exception.CoreException;
import com.legyver.core.function.ThrowingAction;

/**
 * Decorate a ThrowingAction so that the Exception type will always be wrapped in a {@link CoreException}
 * @param <T> the type of the consumer's argument
 */
public class ExceptionToCoreExceptionActionDecorator<T> implements ThrowingAction<T> {
	private final ThrowingAction<T> action;

	/**
	 * Construct an ExceptionToCoreExceptionActionDecorator with an action
	 *
	 * @param action the action to decorate
	 */
	public ExceptionToCoreExceptionActionDecorator(ThrowingAction<T> action) {
		this.action = action;
	}

	/**
	 * Execute an action
	 *
	 * @return the result of the action
	 * @throws CoreException any thrown Exception wrapped in a CoreException
	 */
	@Override
	public T execute() throws CoreException {
		try {
			return action.execute();
		} catch (CoreException coreException) {
			throw coreException;
		} catch (Exception ex) {
			throw new CoreException(ex);
		}
	}

	/**
	 * A function that throws an Exception and returns the result
	 * @param <R> the type of the result
	 */
	@FunctionalInterface
	public interface ThrowingAction<R> {
		/**
		 * Retrieve a value
		 * @throws Exception any exception thrown by the action
		 * @return the result
		 */
		R execute() throws Exception;
	}
}