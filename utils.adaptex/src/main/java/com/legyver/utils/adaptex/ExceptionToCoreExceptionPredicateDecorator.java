package com.legyver.utils.adaptex;

import com.legyver.core.exception.CoreException;

/**
 * Guarantee that the type of an exception thrown by predicate is of type CoreException
 * @param <T> the type of the parameter accepted in the predicate's test method
 */
public class ExceptionToCoreExceptionPredicateDecorator<T> {
	private final ThrowingPredicate<T> predicate;

	/**
	 * Decorate a ThrowingPredicate in a way that the type of the exception thrown is CoreException.
	 * @param predicate the predicate to decorate
	 */
	public ExceptionToCoreExceptionPredicateDecorator(ThrowingPredicate<T> predicate) {
		this.predicate = predicate;
	}

	/**
	 * Test a condition.
	 * @param arg the argument to test
	 * @return the result of the test
	 * @throws CoreException if the predicate throws an Exception
	 */
	public boolean test(T arg) throws CoreException {
		try {
			return predicate.test(arg);
		} catch (Exception ex) {
			throw new CoreException(ex);
		}
	}

	/**
	 * Alternative to {@link java.util.function.Predicate#test(Object)} that throws an Exception
	 * @param <T> the type of the parameter accepted as an argument to the test method
	 */
	@FunctionalInterface
	public interface ThrowingPredicate<T> {
			/**
			 * Alternative to {@link java.util.function.Predicate#test(Object)} that throws an Exception
			 * @param arg the argument to test
			 * @return the result of the test
			 * @throws Exception if the implementing test throws an exception
			 */
		boolean test(T arg) throws Exception;
	}
}
