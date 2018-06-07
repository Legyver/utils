package com.legyver.util.extractex;

import java.lang.reflect.InvocationTargetException;

public class ExceptionExtractor<T extends Exception> {

	private final Class<T> klass;

	public ExceptionExtractor(Class<T> klass) {
		this.klass = klass;
	}

	public T extractException(Throwable e) {
		T exception = null;
		if (e != null) {
			if (klass.isInstance(e)) {
				exception = (T) e;
			} else if (e instanceof InvocationTargetException) {
				exception = extractException(((InvocationTargetException) e).getTargetException());//whereas wrapped exceptions can generally be retrieved via ex.getCause(), InvocationTargetException stores the exception as a targetException, so you need to retrieve it with getTargetException()
			} else {
				//wrapped exception hell
				exception = extractException(e.getCause());
			}
		}
		return exception;
	}

}
