package com.legyver.utils.graphrunner;

import com.legyver.core.exception.CoreException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

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
public class Graph<T> {
	protected final String name;
	private RunStrategy strategy = new RunAllStrategy();
	protected Map<String, Graph.GraphNode<T>> graph = new HashMap<>();
	/**
	 * A set of values shared by all nodes in the graph
	 */
	protected final Map<String, T> values;

	private Graph(String name, Map<String, T> values) {
		this.name = name;
		this.values = values;
	}

	public Graph(String name) {
		this(name, new HashMap<>());
	}

	public void setStrategy(RunStrategy strategy) {
		this.strategy = strategy;
	}

	Map<String, Graph.GraphNode<T>> getChildMap() {
		return graph;
	}

	void addPrerequisite(Graph.GraphNode graphNode) {
		//noop.  Root nodes have no pre-requisites
	}

	void addDependent(GraphNode graphNode) {
		graph.put(graphNode.name, graphNode);
	}

	void accept(String dependent, String predecessor) {
		GraphNode graphNode = find(predecessor);
		if (graphNode == null) {
			graphNode = new GraphNode(predecessor, values);
			graph.put(predecessor, graphNode);
		}

		GraphNode dependentNode = find(dependent);
		if (dependentNode == null) {
			dependentNode = new GraphNode(dependent, values);
		}

		if (!dependent.equals(predecessor)) {
			//remove dependent from root
			if (graph.containsKey(dependent)) {
				graph.remove(dependent);
			}
			graphNode.addDependent(dependentNode);
			dependentNode.addPrerequisite(graphNode);
		}
	}

	GraphNode find(String name) {
		Iterator<String> topIt = graph.keySet().iterator();
		if (graph.containsKey(name)) {
			return graph.get(name);
		}
		GraphNode graphNode = graph.get(name);
		while (topIt.hasNext() && graphNode == null) {
			String next = topIt.next();
			GraphNode temp = graph.get(next);
			graphNode = temp.find(name);
		}
		return graphNode;
	}

	public void executeStrategy(GraphExecutedCommand<T> command) throws CoreException {
		strategy.execute(this, command);
	}

	void run(GraphExecutedCommand<T> command) throws CoreException {
		for (String s: graph.keySet()) {
			GraphNode graphNode = graph.get(s);
			if (graphNode != null) {
				graphNode.run(command);
			}
		}
	}

	public Graph filter(String nodeToRun) {
		Graph filtered = new Graph(null, values);
		new DepthFirstSearch(graph, new BiConsumer<String, GraphNode<T>>() {
			@Override
			public void accept(String s, GraphNode<T> graphNode) {
				filtered.graph.put(s, graphNode);
			}
		}).search(nodeToRun);
		return filtered;
	}

	private void resetNode(ResetOptions options, GraphNode<T> graphNode) {
		if (options.resetEvaluatedFlag) {
			graphNode.evaluated = false;
		}
		if (options.resetEvaluationProgress) {
			graphNode.satisfiedNodeNames.clear();
		}
		graphNode.satisfiedNodeNames.clear();
		graphNode.graph.values().stream().forEach(node -> resetNode(options, node));
	}

	/**
	 *
	 * @param options: what to reset
	 */
	public void reset(ResetOptions options) {
		this.graph.values().stream()
				.forEach(node -> resetNode(options, node));
	}

	/**
	 * Set all {@link GraphNode} evaluated flags to false
	 * and clear all satisfied node names
	 */
	public void resetEvaluated() {
		reset(new ResetOptions()
				.evaluatedFlag()
				.evaluationProgress());
	}

	static class GraphNode<T> extends Graph<T> {
		private Set<String> requisiteNodeNames = new HashSet<>();
		private Set<String> satisfiedNodeNames = new HashSet<>();
		private boolean evaluated;

		GraphNode(String name, Map<String, T> values) {
			super(name, values);
		}

		@Override
		public void addPrerequisite(GraphNode graphNode) {
			requisiteNodeNames.add(graphNode.name);
		}

		@Override
		void run(GraphExecutedCommand command) throws CoreException {
			if (satisfiedNodeNames.size() == requisiteNodeNames.size() && !evaluated) {
				T target = values.get(name);
				command.execute(name, target);
				evaluated = true;

				for (String s: graph.keySet()) {
					GraphNode graphNode = graph.get(s);
					if (graphNode != null) {
						graphNode.satisfiedNodeNames.add(name);
						graphNode.run(command);
					}
				}
			}
		}
	}

	/**
	 * Builder for Graph.
	 * The individual nodes are specified as {@link Payload} payloads
	 * The connections are specified with the {@link Connection} sub-builder
	 */
	public static class Builder {
		private Graph graph = new Graph(null);

		public Graph build() {
			return graph;
		}

		public Builder nodes(Payload... payloads) {
			if (payloads != null) {
				Stream.of(payloads).forEach(payload -> {
					graph.graph.put(payload.getNodeName(), new GraphNode<>(payload.getNodeName(), graph.values));//all nodes seeded with reference to same value map
					graph.values.put(payload.getNodeName(), payload);
				});
			}

			return this;
		}

		public Builder connect(Connection connection) {
			graph.accept(connection.to, connection.from);
			return this;
		}
	}

	public static class Connection {
		private String from;
		private String to;

		public Connection from(String from) {
			this.from = from;
			return this;
		}

		public Connection to(String to) {
			this.to = to;
			return this;
		}
	}

	/**
	 * Whatever values you want to associate with a graph node
	 */
	public interface Payload {
		String getNodeName();
	}

	/**
	 * Options for resetting internal graph state
	 */
	public static class ResetOptions {
		private boolean resetEvaluatedFlag;
		private boolean resetEvaluationProgress;

		/**
		 * Set all {@link GraphNode} evaluated flags to false
		 */
		public ResetOptions evaluatedFlag() {
			resetEvaluatedFlag = true;
			return this;
		}

		/**
		 * Clear all {@link GraphNode} satisfied node names
		 */
		public ResetOptions evaluationProgress() {
			resetEvaluationProgress = true;
			return this;
		}

	}
}
