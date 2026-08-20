package com.legyver.utils.httpclient.exception;

import com.legyver.core.exception.CoreException;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * An exception thrown when a response code other than 200 hundred is received
 */
public class ResponseCodeException extends CoreException {
    public static final String RETRY_AFTER = "Retry-After";
    private final int responseCode;
    private final Map<String, List<String>> responseHeaders;

    /**
     * Construct a response code exception
     * @param aMessage a message about the exception
     * @param responseCode the response code returned
     * @param responseHeaders any response headers
     */
    public ResponseCodeException(String aMessage, int responseCode, Map<String, List<String>> responseHeaders) {
        super(aMessage);
        this.responseCode = responseCode;
        this.responseHeaders = toCaseInsensitiveMap(responseHeaders);
    }

    /**
     * Construct a response code exception
     *
     * @param aMessage     a message about the exception
     * @param responseCode the response code returned
     */
    public ResponseCodeException(String aMessage, int responseCode) {
        this(aMessage, responseCode, null);
    }

    /**
     * Return the response code
     * @return the response code
     */
    public int getResponseCode() {
        return responseCode;
    }

    /**
     * Return the values of a specified response header
     * @param responseHeader the response header name
     * @return the response values associated with the header name
     */
    public List<String> getResponseHeader(String responseHeader) {
        return responseHeaders == null ? null : responseHeaders.get(responseHeader);
    }

    /**
     * Return the first values of a specified response header
     * @param responseHeader the response header name
     * @return the first response header value associated with the header name
     */
    public String getFirstResponseHeader(String responseHeader) {
        List<String> responseHeaderValue = getResponseHeader(responseHeader);
        return responseHeaderValue == null ? null : responseHeaderValue.iterator().next();
    }

    /**
     * Get the Retry-After value if the specified value is a delay
     * @return the specified delay in seconds.  Will turn null if there is no Retry-After specified or if the specified value is a Http-Date
     */
    public Long getRetryAfterDelaySeconds() {
        Long result = null;
        String retryAfter = getFirstResponseHeader(RETRY_AFTER);
        if (retryAfter != null && isNumeric(retryAfter)) {
            result = Long.parseLong(retryAfter);
        }
        return result;
    }

    /**
     * Get the Retry-After value if the specified value is a Http-Date
     * @return the specified date-time to retry.  Will return null if there is no Retry-After specified or if the specified value is a delay in seconds
     */
    public ZonedDateTime getRetryAfterHttpDate() {
        ZonedDateTime zonedDateTime = null;
        String retryAfter = getFirstResponseHeader(RETRY_AFTER);
        if (retryAfter != null && !isNumeric(retryAfter)) {
            DateTimeFormatter httpDateFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z");
            zonedDateTime = ZonedDateTime.parse(retryAfter, httpDateFormatter);
        }
        return zonedDateTime;
    }

    private static boolean isNumeric(String string) {
        return string != null && !string.isBlank() && string.matches("^\\d+$");
    }

    /**
     * HTTP header names are case-insensitive, so copy into a case-insensitive map to make lookups
     * robust to however the server (or an intermediary) cased them on the wire.
     * {@link java.net.HttpURLConnection#getHeaderFields()} also includes one entry keyed by
     * {@code null} representing the HTTP status line, which must be dropped since a case-insensitive
     * {@link TreeMap} rejects null keys.
     * @param source the raw response headers, or null
     * @return a case-insensitive copy of the response headers, or null if source was null
     */
    private static Map<String, List<String>> toCaseInsensitiveMap(Map<String, List<String>> source) {
        if (source == null) {
            return null;
        }
        Map<String, List<String>> caseInsensitive = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (Map.Entry<String, List<String>> entry : source.entrySet()) {
            if (entry.getKey() != null) {
                caseInsensitive.put(entry.getKey(), entry.getValue());
            }
        }
        return caseInsensitive;
    }
}
