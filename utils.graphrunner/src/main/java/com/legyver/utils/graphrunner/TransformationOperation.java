package com.legyver.utils.graphrunner;

public interface TransformationOperation {

	String transform(String original);

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
