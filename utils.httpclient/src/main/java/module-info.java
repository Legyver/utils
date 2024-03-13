/**
 * Command line interface module
 * @since 3.5
 */
module com.legyver.utils.cli {
    requires com.legyver.core;
    requires org.apache.logging.log4j;
    requires com.legyver.utils.adaptex;

    exports com.legyver.utils.httpclient;
    exports com.legyver.utils.httpclient.auth;
    exports com.legyver.utils.httpclient.exception;
    provides com.legyver.core.license.LicenseService with com.legyver.utils.httpclient.license.LicenseServiceImpl;
}