package com.legyver.utils.adaptex;

import com.legyver.core.exception.CoreException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

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
			fail("Expected an exception to be thrown");
		} catch (CoreException ex) {
			//okay
			Exception originalException = (Exception) ex.getCause();
			assertThat(originalException.getMessage()).isEqualTo(EXCEPTION_MESSAGE);
		}
	}

	@Test
	public void checkedException_noException() throws Exception {
		ExceptionToCoreExceptionConsumerDecorator decorator = new ExceptionToCoreExceptionConsumerDecorator((value)-> {
			assertThat(value).isEqualTo(VALUE);
		});
		decorator.accept(VALUE);
	}
}