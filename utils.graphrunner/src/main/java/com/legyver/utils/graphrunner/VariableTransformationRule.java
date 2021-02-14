package com.legyver.utils.graphrunner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A rule for transforming values when updating the sharedContextMap
 * Ex1: Given
 *   originalPattern: "\\.format$"
 *   text: "build.date.format"
 *   transformOperation: TransformationOperation.upToLastIndexOf(".format")
 *   The result will be set on the internal map using the key: "build.date"
 */
public class VariableTransformationRule {
	/**
	 * The pattern to which this VariableTransformationRule applies
	 */
	private final Pattern originalPattern;
	/**
	 * The TransformationOperation to apply.
	 */
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

	/**
	 * Test if a specified text matches the {@link #originalPattern}
	 * @param text the text to test
	 * @return true if the specified text matches, otherwise false.
	 */
	public boolean matches(String text) {
		Matcher m = originalPattern.matcher(text);
		return m.find();
	}

	/**
	 * Applies the {@link #transformationOperation} to the specified text
	 * @param text the text to transform
	 * @return the result of the transform operation
	 */
	public String transform(String text) {
		return transformationOperation.transform(text);
	}
}
