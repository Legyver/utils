package com.legyver.util.adaptex;

import com.legyver.core.exception.CoreException;

public class ExceptionToCoreExceptionPredicateDecorator<T> {
	private final ThrowingPredicate<T> predicate;

	public ExceptionToCoreExceptionPredicateDecorator(ThrowingPredicate<T> predicate) {
		this.predicate = predicate;
	}

	public boolean test(T arg) throws CoreException {
		try {
			return predicate.test(arg);
		} catch (Exception ex) {
			throw new CoreException(ex);
		}
	}

	@FunctionalInterface
	public interface ThrowingPredicate<T> {
		boolean test(T arg) throws Exception;
	}
}
