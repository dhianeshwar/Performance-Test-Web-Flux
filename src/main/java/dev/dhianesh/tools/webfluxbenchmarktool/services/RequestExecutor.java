package dev.dhianesh.tools.webfluxbenchmarktool.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestExecutor {

    private final WebClient webClient;

    public Mono<Long> execute(String url, HttpMethod method,
                              Map<String, String> headers,
                              Map<String, String> params,
                              String body) {
        // Append query parameters manually if any
        if (params != null && !params.isEmpty()) {
            StringBuilder sb = new StringBuilder(url);
            if (!url.contains("?")) sb.append("?");
            else if (!url.endsWith("&")) sb.append("&");
            params.forEach((key, value) -> sb.append(key).append("=").append(value).append("&"));
            url = sb.toString().replaceAll("&$", ""); // remove last '&'
        }

        WebClient.RequestBodySpec requestSpec = webClient.method(method).uri(URI.create(url));

        if (headers != null) {
            headers.forEach(requestSpec::header);
        }

        long startTime = System.currentTimeMillis();
        log.info("before hitting : {}", url);

        Mono<ClientResponse> responseMono = (body != null && method != HttpMethod.GET && method != HttpMethod.DELETE)
                ? requestSpec.body(BodyInserters.fromValue(body)).exchangeToMono(Mono::just)
                : requestSpec.exchangeToMono(Mono::just);

        return responseMono
                .flatMap(clientResponse -> clientResponse.bodyToMono(String.class)
                        .defaultIfEmpty("")
                        .doOnNext(responseBody -> {
                            log.info("⬅️ Status: {}", clientResponse.statusCode());
                            log.info("⬅️ Body: {}", responseBody);
                        })
                        .thenReturn(System.currentTimeMillis() - startTime)
                )
                .onErrorResume(e -> {
                    log.error("❌ Request failed: {}", e.getMessage(), e);
                    return Mono.just(-1L);
                });
    }

}
