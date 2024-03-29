package com.legyver.utils.extractex;

import com.legyver.core.exception.CoreException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;


public class ExceptionExtractorTest {

	private static final String MESSAGE = "testMessage";
	private static final Exception EXCEPTION = new ExceptionC(new ExceptionB(new CoreException(MESSAGE)));

	@Test
	public void matchesExceptionInstance() throws Exception {
		{
			CoreException extracted = new ExceptionExtractor<>(CoreException.class).extractException(EXCEPTION);
			assertThat(extracted).isNotNull();
			assertThat(extracted.getMessage()).isEqualTo(MESSAGE);
		}
		{
			ExceptionB extracted = new ExceptionExtractor<>(ExceptionB.class).extractException(EXCEPTION);
			assertThat(extracted).isNotNull();
			assertThat(extracted.getCause()).isInstanceOf(CoreException.class);
		}
		{
			ExceptionC extracted = new ExceptionExtractor<>(ExceptionC.class).extractException(EXCEPTION);
			assertThat(extracted).isNotNull();
			assertThat(extracted.getCause()).isInstanceOf(ExceptionB.class);
		}
	}

	@Test
	public void matchesExceptionBroadInstance() {
		{
			Exception extracted = new ExceptionExtractor<>(Exception.class).extractException(EXCEPTION);
			assertThat(extracted).isNotNull();
			//first one it finds should be the top-level exception
			assertThat(extracted).isInstanceOf(ExceptionC.class);
		}
		{
			Exception extracted = new ExceptionExtractor<>(Exception.class, true).extractException(EXCEPTION);
			assertThat(extracted).isNotNull();
			//by depth-first, the original exception should be found
			assertThat(extracted).isInstanceOf(CoreException.class);
		}

	}

	@Test
	public void doesNotMatchWrongType() throws Exception {
		ExceptionD extracted = new ExceptionExtractor<>(ExceptionD.class).extractException(EXCEPTION);
		assertThat(extracted).isNull();
	}

	@Test
	public void nullSafety() throws Exception {
		CoreException extracted = new ExceptionExtractor<>(CoreException.class).extractException(null);
		assertThat(extracted).isNull();
	}

	@Test
	public void invocationTargetException() throws Exception {
		IvocationThrowingClass throwingClass = new IvocationThrowingClass();
		Method method = IvocationThrowingClass.class.getMethod("throwException");
		try {
			method.invoke(throwingClass);
			fail("exception not thrown");
		} catch (InvocationTargetException ex) {
			CoreException extracted = new ExceptionExtractor<>(CoreException.class).extractException(ex);
			assertThat(extracted).isNotNull();

			assertThat(extracted.getMessage()).isEqualTo(MESSAGE);
		}

	}

	private static class ExceptionB extends Exception {
		public ExceptionB(Throwable thrwbl) {
			super(thrwbl);
		}
	}

	private static class ExceptionC extends Exception {
		public ExceptionC(Throwable thrwbl) {
			super(thrwbl);
		}
	}

	private static class ExceptionD extends Exception {

	}

	private static class IvocationThrowingClass {

		public void throwException() throws Exception {
			throw EXCEPTION;
		}

	}
}
