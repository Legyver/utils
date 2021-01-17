package com.legyver.utils.graphrunner;

/**
 * Transform a string to a new string.
 * ie: the new string could be a substring of the original string
 * The main use-case is to support creating new properties based on the evaluation of old properties
 */
public interface TransformationOperation {

	/**
	 * Transform a string
	 * @param original the original string to apply the transformation operation on
	 * @return the transformed value.
	 */
	String transform(String original);

	/**
	 * Transformation to return everything up to the last occurrence of a String
	 * Example:
	 *   original: "build.date.format"
	 *   upToLastIndexOf: ".format"
	 *   would result in: "build.date"
	 * @param lastIndexOf the string pattern to find the last index of
	 * @return the TransformationOperation
	 */
	static TransformationOperation upToLastIndexOf(String lastIndexOf) {
		return new TransformationOperation() {
			@Override
			public String transform(String original) {
				String result = original;
				if (original != null && lastIndexOf != null) {
					int lastIndex = original.lastIndexOf(lastIndexOf);
					if (lastIndex > -1) {
						result = original.substring(0, lastIndex);
					}
				}
				return result;
			}
		};
	}
}
