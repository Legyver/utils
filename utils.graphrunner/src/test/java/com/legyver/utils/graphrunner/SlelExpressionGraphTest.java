package com.legyver.utils.graphrunner;

import com.legyver.core.exception.CoreException;
import com.legyver.utils.graphrunner.ctx.shared.SharedContextCommand;
import com.legyver.utils.graphrunner.ctx.shared.SharedMapCtx;
import com.legyver.utils.slel.ExpressionInterpreter;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copy of {@link JexlExpressionGraphTest} with the slel ExpressionInterpreter
 */
public class SlelExpressionGraphTest extends AbstractGraphExpressionTest {
	private static final String SLEL_VARIABLE = "\\$\\{(([a-z\\.-])*)\\}";

	public SlelExpressionGraphTest() {
		super(".slel", Pattern.compile(SLEL_VARIABLE));
	}

	@Override
	protected void evaluateGraph(Graph<SharedMapCtx> contextGraph, VariableTransformationRule variableTransformationRule, Map<String, Object> map) throws CoreException {
		contextGraph.executeStrategy(new SharedContextCommand() {
			@Override
			public void executeString(String ctx, String currentValue) {
				Matcher m = varPattern.matcher(currentValue);
				if (m.find()) {
					ExpressionInterpreter expressionInterpreter = new ExpressionInterpreter(map);
					String value = expressionInterpreter.evaluate(currentValue);
					//update the map with the value
					String key = ctx;
					//avoid overwriting the .format property
					if (variableTransformationRule.matches(ctx)) {
						key = variableTransformationRule.transform(ctx);
					}
					map.put(key, value);
				}
			}
		});
	}
}
