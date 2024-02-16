# utils-httpclient
An HTTP client wrapper around Http(s)URLConnection

```groovy
implementation group: 'com.legyver', name: 'utils.httpclient', version: '3.6.1'
```

## Features
- Handles GZIP responses on HttpsURLConnection by detecting the returned Content-Encoding
- Supports GET requests (only, currently)
- Supports Basic Authentication (only, currently)
- Supports mixing map-based query params with query parameters already in URL
- Robust (DEBUG) logging of inbound and outbound headers

Example GET request with Basic authentication
```Java
    HttpClient httpClient = HttpClient.builder()
        .addHeader("Accept", "application/json")
        .addHeader("Accept-Encoding", "gzip")
        .addQueryParameter("modified-after", "modified-after=2023-03-31T15:51:09.710Z")
        .auth(Auth.BASIC)
            .username("myuser")
            .password("mypass")
        .build();
    InputStream is = httpClient.get("https://example.com");
```

Since 3.5.0