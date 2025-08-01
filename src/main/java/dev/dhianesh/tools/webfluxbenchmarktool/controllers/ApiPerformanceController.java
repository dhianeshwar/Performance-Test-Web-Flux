package dev.dhianesh.tools.webfluxbenchmarktool.controllers;

import dev.dhianesh.tools.webfluxbenchmarktool.dtos.requests.LoadTestRequest;
import dev.dhianesh.tools.webfluxbenchmarktool.dtos.responses.InstantLoadTestResult;
import dev.dhianesh.tools.webfluxbenchmarktool.dtos.responses.LoadTestResult;
import dev.dhianesh.tools.webfluxbenchmarktool.services.LoadTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/load-test")
@RequiredArgsConstructor
public class ApiPerformanceController {

    private final LoadTestService loadTestService;

    @PostMapping
    public Mono<LoadTestResult> startLoadTest(@RequestBody LoadTestRequest request) {
        return loadTestService.runLoadTest(request);

    }

//    @PostMapping("/instant-result")
//    public Mono<InstantLoadTestResult> performInstantLoadTest(@RequestBody LoadTestRequest request)
//    {
//        return loadTestService.runLoadTest(request);
//    }

}
