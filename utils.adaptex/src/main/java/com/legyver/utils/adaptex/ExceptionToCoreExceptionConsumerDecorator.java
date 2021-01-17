package com.legyver.utils.adaptex;

import com.legyver.core.exception.CoreException;

/**
 * Guarantee that the type of an exception thrown by the consumer is of type CoreException
 * @param <T> the type of the parameter accepted in the consumer's accept method
 */
public class ExceptionToCoreExceptionConsumerDecorator<T> {
	private final ThrowingConsumer<T> consumer;

	/**
	 * Decorate a ThrowingConsumer in a way that the type of the exception thrown is CoreException.
	 * @param consumer the consumer to decorate
	 */
	public ExceptionToCoreExceptionConsumerDecorator(ThrowingConsumer<T> consumer) {
		this.consumer = consumer;
	}

	/**
	 * Accept a parameter
	 * @param arg the parameter to accept
	 * @throws CoreException if the consumer throws an Exception
	 */
	public void accept(T arg) throws CoreException {
		try {
			consumer.accept(arg);
		} catch (Exception ex) {
			throw new CoreException(ex);
		}
	}

	/**
	 * Alternative to {@link java.util.function.Consumer} that throws an Exception
	 * @param <T> argument type
	 */
	@FunctionalInterface
	public interface ThrowingConsumer<T> {
		/**
		 * Alternative to {@link java.util.function.Consumer#accept(Object)} that throws an Exception
		 * @param arg the consumer argument
		 * @throws Exception if the implementing consumer throws an exception
		 */
		void accept(T arg) throws Exception;
	}
}
