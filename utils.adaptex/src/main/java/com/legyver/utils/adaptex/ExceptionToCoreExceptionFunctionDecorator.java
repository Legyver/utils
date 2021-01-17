package com.legyver.utils.adaptex;

import com.legyver.core.exception.CoreException;

/**
 * Guarantee that the type of an exception thrown by the function is of type CoreException
 * @param <T> the type of the parameter accepted in the function's apply method
 * @param <R> the type returned by the function's apply method
 */
public class ExceptionToCoreExceptionFunctionDecorator<T, R> {
	private final ThrowingFunction<T, R> function;

	/**
	 * Decorate a ThrowingFunction in a way that the type of the exception thrown is CoreException.
	 * @param function the function to decorate
	 */
	public ExceptionToCoreExceptionFunctionDecorator(ThrowingFunction<T, R> function) {
		this.function = function;
	}

	/**
	 * Apply a function to a parameter and return a result
	 * @param arg the parameter
	 * @return the result of the function
	 * @throws CoreException if the function throws an Exception
	 */
	public R apply(T arg) throws CoreException {
		try {
			return function.apply(arg);
		} catch (Exception ex) {
			throw new CoreException(ex);
		}
	}

	/**
	 * Alternative to {@link java.util.function.Function} that throws an Exception
	 * @param <R> return type
	 * @param <T> argument type
	 */
	@FunctionalInterface
	public interface ThrowingFunction<T, R> {
		/**
		 * Alternative to {@link java.util.function.Function#apply(Object)} that throws an Exception
		 * @param arg the function argument
		 * @return the function result
		 * @throws Exception if the implementing function throws an exception
		 */
		R apply(T arg) throws Exception;
	}
}
