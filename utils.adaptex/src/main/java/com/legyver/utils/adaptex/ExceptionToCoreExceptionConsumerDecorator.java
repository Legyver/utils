package com.legyver.utils.adaptex;

import com.legyver.core.exception.CoreException;

/**
 * Decorate a ThrowingConsumer so that the Exception type will always be wrapped in a {@link CoreException}
 * @param <T> the type of the consumer's argument
 */
public class ExceptionToCoreExceptionConsumerDecorator<T> {
	private final ThrowingConsumer<T> consumer;

	/**
	 * Construct an ExceptionToCoreExceptionConsumerDecorator with a consumer
	 * @param consumer the consumer to decorate
	 */
	public ExceptionToCoreExceptionConsumerDecorator(ThrowingConsumer<T> consumer) {
		this.consumer = consumer;
	}

	/**
	 * Accept an argument
	 * @param arg the argument
	 * @throws CoreException any thrown Exception wrapped in a CoreException
	 */
	public void accept(T arg) throws CoreException {
		try {
			consumer.accept(arg);
		} catch (Exception ex) {
			throw new CoreException(ex);
		}
	}

	/**
	 * A version of {@link java.util.function.Consumer} that throws an Exception
	 * @param <T> the argument type
	 */
	@FunctionalInterface
	public interface ThrowingConsumer<T> {
		/**
		 * Accept and argument
		 * @param arg the argument
		 * @throws Exception if the consumer throws one
		 */
		void accept(T arg) throws Exception;
	}
}