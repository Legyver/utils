package com.legyver.utils.nippe;

/**
 * Base class that is contains the args
 * @since 2.0
 */
public class Base<T> extends AbstractStepExecutor<T> {

	private final T args;

	/**
	 * Construct a Base with the innermost value
	 * @param args the value to be returned by the Base
	 */
	public Base(T args) {
		this.args = args;
	}

	/**
	 * Returns the value directly
	 * @return the value passed in to the constructor
	 */
	@Override
	public T execute() {
		return args;
	}

}
