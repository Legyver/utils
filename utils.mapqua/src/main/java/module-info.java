/**
 * @since 2.0
 */
module com.legyver.utils.mapqua {
	requires com.legyver.core;
	requires transitive com.legyver.utils.jackiso;

	exports com.legyver.utils.mapqua;
	exports com.legyver.utils.mapqua.mapbacked;
	provides com.legyver.core.license.LicenseService with com.legyver.utils.mapqua.license.LicenseServiceImpl;
}