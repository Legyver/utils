package com.legyver.util.adaptex;

import com.legyver.core.exception.CoreException;
import com.legyver.util.extractex.ExceptionExtractor;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ExceptionToCoreExceptionConsumerDecoratorTest {

	private static final String EXCEPTION_MESSAGE = "Exception message";
	private static final String VALUE = "Test value";

	@Test
	public void checkedException_throwsException() throws Exception {
		ExceptionToCoreExceptionConsumerDecorator decorator = new ExceptionToCoreExceptionConsumerDecorator((value)-> {
			throw new Exception(EXCEPTION_MESSAGE);
		});
		try {
			decorator.accept(VALUE);
			Assert.fail("Expected an exception to be thrown");
		} catch (CoreException ex) {
			//okay
			Exception originalException = new ExceptionExtractor<>(Exception.class, true).extractException(ex);
			assertThat(originalException.getMessage(), is(EXCEPTION_MESSAGE));
		}
	}

	@Test
	public void checkedException_noException() throws Exception {
		ExceptionToCoreExceptionConsumerDecorator decorator = new ExceptionToCoreExceptionConsumerDecorator((value)-> {
			assertThat(value, is(VALUE));
		});
		decorator.accept(VALUE);
	}
}
