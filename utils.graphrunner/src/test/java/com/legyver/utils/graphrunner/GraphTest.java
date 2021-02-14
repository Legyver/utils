package com.legyver.utils.graphrunner;

import com.legyver.core.exception.CoreException;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;

public class GraphTest {
	@Test
	public void resolvesCDependsOnAandB() throws Exception {
		TestData testData = new TestData();
		Graph graph = testData.makeGraph();
		graph.setStrategy(new RunAllStrategy());//this is also the default strategy
		Counter counter = new Counter();

		graph.executeStrategy(new GraphExecutedCommand<Evaluated>() {
			@Override
			public void execute(String nodeName, Evaluated evaluated) throws CoreException {
				if (evaluated.count == -1) {
					evaluated.count = counter.next();
				}
			}
		});

		testData.assertAllRunInOrder();
	}
	@Test
	public void justRunOrphan() throws Exception {
		TestData testData = new TestData();
		Graph graph = testData.makeGraph();
		graph.setStrategy(new RunWithDependentsStrategy(testData.d.name));
		Counter counter = new Counter();

		graph.executeStrategy(new GraphExecutedCommand<Evaluated>() {
			@Override
			public void execute(String nodeName, Evaluated evaluated) throws CoreException {
				if (evaluated.count == -1) {
					evaluated.count = counter.next();
				}
			}
		});

		testData.assertOnlyDRun();
	}

	@Test
	public void justRunB() throws Exception {
		TestData testData = new TestData();
		Graph graph = testData.makeGraph();
		graph.setStrategy(new RunWithDependentsStrategy(testData.b.name));
		Counter counter = new Counter();

		graph.executeStrategy(new GraphExecutedCommand<Evaluated>() {
			@Override
			public void execute(String nodeName, Evaluated evaluated) throws CoreException {
				if (evaluated.count == -1) {
					evaluated.count = counter.next();
				}
			}
		});

		testData.assertOnlyBRun();
	}

	@Test
	public void runningCRunsAAndB() throws Exception {
		TestData testData = new TestData();
		Graph graph = testData.makeGraph();
		graph.setStrategy(new RunWithDependentsStrategy(testData.c.name));
		Counter counter = new Counter();

		graph.executeStrategy(new GraphExecutedCommand<Evaluated>() {
			@Override
			public void execute(String nodeName, Evaluated evaluated) throws CoreException {
				if (evaluated.count == -1) {
					evaluated.count = counter.next();
				}
			}
		});

		testData.assertABCRun();
	}


	class Evaluated implements Graph.Payload {
		final String name;
		int count = -1;

		Evaluated(String name) {
			this.name = name;
		}

		@Override
		public String getNodeName() {
			return name;
		}
	}

	class Counter {
		int count = 0;
		int next() {
			return count++;
		}
	}

	class TestData {
		Evaluated a = new Evaluated("A");
		Evaluated b = new Evaluated("B");
		Evaluated c = new Evaluated("C");
		Evaluated d = new Evaluated("D");

		Graph makeGraph() {
			return new Graph.Builder()
					.nodes(a, b, c, d)
					.connect(new Graph.Connection()
							.from(a.getNodeName())
							.to(c.getNodeName()))
					.connect(new Graph.Connection()
							.from(b.getNodeName())
							.to(c.getNodeName()))
					.build();
		}

		private void assertAllRunInOrder() {
			//assert a and b both evaluated
			assertThat(a.count, is(greaterThan(-1)));
			assertThat(b.count, is(greaterThan(-1)));

			//assert that c was evaluated after a and b
			assertThat(c.count, is(greaterThan(a.count)));
			assertThat(c.count, is(greaterThan(b.count)));

			//assert that orphan node d was also evaluated
			assertThat(d.count, is(greaterThan(-1)));
		}

		private void assertOnlyDRun() {
			//assert a, b, c not evaluated
			assertThat(a.count, is(-1));
			assertThat(b.count, is(-1));
			assertThat(c.count, is(-1));

			//assert that node d was evaluated
			assertThat(d.count, is(greaterThan(-1)));
		}

		private void assertOnlyBRun() {
			//assert a, c, d not evaluated
			assertThat(a.count, is(-1));
			assertThat(c.count, is(-1));
			assertThat(d.count, is(-1));

			//assert that node b was evaluated
			assertThat(b.count, is(greaterThan(-1)));
		}

		public void assertABCRun() {
			//assert a and b both evaluated
			assertThat(a.count, is(greaterThan(-1)));
			assertThat(b.count, is(greaterThan(-1)));

			//assert that c was evaluated after a and b
			assertThat(c.count, is(greaterThan(a.count)));
			assertThat(c.count, is(greaterThan(b.count)));

			//assert that orphan node d was not evaluated
			assertThat(d.count, is(-1));
		}
	}

}
