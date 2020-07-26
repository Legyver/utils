package com.legyver.util.graphrunner;

import com.legyver.core.exception.CoreException;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A scaffold to perform operations in the order specified by a directed {@link ContextGraph}
 */
public class GraphRunner<T> {
	private final Map<String, T> targetMap;
	private GraphRunCommand<T> command = (aThis, noop) -> {};

	public GraphRunner(Map<String, T> targetMap) {
		this.targetMap = targetMap;
	}

	public void setCommand(GraphRunCommand<T> command) {
		this.command = command;
	}

	public RunMap runGraph(ContextGraph contextGraph) throws CoreException {
		RunMap runMap = new RunMap();
		contextGraph.run(targetMap, command, runMap);
		return runMap;
	}

	public void runRemainingNodes(RunMap runMap) throws CoreException {
		runMap.complete(targetMap, command);
	}

	@FunctionalInterface
	public interface GraphRunCommand<T> {
		/**
		 * The operation to be run one the node.
		 * @param nodeName: name of current node
		 * @param object: value of the current node
		 * @throws CoreException
		 */
		void execute(String nodeName, T object) throws CoreException;
	}

	public class RunMap {
		private Set<String> processed = new HashSet<>();

		void complete(Map<String,T> targetMap, GraphRunCommand<T> command) throws CoreException {
			for (String s : targetMap.keySet()) {
				if (!processed.contains(s)) {
					T target = targetMap.get(s);
					command.execute(s, target);
				}
			}
		}

		void addProcessed(String name) {
			processed.add(name);
		}
	}
}
