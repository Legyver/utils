package com.legyver.utils.graphrunner;

import java.util.regex.Pattern;

/**
 * The VariableExtractionOptions that identify a regular expression and group to extract
 */
public class VariableExtractionOptions {
	/**
	 * The Pattern to match
	 */
	private final Pattern tokenizerPattern;
	/**
	 * The group to extract
	 */
	private final int group;

	/**
	 * Match options to construct directional {@link Graph}
	 * @param tokenizerPattern : compatible with java.util.Matcher, used for matching variables to extract from value
	 * @param group: Matcher.group(group) to extract
	 * example: `${build.date.day} ${build.date.month} ${build.date.year}`
	 *   tokenizerPattern: \$\{(([a-z\.-])*)\}
	 *   group: 1
	 *   would give you build.date.day, build.date.month and build.date.year
	 */
	public VariableExtractionOptions(Pattern tokenizerPattern, int group) {
		this.tokenizerPattern = tokenizerPattern;
		this.group = group;
	}

	/**
	 * Return the tokenizer pattern
	 * @return the tokenizer pattern
	 */
	public Pattern getTokenizerPattern() {
		return tokenizerPattern;
	}

	/**
	 * The {@link java.util.regex.Matcher#group(int)} to extract
	 * @return the group index
	 */
	public int getGroup() {
		return group;
	}
}
