package com.legyver.util.graphrunner;

import com.legyver.core.exception.CoreException;

/**
 * Run all nodes in the graph.  Guarantees that all nodes are only evaluated once, and that as soon as a node is evaluable it is evaluated.
 */
public class RunAllStrategy implements RunStrategy {

	@Override
	public <T> void execute(Graph graph, GraphExecutedCommand<T> command) throws CoreException {
		graph.run(command);
	}
}
