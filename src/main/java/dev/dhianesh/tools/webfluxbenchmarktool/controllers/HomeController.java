package dev.dhianesh.tools.webfluxbenchmarktool.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController("")
@Slf4j
public class HomeController {
    @RequestMapping("**")
    public Flux<Object> home(ServerHttpRequest request) {
        log.info("Request URI: {}", request.getURI());
        return Flux.just("Hello World");
    }
}
