/**
 * @since 2.1
 */
module com.legyver.utils.jackiso {
	requires com.legyver.core;
	requires com.fasterxml.jackson.datatype.jsr310;
	requires com.fasterxml.jackson.databind;
	exports com.legyver.utils.jackiso;

	provides com.legyver.core.license.LicenseService with com.legyver.utils.jackiso.license.LicenseServiceImpl;
}