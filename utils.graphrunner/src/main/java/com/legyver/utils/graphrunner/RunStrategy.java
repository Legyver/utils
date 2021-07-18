package com.legyver.utils.graphrunner;

import com.legyver.core.exception.CoreException;

/**
 * Strategy for running nodes in a graph.
 */
public interface RunStrategy {

	/**
	 * Execute the command all identified nodes in the graph in the order specified in the directed graph.
	 * @param graph the directed graph containing all nodes and the directed edges in between
	 * @param command the command to be executed
	 * @param <T> The type the parameter that the command expects
	 * @throws CoreException if the command throws a CoreException
	 */
	<T> void execute(Graph<T> graph, GraphExecutedCommand<T> command) throws CoreException;
}
