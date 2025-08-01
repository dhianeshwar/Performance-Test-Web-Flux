package dev.dhianesh.tools.webfluxbenchmarktool.controllers;

import dev.dhianesh.tools.webfluxbenchmarktool.dtos.requests.LoadTestRequest;
import dev.dhianesh.tools.webfluxbenchmarktool.dtos.responses.StressTestResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController("/stress-test")
public class StressTestController {

    @PostMapping
    public Mono<StressTestResponse> performStressTest(@RequestBody LoadTestRequest loadTestRequest) {


    }
}
