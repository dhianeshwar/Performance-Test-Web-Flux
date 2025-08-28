package dev.dhianesh.tools.webfluxbenchmarktool.controllers;

import dev.dhianesh.tools.webfluxbenchmarktool.dtos.requests.StressTestRequest;
import dev.dhianesh.tools.webfluxbenchmarktool.dtos.responses.StressTestResponse;
import dev.dhianesh.tools.webfluxbenchmarktool.services.StressTestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/stress-test")
@RequiredArgsConstructor
@Slf4j
public class StressTestController {

    private final StressTestService stressTestService;

    @PostMapping
    public Mono<StressTestResponse> performStressTest(@RequestBody StressTestRequest loadTestRequest) {
        log.info("StressTestController performStressTest : {}", loadTestRequest);
        StressTestResponse stressTestResponse = stressTestService.performStressTest(loadTestRequest);
//        Mono<StressTestResponse> result = Mono.just(stressTestResponse);
//        result.block();

        return Mono.just(stressTestResponse);

    }
}
