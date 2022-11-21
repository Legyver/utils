package com.legyver.utils.adaptex;

import com.legyver.core.exception.CoreException;

/**
 * Decorate a ThrowingBiConsumer so that the Exception type will always be wrapped in a {@link CoreException}
 * @param <T> the type of the consumer's first argument
 * @param <U> the type of the consumer's second argument
 */
public class ExceptionToCoreExceptionBiConsumerDecorator<T, U> {
	private final ThrowingBiConsumer<T, U> biConsumer;

	/**
	 * Construct an ExceptionToCoreExceptionBiConsumerDecorator with a consumer
	 * @param biConsumer the bi-consumer to decorate
	 */
	public ExceptionToCoreExceptionBiConsumerDecorator(ThrowingBiConsumer<T, U> biConsumer) {
		this.biConsumer = biConsumer;
	}

	/**
	 * Accept an argument
	 * @param t the first argument
	 * @param u the second argument
	 * @throws CoreException any thrown Exception wrapped in a CoreException
	 */
	public void accept(T t, U u) throws CoreException {
		try {
			biConsumer.accept(t, u);
		} catch (CoreException coreException) {
			throw coreException;
		} catch (Exception ex) {
			throw new CoreException(ex);
		}
	}

	/**
	 * A version of {@link java.util.function.BiConsumer} that throws an Exception
	 * @param <T> the first argument type
	 * @param <U> the second argument type
	 */
	@FunctionalInterface
	public interface ThrowingBiConsumer<T, U> {
		/**
		 * Accept and argument
		 * @param t the first argument
		 * @param u the second argument
		 * @throws Exception if the consumer throws one
		 */
		void accept(T t, U u) throws Exception;
	}
}