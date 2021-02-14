/**
 * @since 2.0
 */
module com.legyver.utils.extractex {
	requires transitive com.legyver.core;

	exports com.legyver.utils.extractex;
	provides com.legyver.core.license.LicenseService with com.legyver.utils.extractex.license.LicenseServiceImpl;
}