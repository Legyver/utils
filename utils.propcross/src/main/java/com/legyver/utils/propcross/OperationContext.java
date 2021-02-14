package com.legyver.utils.propcross;

import com.legyver.utils.graphrunner.VariableExtractionOptions;
import com.legyver.utils.graphrunner.VariableTransformationRule;

import java.util.regex.Pattern;

/**
 * Define the rules for handling variables in the PropertyGraph
 */
public interface OperationContext {
	/**
	 * Pattern that identifies variables to resolve
	 * Example: properties ending in ".format" the variable pattern would be Pattern.compile("\\.format$")
	 * @return the compiled pattern
	 */
	Pattern getVariablePattern();

	/**
	 * Extraction options that determine how to get the variable name out of the expression.
	 * @return the extraction options to use
	 */
	VariableExtractionOptions getVariableExtractionOptions();

	/**
	 * Rules determining the property key transformation when placing the variable's evaluation result back on the graph.
	 * @return the transformation rule to apply to the property key
	 */
	VariableTransformationRule getVariableTransformationRule();
}
