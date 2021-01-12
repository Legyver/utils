package com.legyver.utils.graphrunner;

import com.legyver.core.exception.CoreException;
import com.legyver.utils.graphrunner.ctx.shared.SharedContextCommand;
import com.legyver.utils.graphrunner.ctx.shared.SharedMapCtx;
import org.apache.commons.jexl3.*;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Including this test as a POC.
 * jexl3 is a testCompile to avoid introducing a compile dependency on jexl3
 */
public class JexlExpressionGraphTest extends AbstractGraphExpressionTest {
	private static final String JEXL_VARIABLE = "\\$\\{(([a-z\\.-])*)\\}";

	public JexlExpressionGraphTest() {
		super(".jexl", Pattern.compile(JEXL_VARIABLE));
	}

	@Override
	protected void evaluateGraph(Graph<SharedMapCtx> contextGraph , VariableTransformationRule variableTransformationRule, Map<String, Object> map) throws CoreException {
		JexlEngine jexl = new JexlBuilder().create();
		JexlContext context = new MapContext(map);

		contextGraph.executeStrategy(new SharedContextCommand() {
			@Override
			public void executeString(String ctx, String currentValue) {
				Matcher m = varPattern.matcher(currentValue);
				if (currentValue.contains("${") && !currentValue.startsWith("`")) {
					return;//jexl will blow up evaluating an expression where backticks are not present
				}
				if (m.find()) {
					JexlExpression expression = jexl.createExpression(currentValue);
					String value = (String) expression.evaluate(context);
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
