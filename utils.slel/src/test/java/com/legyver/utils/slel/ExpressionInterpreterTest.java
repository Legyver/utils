package com.legyver.utils.slel;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

public class ExpressionInterpreterTest {

	@Test
	public void singleValue() {
		Map<String, String> context = new HashMap<>();
		context.put("var1", "Hello");
		ExpressionInterpreter expressionInterpreter = new ExpressionInterpreter(context);
		String result = expressionInterpreter.evaluate("${var1}");

		assertThat(result).isEqualTo("Hello");
	}

	@Test
	public void concatenation() {
		Map<String, String> context = new HashMap<>();
		context.put("var1", "Hello");
		context.put("var2", "World");
		ExpressionInterpreter expressionInterpreter = new ExpressionInterpreter(context);
		String result = expressionInterpreter.evaluate("${var1} ${var2}");

		assertThat(result).isEqualTo("Hello World");
	}

}
