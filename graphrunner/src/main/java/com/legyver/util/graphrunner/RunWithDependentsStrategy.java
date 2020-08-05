package com.legyver.util.graphrunner;

import com.legyver.core.exception.CoreException;

/**
 *
 * Run only nodes in the graph necessary to run the nodeToRun.
 * Guarantees that any node that is run is only evaluated once, and that as soon as a node is evaluable it is evaluated.
 *
 */
public class RunWithDependentsStrategy implements RunStrategy {
	/**
	 * Name of node to run.
	 */
	private final String nodeToRun;

	public RunWithDependentsStrategy(String nodeToRun) {
		this.nodeToRun = nodeToRun;
	}

	@Override
	public <T> void execute(Graph graph, GraphExecutedCommand<T> command) throws CoreException {
		Graph filtered = graph.filter(nodeToRun);
		filtered.run(command);
	}
}
