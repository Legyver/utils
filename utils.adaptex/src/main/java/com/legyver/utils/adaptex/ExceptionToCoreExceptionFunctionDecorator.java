package com.legyver.utils.adaptex;

import com.legyver.core.exception.CoreException;

public class ExceptionToCoreExceptionFunctionDecorator<T, R> {
	private final ThrowingFunction<T, R> function;

	public ExceptionToCoreExceptionFunctionDecorator(ThrowingFunction<T, R> function) {
		this.function = function;
	}

	public R apply(T arg) throws CoreException {
		try {
			return function.apply(arg);
		} catch (Exception ex) {
			throw new CoreException(ex);
		}
	}

	@FunctionalInterface
	public interface ThrowingFunction<T, R> {
		R apply(T arg) throws Exception;
	}
}
