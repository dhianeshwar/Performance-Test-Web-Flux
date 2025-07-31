package dev.dhianesh.tools.webfluxbenchmarktool.dtos.requests;


import lombok.Data;

import java.util.Map;

@Data
public class LoadTestRequest {
    private String testUrl;
    private int forHowLong; // in seconds
    private int howManyHitsPerSecond;
    private String restApiMethod;
    private Map<String, String> requestParams;
    private Map<String, String> headers;
    private String requestBody;
}
