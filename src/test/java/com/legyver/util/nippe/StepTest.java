package com.legyver.util.nippe;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class StepTest {

	@Test
	public void testOriginalNull() {
		ClassA classA = null;
		String value = new Step<>(new Step<>(new Step<>(new Base<>(classA),
				c -> c.classB),
				c -> c.classC),
				c -> c.text).execute();
		assertThat(value, nullValue());
	}

	@Test
	public void testNothingNull() {
		ClassA classA = new ClassA(new ClassB(new ClassC("test text")));
		String value = new Step<>(new Step<>(new Step<>(new Base<>(classA),
				c -> c.classB),
				c -> c.classC),
				c -> c.text).execute();
		assertThat(value, is("test text"));
	}

	@Test
	public void testMiddleNull() {
		ClassA classA = new ClassA(new ClassB(new ClassC("test text")));
		classA.classB = null;

		String value = new Step<>(new Step<>(new Step<>(new Base<>(classA),
				c -> c.classB),
				c -> c.classC),
				c -> c.text).execute();
		assertThat(value, nullValue());
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
