package com.legyver.utils.graphrunner;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TransformationOperationTest {

	@Test
	public void upToLastIndexOf() throws Exception {
		TransformationOperation operation = TransformationOperation.upToLastIndexOf(".format");
		assertThat(operation.transform("build.date.format"), is("build.date"));
		assertThat(operation.transform("build.version.format"), is("build.version"));
		assertThat(operation.transform("build.message.format"), is("build.message"));
	}
}
