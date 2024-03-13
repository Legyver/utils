package com.legyver.utils.adaptex;

import com.legyver.core.exception.CoreException;
import com.legyver.core.function.ThrowingPredicate;

/**
 * Decorate a ThrowingPredicate so that the Exception type will always be wrapped in a {@link CoreException}
 * @param <T> the argument type of the predicate
 */
public class ExceptionToCoreExceptionPredicateDecorator<T> implements ThrowingPredicate<T> {
	private final ThrowingPredicate<T> predicate;

	/**
	 * Construct an ExceptionToCoreExceptionPredicateDecorator with a predicate
	 * @param predicate the predicate
	 */
	public ExceptionToCoreExceptionPredicateDecorator(ThrowingPredicate<T> predicate) {
		this.predicate = predicate;
	}

	/**
	 * Test a predicate
	 * @param arg the arg to use in the test
	 * @return the result of the test
	 * @throws CoreException if the test throws an Exception
	 */
	@Override
	public boolean test(T arg) throws CoreException {
		try {
			return predicate.test(arg);
		} catch (CoreException coreException) {
			throw coreException;
		} catch (Exception ex) {
			throw new CoreException(ex);
		}
	}

	/**
	 * A version of {@link java.util.function.Predicate} that throws an Exception
	 * @param <T> the type of the predicate's argument
	 */
	@FunctionalInterface
	public interface ThrowingPredicate<T> {
		/**
		 * Test an argument
		 * @param arg the argument
		 * @return the result of the test
		 * @throws Exception any exception thrown by the test
		 */
		boolean test(T arg) throws Exception;
	}
}