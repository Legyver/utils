package com.legyver.util.nippe;

public class Base<T> extends AbstractStepExecutor<T> {

	private final T args;

	public Base(T args) {
		this.args = args;
	}

	@Override
	public T execute() {
		return args;
	}

}
