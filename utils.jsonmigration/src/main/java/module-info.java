/**
 * Annotation based JSON migration
 * @since 3.4
 */
module com.legyver.utils.jsonmigration {
    requires com.jayway.jsonpath;
    requires com.legyver.core;
    requires com.legyver.utils.mapadapt;
    requires com.legyver.utils.ruffles;

    requires org.apache.logging.log4j;
    requires org.apache.commons.lang3;

    exports com.legyver.utils.jsonmigration.adapter;
    exports com.legyver.utils.jsonmigration.annotation;
    exports com.legyver.utils.jsonmigration.version;

    provides com.legyver.core.license.LicenseService with com.legyver.utils.jsonmigration.license.LicenseServiceImpl;
}