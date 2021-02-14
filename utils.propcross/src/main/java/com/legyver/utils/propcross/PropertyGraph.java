package com.legyver.utils.propcross;

import com.legyver.core.exception.CoreException;
import com.legyver.utils.graphrunner.*;
import com.legyver.utils.graphrunner.ctx.shared.SharedContextCommand;
import com.legyver.utils.graphrunner.ctx.shared.SharedMapCtx;
import com.legyver.utils.slel.ExpressionInterpreter;

import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A graph of properties used to evaluate cross-referencing properties.
 */
public class PropertyGraph {
	private final Map<String, Object> map;

	/**
	 * Construct a PropertyGraph
	 * @param map a map that corresponds to the union of any {@link java.util.Properties} to be included in the graph.
	 *            You may wish to use {@link com.legyver.utils.graphrunner.PropertyMap#of(Properties...)} to construct this    
	 */
	public PropertyGraph(Map<String, Object> map) {
		this.map = map;
	}

	/**
	 * Run the graph and evaluate any properties that might rely on other properties
	 * @param context the operation context to use when evaluating properties
	 * @throws CoreException if there is an error
	 */
	public void runGraph(OperationContext context) throws CoreException {
		Pattern jexlVar = context.getVariablePattern();
		VariableExtractionOptions variableExtractionOptions = context.getVariableExtractionOptions();
		VariableTransformationRule variableTransformationRule = context.getVariableTransformationRule();

		PropertyGraphFactory factory = new PropertyGraphFactory(variableExtractionOptions, variableTransformationRule);
		Graph<SharedMapCtx> contextGraph = factory.make(map, (s, o) -> new SharedMapCtx(s, map));

		contextGraph.executeStrategy(new SharedContextCommand() {
			@Override
			public void executeString(String nodeName, String currentValue) {
				Matcher m = jexlVar.matcher(currentValue);
				if (m.find()) {
					ExpressionInterpreter expressionInterpreter = new ExpressionInterpreter(map);
					String value = (String) expressionInterpreter.evaluate(currentValue);
					//update the map with the value
					String key = nodeName;
					//avoid overwriting the <transformationSuffix> property
					if (variableTransformationRule.matches(nodeName)) {
						key = variableTransformationRule.transform(nodeName);
					}
					map.put(key, value);
				}
			}
		});
	}


}
