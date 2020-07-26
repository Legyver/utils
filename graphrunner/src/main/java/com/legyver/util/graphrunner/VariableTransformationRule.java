package com.legyver.util.graphrunner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariableTransformationRule {
	private final Pattern originalPattern;
	private final TransformationOperation transformationOperation;

	/**
	 * Provide variable transformation rules to be accounted for in graph creation
	 * example: the result of the operation on build.date.format injects a variable called build.date
	 *   originalPattern:
	 * @param originalPattern: apply the operation to variables that match this
	 * @param transformationOperation: operation to appy
	 */
	public VariableTransformationRule(Pattern originalPattern, TransformationOperation transformationOperation) {
		this.originalPattern = originalPattern;
		this.transformationOperation = transformationOperation;
	}

	public boolean matches(String text) {
		Matcher m = originalPattern.matcher(text);
		return m.find();
	}

	public String transform(String text) {
		return transformationOperation.transform(text);
	}
}
