/**
 * Run functions in order defined in a directed graph.
 */
module com.legyver.utils.graphrunner {
	requires transitive com.legyver.utils.wrapadapt;

	exports com.legyver.utils.graphrunner;
	exports com.legyver.utils.graphrunner.ctx.shared;
	provides com.legyver.core.license.LicenseService with com.legyver.utils.graphrunner.license.LicenseServiceImpl;
}