/**
 * Command line interface module
 * @since 3.5
 */
module com.legyver.utils.cli {
    requires com.legyver.core;
    requires org.apache.logging.log4j;

    exports com.legyver.utils.cli;
    exports com.legyver.utils.cli.exception;
    provides com.legyver.core.license.LicenseService with com.legyver.utils.cli.license.LicenseServiceImpl;
}