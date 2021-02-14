/**
 * @since 2.0
 */
module com.legyver.utils.mapqua {
	requires com.google.gson;
	requires com.legyver.core;

	exports com.legyver.utils.mapqua;
	exports com.legyver.utils.mapqua.mapbacked;
	provides com.legyver.core.license.LicenseService with com.legyver.utils.mapqua.license.LicenseServiceImpl;
}