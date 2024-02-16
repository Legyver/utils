package com.legyver.utils.httpclient;

import com.legyver.utils.httpclient.auth.Auth;
import com.legyver.utils.httpclient.auth.AuthOptions;
import com.legyver.utils.httpclient.exception.ResponseCodeException;
import com.legyver.utils.httpclient.internal.PathVariableProcessor;
import com.legyver.utils.httpclient.internal.QueryParamProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;


/**
 * A HttpClient wrapper for Http(s)URLConnection
 */
public class HttpClient {
    private static final Logger logger = LogManager.getLogger(HttpClient.class);

    private final Map<String, String> defaultHeaders;
    private final Map<String, String> defaultQueryParams;
    private final Auth auth;
    private final AuthOptions authOptions;
    private final String[] pathVariables;

    private HttpClient(Map<String, String> defaultHeaders, Map<String, String> queryParams, Auth auth, AuthOptions authOptions, String[] pathVariables) {
        this.defaultHeaders = defaultHeaders;
        this.defaultQueryParams = queryParams;
        this.auth = auth;
        this.authOptions = authOptions;
        this.pathVariables = pathVariables;
    }

    /**
     * Get client builder
     * @return a client builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Execute a GET request with the given URL and the default headers, query parameters and authentication data supplied via the builder
     * @param sUrl the URL to send the request to
     * @return the response InputStream
     * @throws IOException if there is an error in communicating with the remote server
     * @throws ResponseCodeException if the request returns a response code other than 200
     */
    public InputStream get(String sUrl) throws IOException, ResponseCodeException {
        return send(Method.GET, sUrl);
    }


    /**
     * Execute a request with the given method, URL and the default headers, query parameters and authentication data supplied via the builder
     * @param method the method of the request
     * @param sUrl the URL to send the request to
     * @return the response InputStream
     * @throws IOException if there is an error in communicating with the remote server
     * @throws ResponseCodeException if the request returns a response code other than 200
     */
    public InputStream send(Method method, String sUrl) throws IOException, ResponseCodeException {
        return send(method, sUrl, defaultHeaders, defaultQueryParams, auth, authOptions, pathVariables);
    }

    /**
     * Execute a request with the given method, URL, headers, query parameters, authentication scheme and authentication data.  No default values are applied
     * @param method the method of the request
     * @param sUrl the URl to send the request to
     * @param headers headers to apply to the request.
     * @param queryParams query parameters to add to the request URL. If any of these query parameters are already in the URL, those will be usurped by these
     * @param auth the authentication scheme to apply to the request
     * @param authOptions authentication data for the authentication scheme
     * @param pathVariables any path variables to be resolved. Replace (in order)of first occurrence marked by regex \{([a-zA-Z0-9\-])+\})
     * @return the response InputStream
     * @throws IOException if there is an error communicating with the remote server
     * @throws ResponseCodeException if the request returns a response code other than 200
     */
    public InputStream send(Method method, String sUrl, Map<String, String> headers, Map<String, String> queryParams, Auth auth, AuthOptions authOptions, String... pathVariables) throws IOException, ResponseCodeException {
        sUrl = manipulateUrl(sUrl, queryParams, pathVariables);
        HttpURLConnection conn = defaultConnection(method, new URL(sUrl), auth, authOptions);
        if (headers != null) {
            for (Map.Entry<String, String> header: headers.entrySet()) {
                conn.addRequestProperty(header.getKey(), header.getValue());
            }
        }
        return send(conn);
    }

    private static String manipulateUrl(String sUrl, Map<String, String> queryParams, String[] pathVariables) {
        sUrl = new QueryParamProcessor(queryParams).process(sUrl);
        sUrl = new PathVariableProcessor(pathVariables).process(sUrl);
        return sUrl;
    }

