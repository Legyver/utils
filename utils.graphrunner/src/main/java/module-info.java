/**
 * Run functions in order defined in a directed graph.
 */
module com.legyver.utils.graphrunner {
	requires transitive com.legyver.core;
	requires transitive com.legyver.utils.wrapadapt;

	exports com.legyver.utils.graphrunner;
	exports com.legyver.utils.graphrunner.ctx.shared;
}