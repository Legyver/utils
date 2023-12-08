package com.legyver.utils.httpclient.internal;

import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Utils {
    public static String encodeToString(byte[] bytes) {
        return new String(Base64.getEncoder().encode(bytes), UTF_8);
    }

}
