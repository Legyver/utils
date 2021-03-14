package com.legyver.utils.nippe;

import com.legyver.core.exception.CoreException;
import com.legyver.core.function.ThrowingFunction;

/**
 * decorator-based mechanism for constructing NPE-safe chained getters: see
 * com.legyver.util.nippe.StepTest
 * @since 2.0
 */
public class Step<T, U> extends AbstractStepExecutor<T> {

	private final AbstractStepExecutor<U> decorated;
	private final ThrowingFunction<U, T> function;

	/**
	 * Constructor to create a Step
	 * @param decorated the previous Step or Base
	 * @param function the function to be evaluated if the node is not null
	 */
	public Step(AbstractStepExecutor<U> decorated, ThrowingFunction<U, T> function) {
		this.decorated = decorated;
		this.function = function;
	}

	/**
	 * Take a step
	 * Executes the Function if the result of the previous Base/Step is not null
	 * @return null if the result of the previous step is null, the result of the function when applied to the result otherwise
	 */
	@Override
	public T execute() throws CoreException {
		U args = decorated.execute();
		if (args != null) {
			return function.apply(args);
		}
		return null;
	}
}
