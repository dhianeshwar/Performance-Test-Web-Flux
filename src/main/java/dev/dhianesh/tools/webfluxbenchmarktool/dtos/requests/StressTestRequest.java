package dev.dhianesh.tools.webfluxbenchmarktool.dtos.requests;

import lombok.Data;

@Data
public class StressTestRequest {

    private int totalRequests;
    private boolean continueOnTimeout;

    private WebClientRequest webClientRequest;
}
