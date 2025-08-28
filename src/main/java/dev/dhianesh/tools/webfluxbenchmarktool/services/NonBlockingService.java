package dev.dhianesh.tools.webfluxbenchmarktool.services;

import dev.dhianesh.tools.webfluxbenchmarktool.dtos.requests.StressTestRequest;
import dev.dhianesh.tools.webfluxbenchmarktool.dtos.requests.WebClientRequest;
import dev.dhianesh.tools.webfluxbenchmarktool.dtos.responses.StressTestResponse;
import dev.dhianesh.tools.webfluxbenchmarktool.utils.MetricsAccumulator;
import io.netty.handler.timeout.ReadTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class NonBlockingService {
    public record RequestResult(
            boolean success,
            long durationMillis,
            String errorType, // e.g., "READ_TIMEOUT", "HTTP_500", "OTHER"
            String errorMessage
    ) {}

    private final WebClient webClient;

    public Mono<StressTestResponse> nonBlockingStressTest(StressTestRequest loadTestRequest) {
        WebClientRequest clientRequest = loadTestRequest.getWebClientRequest();
        int totalRequest = loadTestRequest.getTotalRequests();
        int batchSize = loadTestRequest.getBatchSize();
        long wallStart = System.currentTimeMillis();

        int concurrency = Math.min(totalRequest, batchSize);

        return Flux.range(1, totalRequest)
                .flatMap(i -> executeSingleRequest(clientRequest), concurrency)
                .collect(MetricsAccumulator::new, MetricsAccumulator::accumulate)
                .map(acc -> {
                    long wallEnd = System.currentTimeMillis();
                    long totalDurationMillis = wallEnd - wallStart;

                    double avgResponseTime = acc.getTotalRequests() == 0
                            ? 0.0
                            : ((double) acc.getSumLatencyMillis() / acc.getTotalRequests());

                    log.info("Error breakdown: {}", acc.getErrorTypeCounts());

                    return StressTestResponse.builder()
                            .totalDurationMinutes(TimeUnit.MILLISECONDS.toSeconds(totalDurationMillis))
                            .processedRequests(acc.getSuccessCount())
                            .averageResponseTime(TimeUnit.MILLISECONDS.toSeconds((long) avgResponseTime))
                            .totalFailure(acc.getFailureCount())
                            .readTimeError(acc.getReadTimeoutCount())
                            .build();
                });
    }

    private Mono<RequestResult> executeSingleRequest(WebClientRequest clientRequest) {
        long requestStartNano = System.nanoTime();

        String url = clientRequest.getUrl();
        WebClient.RequestBodySpec spec;

        if (url.startsWith("http://") || url.startsWith("https://")) {
            // Absolute URL → use directly
            spec = webClient.method(resolveHttpMethod(clientRequest.getMethod()))
                    .uri(url);
        } else {
            // Relative URL → use builder
            spec = webClient.method(resolveHttpMethod(clientRequest.getMethod()))
                    .uri(uriBuilder -> {
                        uriBuilder.path(url);
                        if (clientRequest.getRequestParams() != null) {
                            clientRequest.getRequestParams().forEach(uriBuilder::queryParam);
                        }
                        return uriBuilder.build();
                    });
        }
        if (clientRequest.getHeaders() != null && !clientRequest.getHeaders().isEmpty()) {
            spec.headers(h -> {
                clientRequest.getHeaders().forEach(h::add);
            });
        }

        // Body handling (null-safe)
        if (clientRequest.getRequestBody() != null) {
            spec.bodyValue(clientRequest.getRequestBody());
        }

        Mono<ResponseEntity<Void>> responseMono = spec
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp ->
                        resp.bodyToMono(String.class)
                                .defaultIfEmpty("<empty>")
                                .flatMap(body -> {
                                    String errType = "HTTP_" + resp.statusCode().value();
                                    String msg = "Status=" + resp.statusCode() + " body=" + body;
                                    return Mono.just(new RuntimeException(errType + ": " + msg));
                                })
                )
                .toBodilessEntity();

        Mono<Void> requestMono = responseMono.then(); // This ensures we only care about success/failure


        // Apply read timeout if provided (millis)
        if (clientRequest.getReadTimeout() > 0) {
            requestMono = requestMono.timeout(Duration.ofMillis(clientRequest.getReadTimeout()));
        }

        return requestMono
                .then(Mono.fromCallable(() -> buildSuccessResult(requestStartNano)))
                .onErrorResume(ex -> {
                    long durationMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - requestStartNano);
                    Throwable unwrapped = Exceptions.unwrap(ex);
                    String errorType = classifyError(unwrapped);
                    String errorMessage = ex.getMessage();
                    return Mono.just(new RequestResult(false, durationMillis, errorType, errorMessage));
                });
    }

    private RequestResult buildSuccessResult(long requestStartNano) {
        long durationMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - requestStartNano);
        return new RequestResult(true, durationMillis, null, null);
    }

    private HttpMethod resolveHttpMethod(String method) {
        if (!StringUtils.hasText(method)) {
            return HttpMethod.POST; // default
        }
        try {
            return HttpMethod.valueOf(method.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Unsupported HTTP method '{}', defaulting to POST", method);
            return HttpMethod.POST;
        }
    }

    private String classifyError(Throwable t) {
        if (containsInCauseChain(t, ReadTimeoutException.class)) {
            return "READ_TIMEOUT";
        }
        if (containsInCauseChain(t, java.util.concurrent.TimeoutException.class)) {
            return "TIMEOUT";
        }
        if (containsInCauseChain(t, WebClientResponseException.class)) {
            return "HTTP_ERROR";
        }
        return "OTHER";
    }

    private boolean containsInCauseChain(Throwable t, Class<? extends Throwable> target) {
        while (t != null) {
            if (target.isInstance(t)) {
                return true;
            }
            t = t.getCause();
        }
        return false;
    }

}
