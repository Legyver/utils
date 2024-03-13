package com.legyver.utils.adaptex;

import com.legyver.core.exception.CoreException;
import com.legyver.core.function.ThrowingBiFunction;

/**
 * Decorate a ThrowingBiFunction so that the Exception type will always be wrapped in a {@link CoreException}
 * @param <T> the type of the function's first argument
 * @param <U> the type of the function's second argument
 * @param <R> the return type of the function
 */
public class ExceptionToCoreExceptionBiFunctionDecorator<T, U, R> implements ThrowingBiFunction<T, U, R> {
	private final ThrowingBiFunction<T, U, R> biFunction;

	/**
	 * Construct an ExceptionToCoreExceptionBiFunctionDecorator with a function
	 * @param biFunction the function to decorate
	 */
	public ExceptionToCoreExceptionBiFunctionDecorator(ThrowingBiFunction<T, U, R> biFunction) {
		this.biFunction = biFunction;
	}

	/**
	 * Apply a function to an argument.
	 * @param t the first argument
	 * @param u the second argument
	 * @return the result of the function
	 * @throws CoreException any thrown Exception wrapped in a CoreException
	 */
	@Override
	public R apply(T t, U u) throws CoreException {
		try {
			return biFunction.apply(t, u);
		} catch (CoreException coreException) {
			throw coreException;
		} catch (Exception ex) {
			throw new CoreException(ex);
		}
	}


	/**
	 * A version of {@link java.util.function.BiFunction} that throws an Exception
	 * @param <T> the type of the function's first argument
	 * @param <U> the type of the function's second argument
	 * @param <R> the return type of the function
	 */
	@FunctionalInterface
	public interface ThrowingBiFunction<T, U, R> {
		/**
		 * Apply a function to an argument
		 * @param t the first argument
		 * @param u the second argument
		 * @return the result of the function
		 * @throws Exception if the function throws an exception
		 */
		R apply(T t, U u) throws Exception;
	}

}