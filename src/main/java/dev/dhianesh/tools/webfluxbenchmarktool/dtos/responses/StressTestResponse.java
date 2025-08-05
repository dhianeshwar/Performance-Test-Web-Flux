package dev.dhianesh.tools.webfluxbenchmarktool.dtos.responses;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StressTestResponse {

    private int processedRequests;
    private double averageResponseTime;
    private long totalDurationMinutes;


    private int totalFailure;
    private int readTimeError;
    private List<Integer> errorOccurredAt;

}
