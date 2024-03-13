package com.legyver.utils.adaptex;

import com.legyver.core.exception.CoreException;
import com.legyver.core.function.ThrowingFunction;

/**
 * Decorate a ThrowingFunction so that the Exception type will always be wrapped in a {@link CoreException}
 * @param <T> the type of the function's argument
 * @param <R> the return type of the function
 */
public class ExceptionToCoreExceptionFunctionDecorator<T, R> implements ThrowingFunction<T, R> {
	private final ThrowingFunction<T, R> function;

	/**
	 * Construct an ExceptionToCoreExceptionFunctionDecorator with a function
	 * @param function the function to decorate
	 */
	public ExceptionToCoreExceptionFunctionDecorator(ThrowingFunction<T, R> function) {
		this.function = function;
	}

	/**
	 * Apply a function to an argument.
	 * @param arg the argument
	 * @return the result of the function
	 * @throws CoreException any thrown Exception wrapped in a CoreException
	 */
	@Override
	public R apply(T arg) throws CoreException {
		try {
			return function.apply(arg);
		} catch (CoreException coreException) {
			throw coreException;
		} catch (Exception ex) {
			throw new CoreException(ex);
		}
	}

	/**
	 * A version of {@link java.util.function.Function} that throws an Exception
	 * @param <T> the type of the function's argument
	 * @param <R> the return type of the function
	 */
	@FunctionalInterface
	public interface ThrowingFunction<T, R> {
		/**
		 * Apply a function to an argument
		 * @param arg the argument
		 * @return the result of the function
		 * @throws Exception if the function throws an exception
		 */
		R apply(T arg) throws Exception;
	}
}