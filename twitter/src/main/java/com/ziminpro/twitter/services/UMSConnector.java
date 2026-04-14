package com.ziminpro.twitter.services;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * Thin HTTP client for the UMS service.
 *
 * <p><b>Improvement over the original:</b> the {@link WebClient} instance is
 * constructed once in {@link #init()} rather than on every
 * {@link #retrieveUmsData(String)} call.  Building a {@code WebClient} is
 * relatively expensive (it allocates connection-pool resources); reusing a
 * shared instance is the Spring-recommended pattern.</p>
 */
@Service
public class UMSConnector {

    @Value("${ums.host}")
    private String umsHost;

    @Value("${ums.port}")
    private String umsPort;

    private WebClient client;

    @PostConstruct
    void init() {
        client = WebClient.builder()
            .baseUrl(umsHost + ":" + umsPort)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }

    /**
     * Performs a GET request to the UMS service and returns the raw response
     * body deserialized as a plain {@code Object} (a {@code LinkedHashMap} at
     * runtime), which is then unpacked by
     * {@link com.ziminpro.twitter.dtos.HttpResponseExtractor}.
     *
     * @param uri the path + query string to append to the UMS base URL
     * @return a {@link Mono} emitting the raw response body
     */
    public Mono<Object> retrieveUmsData(String uri) {
        return client.get()
            .uri(uri)
            .accept(MediaType.APPLICATION_JSON)
            .acceptCharset(StandardCharsets.UTF_8)
            .retrieve()
            .bodyToMono(Object.class);
    }
}