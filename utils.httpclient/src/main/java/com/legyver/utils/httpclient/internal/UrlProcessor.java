package com.legyver.utils.httpclient.internal;

/**
 * Processor that modifies a URL in some manner
 */
public interface UrlProcessor {
    /**
     * Process the URL
     * @param sUrl the url to be processed
     * @return the processed url
     */
    String process(String sUrl);
}
