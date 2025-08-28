package dev.dhianesh.tools.webfluxbenchmarktool.utils;


import org.springframework.http.HttpMethod;

public class HttpUtils {
    public static HttpMethod fromString(String method) {
        return HttpMethod.valueOf(method.toUpperCase());
    }
}
