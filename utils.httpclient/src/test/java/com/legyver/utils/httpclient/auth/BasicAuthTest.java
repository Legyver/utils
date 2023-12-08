package com.legyver.utils.httpclient.auth;

import com.legyver.utils.httpclient.internal.Utils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class BasicAuthTest {

    @Test
    public void authApplied() throws Exception {
        URL url = new URL("http://test.example.com");
        TestConnection testConnection = new TestConnection(url);
        AuthOptions authOptions = new AuthOptions();
        authOptions.setUsername("username");
        authOptions.setPassword("password");
        Auth.BASIC.apply(testConnection, authOptions);
        String expectedValue = "Basic " + Utils.encodeToString((authOptions.getUsername() + ":" + authOptions.getPassword()).getBytes(StandardCharsets.UTF_8));
        assertThat(testConnection.requestProperties).containsKey("Authorization");
        assertThat(testConnection.requestProperties.get("Authorization")).isEqualTo(expectedValue);
    }

    private class TestConnection extends HttpURLConnection {
        private final Map<String, String> requestProperties = new HashMap<>();
        @Override
        public void setRequestProperty(String key, String value) {
            requestProperties.put(key, value);
        }

        /**
         * Constructor for the HttpURLConnection.
         *
         * @param u the URL
         */
        protected TestConnection(URL u) {
            super(u);
        }

        @Override
        public void disconnect() {

        }

        @Override
        public boolean usingProxy() {
            return false;
        }

        @Override
        public void connect() throws IOException {

        }
    }




}
