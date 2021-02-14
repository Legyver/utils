package com.legyver.utils.graphrunner.ctx.shared;

import com.legyver.utils.graphrunner.Graph;

import java.util.Map;

/**
 *
 *  A convenience class for situations where the value is a map common to all nodes but where getValue() only returns the value of that node.
 */
public class SharedMapCtx implements Graph.Payload {
	/**
	 * The name of the node
	 */
	private final String name;
	/**
	 * The shared context map of variables available to the {@link Graph}
	 */
	private final Map<String, Object> sharedContextMap;

	/**
	 * Construct a SharedMapCtx with a specified name and a preexisting value map
	 * @param name the name of the node
	 * @param sharedContextMap any preexisting values to load into the {@link #sharedContextMap}
	 */
	public SharedMapCtx(String name, Map<String, Object> sharedContextMap) {
		this.name = name;
		this.sharedContextMap = sharedContextMap;
	}

	/**
	 * Return the name of the current node.
	 * @return the node name
	 */
	@Override
	public String getNodeName() {
		return name;
	}

	/**
	 * Return the value set on the {@link #sharedContextMap}
	 * @return the value
	 */
	public Object getValue() {
		return sharedContextMap.get(name);
	}

	/**
	 * Set the value on the {@link #sharedContextMap}
	 * @param value the value to set
	 */
	public void setValue(Object value) {
		sharedContextMap.put(name, value);
	}
}
