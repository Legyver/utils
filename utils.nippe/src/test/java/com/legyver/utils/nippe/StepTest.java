package com.legyver.utils.nippe;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StepTest {

	@Test
	public void testOriginalNull() throws Exception {
		ClassA classA = null;
		String value = new Step<>(new Step<>(new Step<>(new Base<>(classA),
				c -> c.classB),
				c -> c.classC),
				c -> c.text).execute();
		assertThat(value).isNull();
	}

	@Test
	public void testNothingNull() throws Exception {
		ClassA classA = new ClassA(new ClassB(new ClassC("test text")));
		String value = new Step<>(new Step<>(new Step<>(new Base<>(classA),
				c -> c.classB),
				c -> c.classC),
				c -> c.text).execute();
		assertThat(value).isEqualTo("test text");
	}

	@Test
	public void testMiddleNull() throws Exception {
		ClassA classA = new ClassA(new ClassB(new ClassC("test text")));
		classA.classB = null;

		String value = new Step<>(new Step<>(new Step<>(new Base<>(classA),
				c -> c.classB),
				c -> c.classC),
				c -> c.text).execute();
		assertThat(value).isNull();
	}

	private class ClassA {
		protected ClassB classB;

		public ClassA(ClassB b) {
			this.classB = b;
		}
	}

	private class ClassB {

		protected ClassC classC;

		public ClassB(ClassC c) {
			this.classC = c;
		}
	}

	private class ClassC {
		public String text;

		public ClassC(String t) {
			this.text = t;
		}
	}
}
