package dev.dhianesh.tools.webfluxbenchmarktool.services;

import dev.dhianesh.tools.webfluxbenchmarktool.dtos.requests.StressTestRequest;
import dev.dhianesh.tools.webfluxbenchmarktool.dtos.responses.StressTestResponse;
import dev.dhianesh.tools.webfluxbenchmarktool.utils.WebClientUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@Slf4j
@AllArgsConstructor
public class StressTestService {

    private final Executor virtualThreadPoolExecutor;



    public StressTestResponse performStressTest(StressTestRequest request)
    {
        WebClient webClient = WebClientUtils.getWebClient(request.getWebClientRequest());

        List<CompletableFuture<Long>> futures = new ArrayList<>();
        for (int i = 0; i < request.getTotalRequests(); i++) {
            futures.add(this.hitTheUrl(request,webClient));
        }
       CompletableFuture<Void> allHits = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        return null;
    }

    private CompletableFuture<Long> hitTheUrl(StressTestRequest request, WebClient webClient) {
        return CompletableFuture.supplyAsync(() -> {
                    long startTime = System.currentTimeMillis();
                    webClient.get()
                            .uri(request.getWebClientRequest().getUrl())
                            .retrieve();

                    return System.currentTimeMillis() - startTime;
                }
                , virtualThreadPoolExecutor);

    }

}
