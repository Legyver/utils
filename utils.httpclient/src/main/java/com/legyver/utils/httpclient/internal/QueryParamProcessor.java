package com.legyver.utils.httpclient.internal;

import java.util.Map;

/**
 * Process query parameters.
 * - Adding query parameters to a url with no query params
 * - Modifying query parameter values in a url with query parameters
 * - Appending query parameters to url with existing parameters
 */
public class QueryParamProcessor implements UrlProcessor {

    private final Map<String, String> queryParams;

    /**
     * Construct a processor to process query parameters
     * @param queryParams the query parameters to put into the url
     */
    public QueryParamProcessor(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    @Override
    public String process(String sUrl) {
        if (queryParams != null) {
            boolean applyQuestion = !sUrl.contains("?");
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (sUrl.contains(key + "=")) {
                    String pre = sUrl.substring(0, sUrl.lastIndexOf(key + "="));
                    String post = sUrl.substring(sUrl.lastIndexOf(key + "=") + key.length() + 1);
                    if (post.contains("&")) {
                        post = post.substring(post.indexOf('&'));
                    } else {
                        post = "";
                    }
                    sUrl = new StringBuilder(pre)
                            .append(key)
                            .append("=")
                            .append(value)
                            .append(post).toString();
                } else if (applyQuestion) {
                    sUrl = new StringBuilder(sUrl)
                            .append("?")
                            .append(key)
                            .append("=")
                            .append(value).toString();
                    applyQuestion = false;
                } else {
                    sUrl = new StringBuilder(sUrl)
                            .append("&")
                            .append(key)
                            .append("=")
                            .append(value).toString();
                }
            }
        }
        return sUrl;
    }
}
