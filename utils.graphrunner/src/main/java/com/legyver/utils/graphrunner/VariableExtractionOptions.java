package com.legyver.utils.graphrunner;

import java.util.regex.Pattern;

public class VariableExtractionOptions {
	private final Pattern tokenizerPattern;
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

	public Pattern getTokenizerPattern() {
		return tokenizerPattern;
	}

	public int getGroup() {
		return group;
	}
}
