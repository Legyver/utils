package com.legyver.util.graphrunner;

import org.junit.Test;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class GraphRunnerTest {
	@Test
	public void resolvesCDependsOnAandB() throws Exception {
		Counter counter = new Counter();
		Evaluated a = new Evaluated("A");
		Evaluated b = new Evaluated("B");
		Evaluated c = new Evaluated("C");
		Evaluated d = new Evaluated("D");
		Map<String, Evaluated> map = Stream.of(a, b, c, d).collect(Collectors.toMap(e->e.name, identity()));

		ContextGraph graph = new ContextGraph();
		graph.accept(c.name, a.name);
		graph.accept(c.name, b.name);

		GraphRunner<Evaluated> runner = new GraphRunner<>(map);
		runner.setCommand((nodeName, evaluated) -> {
			if (evaluated.count == -1) {
				evaluated.count = counter.next();
			}
		});
		GraphRunner.RunMap runMap = runner.runGraph(graph);
		assertThat(c.count, is(2));
		assertThat(d.count, is(-1));

		runner.runRemainingNodes(runMap);
		assertThat(d.count, is(3));
	}

	class Evaluated {
		final String name;
		int count = -1;

		Evaluated(String name) {
			this.name = name;
		}
	}

	class Counter {
		int count = 0;
		int next() {
			return count++;
		}
	}
}
