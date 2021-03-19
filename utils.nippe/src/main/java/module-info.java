/**
 * @since 2.0
 */
module com.legyver.utils.nippe {
	requires com.legyver.core;
	exports com.legyver.utils.nippe;

	provides com.legyver.core.license.LicenseService with com.legyver.utils.nippe.license.LicenseServiceImpl;
}