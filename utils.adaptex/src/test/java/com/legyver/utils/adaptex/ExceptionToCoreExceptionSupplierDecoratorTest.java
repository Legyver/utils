package com.legyver.utils.adaptex;

import com.legyver.core.exception.CoreException;
import com.legyver.utils.extractex.ExceptionExtractor;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ExceptionToCoreExceptionSupplierDecoratorTest {

	private static final String EXCEPTION_MESSAGE = "Exception message";
	private static final String VALUE = "Test value";

	@Test
	public void checkedException_throwsException() throws Exception {
		ExceptionToCoreExceptionSupplierDecorator decorator = new ExceptionToCoreExceptionSupplierDecorator(() -> {
			throw new Exception(EXCEPTION_MESSAGE);
		});
		try {
			decorator.get();
			Assert.fail("Expected an exception to be thrown");
		} catch (CoreException ex) {
			//okay
			Exception originalException = new ExceptionExtractor<>(Exception.class, true).extractException(ex);
			assertThat(originalException.getMessage(), is(EXCEPTION_MESSAGE));
		}
	}

	@Test
	public void checkedException_noException() throws Exception {
		ExceptionToCoreExceptionSupplierDecorator decorator = new ExceptionToCoreExceptionSupplierDecorator(() -> VALUE);
		Object value = decorator.get();
		assertThat(value, is(VALUE));
	}
}
