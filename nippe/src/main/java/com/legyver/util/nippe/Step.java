package com.legyver.util.nippe;

import java.util.function.Function;

/**
 * decorator-based mechanism for constructing NPE-safe chained getters: see
 * com.legyver.util.nippe.StepTest
 */
public class Step<T, U> extends AbstractStepExecutor<T> {

	private final AbstractStepExecutor<U> decorated;
	private final Function<U, T> function;

	public Step(AbstractStepExecutor<U> decorated, Function<U, T> function) {
		this.decorated = decorated;
		this.function = function;
	}

	@Override
	public T execute() {
		U args = decorated.execute();
		if (args != null) {
			return function.apply(args);
		}
		return null;
	}
}
