package dev.dhianesh.tools.webfluxbenchmarktool.services;

import dev.dhianesh.tools.webfluxbenchmarktool.dtos.requests.StressTestRequest;
import dev.dhianesh.tools.webfluxbenchmarktool.dtos.requests.WebClientRequest;
import dev.dhianesh.tools.webfluxbenchmarktool.dtos.responses.StressTestResponse;
import io.netty.handler.timeout.ReadTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
@Slf4j
public class NonBlockingService {

    private final WebClient webClient;

    public Mono<StressTestResponse> nonBlockingStressTest(StressTestRequest loadTestRequest) {
        log.info("NonBlockingStressTestController nonBlockingStressTest : {}", loadTestRequest);

        WebClientRequest clientRequest = loadTestRequest.getWebClientRequest();
        int totalRequest = loadTestRequest.getTotalRequests();
        int batchSize = loadTestRequest.getBatchSize();
        long startTime = System.currentTimeMillis();

        AtomicInteger totalProcessedRequests = new AtomicInteger(totalRequest);

        AtomicInteger totalFailedRequests = new AtomicInteger(0);
        AtomicInteger totalReadTimeOutExceptions = new AtomicInteger(0);

        Set<String> uniqueErrors = new HashSet<>();



        return Flux.range(1,totalRequest)
                .flatMap(i->
                        webClient.post()
                                .uri(clientRequest.getUrl())
                                .bodyValue(clientRequest.getRequestBody())
                                .retrieve()
                                .toBodilessEntity()
                                .map(resp ->System.currentTimeMillis())
                                .onErrorResume(ex -> {
                                    uniqueErrors.add(ex.getMessage());
                                    log.error("error while calling the api", ex);
                                    totalProcessedRequests.decrementAndGet();
                                    return Mono.just(System.currentTimeMillis());
                                } )
                ,Math.min(totalRequest,batchSize))
                .collectList()
                .map(timeStamps ->{
                    long endTime = System.currentTimeMillis();
                    long totalDurationInMinutes = endTime - startTime;

                    log.info("Unique errors : {}", uniqueErrors);

                    uniqueErrors.forEach(System.out::println);

                    return StressTestResponse.builder()
                            .totalDurationMinutes(TimeUnit.MILLISECONDS.toMinutes(totalDurationInMinutes))
                            .processedRequests(totalProcessedRequests.get())
                            .averageResponseTime((double) totalDurationInMinutes /totalProcessedRequests.get())
                            .totalFailure(totalFailedRequests.get())
                            .readTimeError(totalReadTimeOutExceptions.get())
                            .build();
                });


    }

}
