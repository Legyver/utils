package com.legyver.utils.adaptex;

import com.legyver.core.exception.CoreException;

/**
 * Decorate a ThrowingSupplier so that the Exception type will always be wrapped in a {@link CoreException}
 * @param <T> the return type of the decorated supplier
 */
public class ExceptionToCoreExceptionSupplierDecorator<T> {
	private final ThrowingSupplier<T> supplier;

	/**
	 * Construct an ExceptionToCoreExceptionSupplierDecorator with a supplier
	 * @param supplier the supplier to decorate
	 */
	public ExceptionToCoreExceptionSupplierDecorator(ThrowingSupplier<T> supplier) {
		this.supplier = supplier;
	}

	/**
	 * Get the value
	 * @return the value
	 * @throws CoreException if the supplier throws an exception
	 */
	public T get() throws CoreException {
		try {
			return supplier.get();
		} catch (CoreException coreException) {
			throw coreException;
		} catch (Exception ex) {
			throw new CoreException(ex);
		}
	}

	/**
	 * A version of {@link java.util.function.Supplier} that throws an Exception
	 * @param <T> the return type
	 */
	@FunctionalInterface
	public interface ThrowingSupplier<T> {
		/**
		 * Retrieve a value
		 * @return the value
		 * @throws Exception any exception thrown by the supplier
		 */
		T get() throws Exception;
	}
}