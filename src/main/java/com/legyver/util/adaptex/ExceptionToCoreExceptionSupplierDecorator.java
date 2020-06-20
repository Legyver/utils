package com.legyver.util.adaptex;

import com.legyver.core.exception.CoreException;

public class ExceptionToCoreExceptionSupplierDecorator<T> {
	private final ThrowingSupplier<T> supplier;

	public ExceptionToCoreExceptionSupplierDecorator(ThrowingSupplier<T> supplier) {
		this.supplier = supplier;
	}

	public T get() throws CoreException {
		try {
			return supplier.get();
		} catch (Exception ex) {
			throw new CoreException(ex);
		}
	}

	@FunctionalInterface
	public interface ThrowingSupplier<T> {
		T get() throws Exception;
	}
}
