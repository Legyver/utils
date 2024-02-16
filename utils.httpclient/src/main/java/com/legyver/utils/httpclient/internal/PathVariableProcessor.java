package com.legyver.utils.httpclient.internal;

public class PathVariableProcessor implements UrlProcessor {

    private final String[] pathVariables;

    public PathVariableProcessor(String... pathVariables) {
        this.pathVariables = pathVariables;
    }

    @Override
    public String process(String sUrl) {
        if (pathVariables != null) {
            for (String pathVariable : pathVariables) {
                sUrl = sUrl.replaceFirst("\\{([a-zA-Z0-9\\-])+\\}", pathVariable);
            }
        }
        return sUrl;
    }
}
