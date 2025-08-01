package dev.dhianesh.tools.webfluxbenchmarktool.dtos.responses;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StressTestResponse {

    private int processedRequests;
    private double averageResponseTime;
    private double totalDurationMinutes;
    private List<Integer> errorOccurredAt;

}
