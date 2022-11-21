/**
 * Annotation based JSON migration
 * @since 3.4
 */
module com.legyver.utils.jsonmigration {
    requires org.apache.logging.log4j;
    requires org.apache.commons.lang3;
    requires com.legyver.core;
    requires com.jayway.jsonpath;
    requires com.legyver.utils.mapadapt;
    exports com.legyver.utils.jsonmigration.adapter;
    exports com.legyver.utils.jsonmigration.annotation;
    exports com.legyver.utils.jsonmigration.version;
}