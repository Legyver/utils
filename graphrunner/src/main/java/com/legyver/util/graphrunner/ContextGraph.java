package com.legyver.util.graphrunner;

import com.legyver.core.exception.CoreException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * The graph is an inverse tree.
 * All the nodes converge to guarantee that if a succeeding node cannot be evaluated, it is because it exists on
 * one-or-more different paths, the exhaustion of which will be produce evaluable conditions.
 * Ex: a depends on b and c.  'a' will only be evaluated after both 'b' and 'c' have been evaluated
 *  root /
 *       /b depends-on a (not evaluable)
 *       /c depends-on a (evaluable)
 *            /a
 */
public class ContextGraph {
	final String name;
	Map<String, GraphNode> graph = new HashMap<>();

	ContextGraph(String name) {
		this.name = name;
	}

	public ContextGraph() {
		this("root");
	}

	public String getName() {
		return name;
	}

	public void accept(String dependent, String predecessor) {
		GraphNode graphNode = find(predecessor);
		if (graphNode == null) {
			graphNode = new GraphNode(predecessor);
			graph.put(predecessor, graphNode);
		}

		GraphNode dependentNode = find(dependent);
		if (dependentNode == null) {
			dependentNode = new GraphNode(dependent);
		}

		if (!dependent.equals(predecessor)) {
			graphNode.addDependent(dependentNode);
			dependentNode.addPrerequisite(graphNode);
		}
	}

	GraphNode find(String name) {
		Iterator<String> topIt = graph.keySet().iterator();
		GraphNode graphNode = graph.get(name);
		while (topIt.hasNext() && graphNode == null) {
			GraphNode temp = graph.get(topIt.next());
			graphNode = temp.find(name);
		}
		return graphNode;
	}

	public <T> void run(Map<String, T> targetMap, GraphRunner.GraphRunCommand<T> command, GraphRunner.RunMap runMap) throws CoreException {
		for (String s: graph.keySet()) {
			GraphNode graphNode = graph.get(s);
			graphNode.run(targetMap, command, runMap);
		}
	}

	private static class GraphNode extends ContextGraph {
		private Set<String> requisiteNodeNames = new HashSet<>();
		private Set<String> satisfiedNodeNames = new HashSet<>();
		private boolean evaluated;

		private GraphNode(String name) {
			super(name);
		}

		public void addDependent(GraphNode graphNode) {
			graph.put(graphNode.name, graphNode);
		}

		public void addPrerequisite(GraphNode graphNode) {
			requisiteNodeNames.add(graphNode.name);
		}

		@Override
		public <T> void run(Map<String, T> targetMap, GraphRunner.GraphRunCommand<T> command, GraphRunner.RunMap runMap) throws CoreException {
			if (satisfiedNodeNames.size() == requisiteNodeNames.size() && !evaluated) {
				T target = targetMap.get(name);
				command.execute(name, target);
				evaluated = true;
				runMap.addProcessed(name);
				for (String s: graph.keySet()) {
					GraphNode graphNode = graph.get(s);
					graphNode.satisfiedNodeNames.add(name);
					graphNode.run(targetMap, command, runMap);
				}
			}
		}
	}
}
