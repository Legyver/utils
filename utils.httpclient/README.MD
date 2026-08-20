# utils-httpclient
An HTTP client wrapper around Http(s)URLConnection

```groovy
implementation group: 'com.legyver', name: 'utils.httpclient', version: '3.7.0'
```

## Features
- Handles GZIP responses on HttpsURLConnection by detecting the returned Content-Encoding
- Supports GET requests (only, currently)
- Supports Basic Authentication (only, currently)
- Supports mixing map-based query params with query parameters already in URL
- Supports auto-retry for 429 and 503 responses, honoring the Retry-After response header when present
  and falling back to a default exponential backoff otherwise, with a configurable bounded retry count
  (default 3 retries)
- Robust (DEBUG) logging of inbound and outbound

Example GET request with Basic authentication
```Java
    HttpClient httpClient = HttpClient.builder()
        .addHeader("Accept", "application/json")
        .addHeader("Accept-Encoding", "gzip")
        .addQueryParameter("modified-after", "modified-after=2023-03-31T15:51:09.710Z")
        .auth(Auth.BASIC)
            .username("myuser")
            .password("mypass")
        .autoHandleRetryAfter()
        .build();
    InputStream is = httpClient.get("https://example.com");
```

The retry count (and, since 3.8.0, the backoff base) used for `autoHandleRetryAfter()` can be configured:
```Java
    HttpClient httpClient = HttpClient.builder()
        .autoHandleRetryAfter(5) // retry up to 5 additional times
        // or, also configure the base delay (in milliseconds) used for backoff when Retry-After is absent
        .autoHandleRetryAfter(5, 500)
        .build();
```

Since 3.5.0