    private HttpURLConnection defaultConnection(Method method, URL url, Auth auth, AuthOptions authOptions) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method.name());
        if (auth != null) {
            auth.apply(conn, authOptions);
        }
        return conn;
    }

    private InputStream send(HttpURLConnection conn) throws IOException, ResponseCodeException {
        if (logger.isDebugEnabled()) {
            logger.debug("Sending URL [{}] with headers {{}}",
                    conn.getURL(),
                    getLoggableHeaders(conn));
        }
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            logErrorResponse(conn);
            throw new ResponseCodeException("Error with response from external API.  Response code: " + responseCode, responseCode);
        }
        logResponseHeaders(conn);
        String contentEncoding = conn.getHeaderField("Content-Encoding");
        InputStream inputStream = conn.getInputStream();
        if ("gzip".equalsIgnoreCase(contentEncoding)) {
            inputStream = new GZIPInputStream(inputStream);
        }
        return inputStream;
    }

    private void logErrorResponse(HttpURLConnection conn) throws IOException {
        try (InputStreamReader isReader = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(isReader)) {
            StringBuilder sb = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                sb.append(str);
            }
            logger.error("Error received from external API: {}", sb);
        }
    }

    private void logResponseHeaders(HttpURLConnection conn) {
        StringJoiner stringJoiner = new StringJoiner(",");
        Map<String, List<String>> responseHeaders = conn.getHeaderFields();
        for (Map.Entry<String, List<String>> header : responseHeaders.entrySet()) {
            String headerValue = header.getValue().stream()
                    .collect(Collectors.joining(","));
            stringJoiner.add(header.getKey() + "=" + headerValue);
        }
        logger.debug("Response headers: {{}}", stringJoiner);
    }

    private String getLoggableHeaders(HttpURLConnection conn) {
        StringJoiner stringJoiner = new StringJoiner(",");
        conn.getRequestProperties().entrySet().stream()
                .forEach(entry -> {
                    String name = entry.getKey();
                    String value = entry.getValue().stream().collect(Collectors.joining(","));
                    stringJoiner.add(name + "=" + value);
                });
        return stringJoiner.toString();
    }

    /**
     * Get the default headers specified by the builder.
     * Modifying this map will result in subsequent requests using the modified values
     * @return the default headers
     */
    public Map<String, String> getDefaultHeaders() {
        return defaultHeaders;
    }

    /**
     * Get the default query parameters specified by the builder.
     * Modifying this map will result in subsequent requests using the modified values
     * @return the default query parameters
     */
    public Map<String, String> getDefaultQueryParams() {
        return defaultQueryParams;
    }

    /**
     * Builder for HttpClient
     */
    public static class Builder {
        private Map<String, String> headers;
        private Map<String, String> queryParams;
        private Auth auth;
        private String username;
        private String password;
        private String[] pathVariables;

        /**
         * Build the HttpClient
         * @return the http client
         */
        public HttpClient build() {
            AuthOptions authOptions = new AuthOptions();
            authOptions.setUsername(username);
            authOptions.setPassword(password);
            return new HttpClient(headers, queryParams, auth, authOptions, pathVariables);
        }

        /**
         * Add a header to apply to each request
         * @param name name of the header
         * @param value value of the header
         * @return this builder
         */
        public Builder addHeader(String name, String value) {
            if (headers == null) {
                headers = new HashMap<>();
            }
            headers.put(name, value);
            return this;
        }

        /**
         * Add a query parameter to apply to each request
         * @param name name of the queryParameter
         * @param value value of the queryParameter
         * @return this builder
         */
        public Builder addQueryParameter(String name, String value) {
            if (queryParams == null) {
                queryParams = new HashMap<>();
            }
            queryParams.put(name, value);
            return this;
        }

        /**
         * Add authentication scheme to apply to each request
         * @param auth the authentication scheme
         * @return this builder
         */
        public Builder auth(Auth auth) {
            this.auth = auth;
            return this;
        }

        /**
         * Add a username for authentication
         * @param username the username to use
         * @return this builder
         */
        public Builder username(String username) {
            this.username = username;
            return this;
        }

        /**
         * Add a password for authentication
         * @param password the password to use
         * @return this builder
         */
        public Builder password(String password) {
            this.password = password;
            return this;
        }

        /**
         * Add path variables to be resolved in url
         * @param pathVariables any path variables to be resolved. Replace (in order)of first occurrence marked by regex \{([a-zA-Z0-9\-])+\})
         * @return this builder
         */
        public Builder pathVariables(String... pathVariables) {
            this.pathVariables = pathVariables;
            return this;
        }

    }
}
