/**
 * Reflection utilities
 * @since 3.4
 */
module com.legyver.utils.ruffles {
    exports com.legyver.utils.ruffles;
    requires com.legyver.core;
    requires org.apache.commons.io;
    requires org.apache.commons.lang3;
    requires org.apache.logging.log4j;

    provides com.legyver.core.license.LicenseService with com.legyver.utils.ruffles.license.LicenseServiceImpl;
}