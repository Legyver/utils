/**
 *  A utility for constructing properties out of other properties
 * @since 2.0
 */
module com.legyver.utils.propcross {
	requires com.legyver.core;
	requires com.legyver.utils.graphrunner;
	requires com.legyver.utils.slel;

	exports com.legyver.utils.propcross;
	provides com.legyver.core.license.LicenseService with com.legyver.utils.propcross.license.LicenseServiceImpl;
}