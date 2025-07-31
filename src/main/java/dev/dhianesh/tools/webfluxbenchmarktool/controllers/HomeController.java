package dev.dhianesh.tools.webfluxbenchmarktool.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController("")
public class HomeController {
    @RequestMapping("**")
    public Flux<Object> home() {
        return Flux.just("Hello World");
    }
}
