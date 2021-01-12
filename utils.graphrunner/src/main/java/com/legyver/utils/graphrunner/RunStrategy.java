package com.legyver.utils.graphrunner;

import com.legyver.core.exception.CoreException;

/**
 * Strategy for running nodes in a graph.
 */
public interface RunStrategy {

	<T> void execute(Graph graph, GraphExecutedCommand<T> command) throws CoreException;
}
