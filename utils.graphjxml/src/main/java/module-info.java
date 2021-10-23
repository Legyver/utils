/**
 * A Java Graph to XML conversion utility
 * @since 2.1.1
 */
module com.legyver.utils.graphjxml  {
    requires com.legyver.core;
    requires org.apache.logging.log4j;
    requires java.xml;

    exports com.legyver.utils.graphjxml;
    exports com.legyver.utils.graphjxml.reader;
    exports com.legyver.utils.graphjxml.writer;
    provides com.legyver.core.license.LicenseService with com.legyver.utils.graphjxml.license.LicenseServiceImpl;
}