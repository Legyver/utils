package com.legyver.utils.graphrunner;

import com.legyver.core.exception.CoreException;
import com.legyver.utils.graphrunner.ctx.shared.SharedMapCtx;

/**
 * Command attached to a graph to be run on all nodes in that graph as soon as they meet the evaluable prerequisites
 * @param <T> Current value associated with the graph node.  See {@link SharedMapCtx} for an example where the value is a map common to all nodes but where getValue() only returns the value of that node.
 */
@FunctionalInterface
public interface GraphExecutedCommand<T> {
	/**
	 * Command to execute on nodes in a graph
	 * @param nodeName the name of the current node
	 * @param object the value associated with the current node
	 * @throws CoreException if the implementation throws a CoreException
	 */
	void execute(String nodeName, T object) throws CoreException;
}
