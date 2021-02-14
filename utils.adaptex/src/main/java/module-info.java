/**
 * An extraction adapter
 */
module com.legyver.utils.adaptex {
	requires com.legyver.core;
	exports com.legyver.utils.adaptex;
	provides com.legyver.core.license.LicenseService with com.legyver.utils.adaptex.license.LicenseServiceImpl;
}