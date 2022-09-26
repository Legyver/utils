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
	/**
	 * The graph/node names
	 */
	protected final String name;
	private RunStrategy strategy = new RunAllStrategy();
	/**
	 * The nodes of the graph
	 */
	protected Map<String, Graph.GraphNode<T>> graph = new HashMap<>();
	/**
	 * A set of values shared by all nodes in the graph
	 */
	protected final Map<String, T> values;

	private Graph(String name, Map<String, T> values) {
		this.name = name;
		this.values = values;
	}

	/**
	 * Construct a graph with a specified name
	 * @param name the name of the graph/node
	 */
	public Graph(String name) {
		this(name, new HashMap<>());
	}

	/**
	 * Set the RunStrategy for the graph
	 * @param strategy the RunStrategy to use
	 */
	public void setStrategy(RunStrategy strategy) {
		this.strategy = strategy;
	}

	Map<String, Graph.GraphNode<T>> getChildMap() {
		return graph;
	}

	void addPrerequisite(Graph.GraphNode<T> graphNode) {
		//noop.  Root nodes have no pre-requisites
	}

	void addDependent(GraphNode<T> graphNode) {
		graph.put(graphNode.name, graphNode);
	}

	void accept(String dependent, String predecessor) {
		GraphNode<T> graphNode = find(predecessor);
		if (graphNode == null) {
			graphNode = new GraphNode<>(predecessor, values);
			graph.put(predecessor, graphNode);
		}

		GraphNode<T> dependentNode = find(dependent);
		if (dependentNode == null) {
			dependentNode = new GraphNode<>(dependent, values);
		}

		if (!dependent.equals(predecessor)) {
			//remove dependent from root
			graph.remove(dependent);
			graphNode.addDependent(dependentNode);
			dependentNode.addPrerequisite(graphNode);
		}
	}

	GraphNode<T> find(String name) {
		Iterator<String> topIt = graph.keySet().iterator();
		if (graph.containsKey(name)) {
			return graph.get(name);
		}
		GraphNode<T> graphNode = graph.get(name);
		while (topIt.hasNext() && graphNode == null) {
			String next = topIt.next();
			GraphNode<T> temp = graph.get(next);
			graphNode = temp.find(name);
		}
		return graphNode;
	}

	/**
	 * Run the specified command according to the {@link #strategy}
	 * @param command the command to run
	 * @throws CoreException if the command throws a CoreException
	 */
	public void executeStrategy(GraphExecutedCommand<T> command) throws CoreException {
		strategy.execute(this, command);
	}

	void run(GraphExecutedCommand<T> command) throws CoreException {
		for (String s: graph.keySet()) {
			GraphNode<T> graphNode = graph.get(s);
			if (graphNode != null) {
				graphNode.run(command);
			}
		}
	}

	/**
	 * Create a new sub Graph from the existing graph based on a filter criteria
	 * @param nodeToRun the node to use as the basis of the new graph
	 * @return the filtered Graph
	 */
	public Graph<T> filter(String nodeToRun) {
		Graph<T> filtered = new Graph<>(null, values);
		new DepthFirstSearch<>(graph, new BiConsumer<String, GraphNode<T>>() {
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
		graphNode.graph.values().forEach(node -> resetNode(options, node));
	}

	/**
	 *
	 * @param options: what to reset
	 */
	public void reset(ResetOptions options) {
		this.graph.values()
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
		void run(GraphExecutedCommand<T> command) throws CoreException {
			if (satisfiedNodeNames.size() == requisiteNodeNames.size() && !evaluated) {
				T target = values.get(name);
				command.execute(name, target);
				evaluated = true;

				for (String s: graph.keySet()) {
					GraphNode<T> graphNode = graph.get(s);
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
	public static class Builder<T extends Payload> {
		private Graph<T> graph = new Graph<T>(null);

		/**
		 * Build the graph
		 * @return the Graph
		 */
		public Graph<T> build() {
			return graph;
		}

		/**
		 * Constructor for the Builder.
		 * For each payload:
		 *  the graph map adds a GraphNode for the payload
		 *  the graph values map accepts the value
		 * @param payloads the values to be associated with each node
		 * @return the Builder to further allow construction of the Graph
		 */
		@SafeVarargs
		public final Builder nodes(T... payloads) {
			if (payloads != null) {
				Stream.of(payloads).forEach(payload -> {
					graph.graph.put(payload.getNodeName(), new GraphNode<>(payload.getNodeName(), graph.values));//all nodes seeded with reference to same value map
					graph.values.put(payload.getNodeName(), payload);
				});
			}

			return this;
		}

		/**
		 * Connect two nodes via a connection
		 * @param connection the connection to apply
		 * @return the Builder to continue building the graph
		 */
		public Builder<T> connect(Connection connection) {
			graph.accept(connection.to, connection.from);
			return this;
		}
	}

	/**
	 * Create a directed edge between two nodes
	 */
	public static class Connection {
		private String from;
		private String to;

		/**
		 * Specify the source node of the edge
		 * @param from the node the arrow starts at
		 * @return builder for specifying the target of the arrow
		 */
		public Connection from(String from) {
			this.from = from;
			return this;
		}

		/**
		 * Specify the target node of the edge.
		 * @param to the node the arrow points to
		 * @return builder for specifying the start of the arrow
		 */
		public Connection to(String to) {
			this.to = to;
			return this;
		}
	}

	/**
	 * Whatever values you want to associate with a graph node
	 */
	public interface Payload {
		/**
		 * Gets the name of the associated node.
		 * @return the node name
		 */
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
		 * @return builder to allow performing additional reset options
		 */
		public ResetOptions evaluatedFlag() {
			resetEvaluatedFlag = true;
			return this;
		}

		/**
		 * Clear all {@link GraphNode} satisfied node names
		 * @return builder to allow performing additional reset options
		 */
		public ResetOptions evaluationProgress() {
			resetEvaluationProgress = true;
			return this;
		}

	}
}
