package com.legyver.utils.adaptex;

import com.legyver.core.exception.CoreException;

/**
 * ExceptionToCoreExceptionSupplierDecorator decorating a Supplier that throws an exception.
 * @param <T> the type of the value returned by the Supplier
 */
public class ExceptionToCoreExceptionSupplierDecorator<T> {
	private final ThrowingSupplier<T> supplier;

	/**
	 * Construct an ExceptionToCoreExceptionSupplierDecorator wrapping a Supplier that throws an exception.
	 * @param supplier the supplier to decorate
	 */
	public ExceptionToCoreExceptionSupplierDecorator(ThrowingSupplier<T> supplier) {
		this.supplier = supplier;
	}

	/**
	 * Get a result
	 * @return a result
	 * @throws CoreException if the supplier throws an Exception
	 */
	public T get() throws CoreException {
		try {
			return supplier.get();
		} catch (Exception ex) {
			throw new CoreException(ex);
		}
	}

	/**
	 * Alternative Supplier to the {@link java.util.function.Supplier} that throws an exception.
	 * @param <T> the type of the object returned
	 */
	@FunctionalInterface
	public interface ThrowingSupplier<T> {
		/**
		 * Get a result
		 * @return a result
		 * @throws Exception if the implementing method throws an Exception
		 */
		T get() throws Exception;
	}
}
