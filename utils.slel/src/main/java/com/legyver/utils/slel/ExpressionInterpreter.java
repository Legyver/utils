package com.legyver.utils.slel;

import java.util.Map;

/**
 * Simple Expression interpreter that only resolves sequential expressions as String concatenations
 * ex:
 * ExpressionInterpreter({"var1": "hello", "var2":"world"}).evaluate("${var1} ${var2}")
 * would result in "hello world"
 * @since 2.0
 */
public class ExpressionInterpreter {
	private final Map<String, ? extends Object> context;

	/**
	 * Construct an ExpressionInterpreter with the specified context
	 * @param context a Map of values that contains variables
	 */
	public ExpressionInterpreter(Map<String, ? extends Object> context) {
		this.context = context;
	}

	/**
	 * Evaluate the expression.
	 * @param expression the expression to be evaluated
	 * @return evaluated expression
	 */
	public String evaluate(String expression) {
		int indexStartEx = expression.indexOf(ExpressionVariable.STRING_VAR.getPrefix());
		if (indexStartEx < 0) {
			//base case: no expression
			return expression;
		}
		String left = expression.substring(0, indexStartEx);
		String right = expression.substring(indexStartEx + ExpressionVariable.STRING_VAR.getPrefix().length());
		int indexEndEx = right.indexOf(ExpressionVariable.STRING_VAR.getSuffix());
		if (indexEndEx < 0) {
			//unbalanced expression, don't treat as valid expression
			return expression;
		}
		String variable = right.substring(0, indexEndEx);
		right = right.substring(indexEndEx + ExpressionVariable.STRING_VAR.getSuffix().length());
		if (!context.containsKey(variable)) {
			//no variable known, so skip it.
			left = left + ExpressionVariable.STRING_VAR.getPrefix() + variable + ExpressionVariable.STRING_VAR.getSuffix();
		} else {
			Object value = context.get(variable);
			left = left + value;
		}
		right = evaluate(right);
		return left + right;
	}
}
