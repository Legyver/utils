/**
 * @since 2.0
 */
module com.legyver.utils.wrapadapt {
	requires transitive com.legyver.core;

	exports com.legyver.utils.wrapadapt;
	provides com.legyver.core.license.LicenseService with com.legyver.utils.wrapadapt.license.LicenseServiceImpl;
}