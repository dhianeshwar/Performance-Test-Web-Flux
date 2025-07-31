package dev.dhianesh.tools.webfluxbenchmarktool.dtos.responses;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoadTestResult {
    private int totalRequests;
    private double averageResponseTimeMillis;
    private double totalDurationMinutes;
}
