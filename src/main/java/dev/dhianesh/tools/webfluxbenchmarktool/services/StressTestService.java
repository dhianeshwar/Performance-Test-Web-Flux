package dev.dhianesh.tools.webfluxbenchmarktool.services;

import dev.dhianesh.tools.webfluxbenchmarktool.dtos.requests.StressTestRequest;
import dev.dhianesh.tools.webfluxbenchmarktool.dtos.responses.StressTestResponse;
import dev.dhianesh.tools.webfluxbenchmarktool.utils.TimeUtils;
import dev.dhianesh.tools.webfluxbenchmarktool.utils.WebClientUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service
@Slf4j
@AllArgsConstructor
public class StressTestService {

//    private final Executor virtualThreadPoolExecutor;
    private final Executor vtExceutor = Executors.newVirtualThreadPerTaskExecutor();
    private final WebClient webClient;


    public StressTestResponse performStressTest(StressTestRequest request)
    {
        WebClient webClient = WebClientUtils.getWebClient(request.getWebClientRequest());
        log.info("StressTestController performStressTest : {}", webClient);

        long startTime = System.currentTimeMillis();
        log.info("start time : {}", startTime);

        List<CompletableFuture<Long>> futures = new ArrayList<>();
        for (int i = 0; i < request.getTotalRequests(); i++) {
            System.out.println("i ->"+i);
            futures.add(this.hitTheUrl(request,webClient));
        }
       CompletableFuture<Void> allHits = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
       allHits.join();

       long endTime = System.currentTimeMillis();
       log.info("end time : {}", endTime);

        long totalDurationMinutes = TimeUtils.toWholeMinutes(endTime - startTime);
        log.info("total duration minutes : {}", totalDurationMinutes);
        return StressTestResponse.builder()
                .totalDurationMinutes(totalDurationMinutes)
                .build();
    }

    private CompletableFuture<Long> hitTheUrl(StressTestRequest request, WebClient dummy) {
        try {

            return CompletableFuture.supplyAsync(() -> {
                        long startTime = System.currentTimeMillis();

                       Mono<ResponseEntity<Void>> responseEntityMono = webClient.post()
                                .uri(request.getWebClientRequest().getUrl())
                                .bodyValue(request.getWebClientRequest().getRequestBody())
                                .retrieve()
                                .toBodilessEntity()
                               .onErrorResume(e->
                               {
                                   log.info("error : {}",e.getMessage(),e);
                                   return Mono.empty();
                               });
//
                       responseEntityMono.block();


                        long endTime = System.currentTimeMillis();
                        return endTime - startTime;
                    }
                    , vtExceutor);
        }catch (Exception ex){
            log.error(ex.getMessage(),ex);
            return CompletableFuture.completedFuture(0L);
        }

    }



}
