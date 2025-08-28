package dev.dhianesh.tools.webfluxbenchmarktool.dtos.requests;

import lombok.Data;

import java.util.Map;

@Data
public class WebClientRequest {

    private int connectionTimeout;
    private int readTimeout;
    private int writeTimeout;

    private String url;
    private String method;
    private Object requestBody;
    private Map<String, String> headers;
    private Map<String, String> requestParams;


}
