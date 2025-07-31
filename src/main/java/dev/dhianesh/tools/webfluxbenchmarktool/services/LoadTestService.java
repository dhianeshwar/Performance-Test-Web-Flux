package dev.dhianesh.tools.webfluxbenchmarktool.services;

import dev.dhianesh.tools.webfluxbenchmarktool.dtos.requests.LoadTestRequest;
import dev.dhianesh.tools.webfluxbenchmarktool.dtos.responses.LoadTestResult;
import dev.dhianesh.tools.webfluxbenchmarktool.utils.HttpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoadTestService {

    private final RequestExecutor requestExecutor;

    public Mono<LoadTestResult> runLoadTest(LoadTestRequest request) {
        HttpMethod method = HttpUtils.fromString(request.getRestApiMethod());
        int totalRequests = request.getForHowLong() * request.getHowManyHitsPerSecond();
        Duration delayBetweenHits = Duration.ofMillis(1000 / request.getHowManyHitsPerSecond());

        return Flux.interval(Duration.ZERO, delayBetweenHits)
                .take(totalRequests)
                .flatMap(i -> requestExecutor.execute(
                        request.getTestUrl(),
                        method,
                        request.getHeaders(),
                        request.getRequestParams(),
                        request.getRequestBody()
                ))
                .filter(duration -> duration > 0)
                .collectList()
                .map(durations -> mapToResult(durations, request.getForHowLong()));
    }

    private LoadTestResult mapToResult(List<Long> durations, int forHowLongSeconds) {
        double totalDurationMs = durations.stream().mapToLong(Long::longValue).sum();
        int totalRequests = durations.size();
        double averageResponseTime = totalRequests > 0 ? totalDurationMs / totalRequests : 0.0;

        return LoadTestResult.builder()
                .totalRequests(totalRequests)
                .averageResponseTimeMillis(averageResponseTime)
                .totalDurationMinutes(forHowLongSeconds / 60.0)
                .build();
    }
}
