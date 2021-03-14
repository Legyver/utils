package com.legyver.utils.adaptex;

import com.legyver.core.exception.CoreException;

/**
 * Decorate a ThrowingBiPredicate so that the Exception type will always be wrapped in a {@link CoreException}
 * @param <T> the first argument type of the predicate
 * @param <U> the second argument type of the predicate
 */
public class ExceptionToCoreExceptionBiPredicateDecorator<T, U> {
	private final ThrowingBiPredicate<T, U> biPredicate;

	/**
	 * Construct an ExceptionToCoreExceptionBiPredicateDecorator with a predicate
	 * @param biPredicate the predicate
	 */
	public ExceptionToCoreExceptionBiPredicateDecorator(ThrowingBiPredicate<T,U> biPredicate) {
		this.biPredicate = biPredicate;
	}

	/**
	 * Test a predicate
	 * @param t the first arg to use in the test
	 * @param u the second arg to use in the test
	 * @return the result of the test
	 * @throws CoreException if the test throws an Exception
	 */
	public boolean test(T t, U u) throws CoreException {
		try {
			return biPredicate.test(t, u);
		} catch (Exception ex) {
			throw new CoreException(ex);
		}
	}

	/**
	 * A version of {@link java.util.function.BiPredicate} that throws an Exception
	 * @param <T> the type of the predicate's first argument
	 * @param <U> the type of the predicate's second argument
	 */
	@FunctionalInterface
	public interface ThrowingBiPredicate<T, U> {
		/**
		 * Test an argument
		 * @param t the first argument
		 * @param u the second argument
		 * @return the result of the test
		 * @throws Exception any exception thrown by the test
		 */
		boolean test(T t, U u) throws Exception;
	}
}