package com.legyver.utils.httpclient.auth;

import com.legyver.utils.httpclient.internal.Utils;

import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

/**
 * Authentication mechanisms
 */
public enum Auth {
    /**
     * Basic Authentication
     */
    BASIC {
        @Override
        public void apply(HttpURLConnection conn, AuthOptions authOptions) {
            conn.setRequestProperty("Authorization",
                    "Basic " + Utils.encodeToString((authOptions.getUsername() + ":" + authOptions.getPassword()).getBytes(StandardCharsets.UTF_8)));
        }
    };

    /**
     * Apply authentication data to the connection
     * @param conn HttpURLConnection
     * @param authOptions Authentication data like username and password
     */
    public abstract void apply(HttpURLConnection conn, AuthOptions authOptions);
}
