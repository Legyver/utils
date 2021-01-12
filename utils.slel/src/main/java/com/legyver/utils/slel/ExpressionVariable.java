package com.legyver.utils.slel;

/**
 * Types of variables supported in variable resolution and their corresponding prefix and suffix demarcation
 * @since 2.0
 */
public enum ExpressionVariable {
	/**
	 * A String variable
	 * ex: "Hello ${name}"
	 */
	STRING_VAR("${","}");
	private final String prefix;
	private final String suffix;

	ExpressionVariable(String prefix, String suffix) {
		this.prefix = prefix;
		this.suffix = suffix;
	}

	/**
	 * Get the expression prefix
	 * ex: "${"
	 * @return prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * Get the expression suffix
	 * ex: "}"
	 * @return suffix
	 */
	public String getSuffix() {
		return suffix;
	}

}
