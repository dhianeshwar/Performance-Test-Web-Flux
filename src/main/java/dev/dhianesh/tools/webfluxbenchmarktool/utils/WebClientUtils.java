package dev.dhianesh.tools.webfluxbenchmarktool.utils;

import dev.dhianesh.tools.webfluxbenchmarktool.dtos.requests.WebClientRequest;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.experimental.UtilityClass;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.util.concurrent.TimeUnit;

import static dev.dhianesh.tools.webfluxbenchmarktool.utils.TimeUtils.secondsToMilliseconds;

@UtilityClass
public class WebClientUtils {

    public static WebClient getWebClient(WebClientRequest webClientRequest) {
        int  connectionTimeout = secondsToMilliseconds(webClientRequest.getConnectionTimeout());
        int  readTimeout = secondsToMilliseconds(webClientRequest.getReadTimeout());
        int  writeTimeout = secondsToMilliseconds(webClientRequest.getWriteTimeout());

        String url = webClientRequest.getUrl();
        ConnectionProvider provider = ConnectionProvider.builder("custom-pool")
                .maxIdleTime(java.time.Duration.ofSeconds(30))
                .build();

        HttpClient httpClient = HttpClient.create(provider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(writeTimeout, TimeUnit.MILLISECONDS))
                );
        WebClient build = WebClient.builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();


        return build;

    }

    public static WebClient createSharedWebClient(WebClientRequest request) {
        int connectionTimeout = request.getConnectionTimeout();
        int readTimeout = request.getReadTimeout();
        int writeTimeout = request.getWriteTimeout();
        String baseUrl = request.getUrl();

        ConnectionProvider provider = ConnectionProvider.builder("shared-pool")
                .maxConnections(100) // adjust based on expected concurrency
                .pendingAcquireMaxCount(500)
                .maxIdleTime(java.time.Duration.ofSeconds(60)) // keep-alive
                .build();

        HttpClient httpClient = HttpClient.create(provider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(readTimeout, TimeUnit.SECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(writeTimeout, TimeUnit.SECONDS))
                );

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
