package com.legyver.utils.adaptex;

import com.legyver.core.exception.CoreException;

public class ExceptionToCoreExceptionConsumerDecorator<T> {
	private final ThrowingConsumer<T> consumer;

	public ExceptionToCoreExceptionConsumerDecorator(ThrowingConsumer<T> consumer) {
		this.consumer = consumer;
	}

	public void accept(T arg) throws CoreException {
		try {
			consumer.accept(arg);
		} catch (Exception ex) {
			throw new CoreException(ex);
		}
	}

	@FunctionalInterface
	public interface ThrowingConsumer<T> {
		void accept(T arg) throws Exception;
	}
}
