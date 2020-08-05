package com.legyver.util.graphrunner;

import java.util.Map;

/**
 *
 *  A convenience class for situations where the value is a map common to all nodes but where getValue() only returns the value of that node.
 */
public class SharedMapCtx implements Graph.Payload {
	private final String name;
	private final Map<String, Object> sharedContextMap;

	public SharedMapCtx(String name, Map<String, Object> sharedContextMap) {
		this.name = name;
		this.sharedContextMap = sharedContextMap;
	}

	@Override
	public String getNodeName() {
		return name;
	}

	public Object getValue() {
		return sharedContextMap.get(name);
	}

	public void setValue(Object value) {
		sharedContextMap.put(name, value);
	}
}
