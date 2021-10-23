package com.legyver.utils.extractex;

import java.lang.reflect.InvocationTargetException;

/**
 * An ExceptionExtractor that extracts either the lowest or first exception of the identified type based on the depthFirst flag.
 * @param <T> the Type of the Exception
 */
public class ExceptionExtractor<T extends Exception> {

	private final Class<T> klass;
	private final boolean depthFirst;

	/**
	 * Create an ExceptionExtractor that extracts either the lowest or first exception of the identified type based on the depthFirst flag
	 * @param klass class of the exception to extract.
	 * @param depthFirst flag to identify if to search breadth-first or depth-first
	 */
	public ExceptionExtractor(Class<T> klass, boolean depthFirst) {
		this.klass = klass;
		this.depthFirst = depthFirst;
	}

	/**
	 * Create an ExceptionExtractor that extracts the first exception of the identified type.
	 * @param klass the class of the exception to extract
	 */
	public ExceptionExtractor(Class<T> klass) {
		this(klass, false);
	}

	/**
	 * Extracts the root exception from the wrapped exception stack
	 * @param e the exception to analyze
	 * @return the root exception
	 */
	@SuppressWarnings("unchecked")
	public T extractException(Throwable e) {
		T exception = null;
		if (depthFirst) {
			if (e instanceof InvocationTargetException) {
				exception = extractException(((InvocationTargetException) e).getTargetException());
			} else if (e.getCause() != null) {
				exception = extractException(e.getCause());
			} else if (klass.isInstance(e)) {
				exception = (T) e;
			}
		} else {
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
		}
		return exception;
	}

}
