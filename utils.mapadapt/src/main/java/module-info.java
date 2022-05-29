/**
 * @since 2.1.2.0
 */
module com.legyver.utils.mapadapt {
    requires com.legyver.core;
    requires org.apache.logging.log4j;
    exports com.legyver.utils.mapadapt;

    provides com.legyver.core.license.LicenseService with com.legyver.utils.mapadapt.license.LicenseServiceImpl;
}