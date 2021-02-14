package com.legyver.utils.propcross;

import com.legyver.utils.graphrunner.TransformationOperation;
import com.legyver.utils.graphrunner.VariableExtractionOptions;
import com.legyver.utils.graphrunner.VariableTransformationRule;

import java.util.regex.Pattern;

import static com.legyver.utils.slel.Patterns.SLEL_VARIABLE;

/**
 * The OperationContext for SLEL variables
 */
public class SlelOperationContext implements OperationContext {
	private final Pattern variablePattern;
	private final VariableExtractionOptions variableExtractionOptions;
	private final VariableTransformationRule variableTransformationRule;

	/**
	 * Construct an OperationContext for SLEL variables
	 * @param transformationSuffix the that identifies variable to be evaluated. Note: this only supports one dot ('.') in the suffix, and it must be the leading character if present.
	 *                             Acceptable Example: ".format"
	 *                             Problematic Example ".special.format
	 */
	public SlelOperationContext(String transformationSuffix) {
		this.variablePattern = Pattern.compile(SLEL_VARIABLE);
		this.variableExtractionOptions = new VariableExtractionOptions(variablePattern, 1);
		String rulePattern = transformationSuffix + "$";
		if (rulePattern.startsWith(".")) {
			//escape the leading '.'
			rulePattern = "\\." + rulePattern.substring(1);
		}
		this.variableTransformationRule = new VariableTransformationRule(Pattern.compile(rulePattern),
				TransformationOperation.upToLastIndexOf(transformationSuffix));
	}

	@Override
	public Pattern getVariablePattern() {
		return variablePattern;
	}

	@Override
	public VariableExtractionOptions getVariableExtractionOptions() {
		return variableExtractionOptions;
	}

	@Override
	public VariableTransformationRule getVariableTransformationRule() {
		return variableTransformationRule;
	}
}
