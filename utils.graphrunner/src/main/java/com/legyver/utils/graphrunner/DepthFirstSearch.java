package com.legyver.utils.graphrunner;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Search a graph for nodes matching a string.
 * Any branch in which the node is found is passed to the matchHandler.
 * This is a depth-first search that returns aborts a branch and returns true to the root as soon as a match in a branch is detected.
 *
 * @param <T> Type of Graph
 */
public class DepthFirstSearch<T extends Graph> implements TreeSearch {

	private final Map<String, T> graph;
	private final BiConsumer<String, T> matchHandler;

	/**
	 * Construct a DepthFirstSearch of a graph.
	 * @param graph The graph nodes to search
	 * @param matchHandler the handler applied to each matched node
	 */
	public DepthFirstSearch(Map<String, T> graph, BiConsumer<String, T> matchHandler) {
		this.graph = graph;
		this.matchHandler = matchHandler;
	}

	@Override
	public void search(String searchString) {
		for (String s : graph.keySet()) {
			T graphNode = graph.get(s);
			//not combining these methods because we only want to add the root nodes
			//additionally we want to exhaustively search all the root nodes, but return on first positive for all child branches
			if (s.equals(searchString)  || branchContains(searchString, graphNode.getChildMap())) {
				matchHandler.accept(s, graphNode);
			}
		}
	}

	private boolean branchContains(String searchString, Map branch) {
		for (Object key : branch.keySet()) {
			if (searchString.equals(key)  /*|| branchContains(searchString, (Map<String, T>) graphNode.getChildMap())*/) {
				return true;//return on first positive
			}
		}
		return false;
	}
}
