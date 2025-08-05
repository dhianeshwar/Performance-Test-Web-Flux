package dev.dhianesh.tools.webfluxbenchmarktool.controllers;

import dev.dhianesh.tools.webfluxbenchmarktool.dtos.requests.StressTestRequest;
import dev.dhianesh.tools.webfluxbenchmarktool.dtos.responses.StressTestResponse;
import dev.dhianesh.tools.webfluxbenchmarktool.services.NonBlockingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/non-blocking")
@RequiredArgsConstructor
public class NonBlockingStressTestController {

    private final NonBlockingService nonBlockingService;

    @PostMapping("/stress-test")
    public Mono<StressTestResponse> performStressTestNonBlockingWay(@RequestBody StressTestRequest loadTestRequest)
    {
        return nonBlockingService.nonBlockingStressTest(loadTestRequest);
    }
}
