package com.ziminpro.twitter.services;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class UMSConnector {

    @Value("${ums.host}")
    private String uriUmsHost;

    @Value("${ums.port}")
    private String uriUmsPort;

    public Mono<Object> retrieveUmsData(String uri) {
        WebClient client = WebClient.builder().baseUrl(uriUmsHost + ":" + uriUmsPort)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();

	    return client.method(HttpMethod.GET).uri(uri).accept(MediaType.APPLICATION_JSON)
	            .acceptCharset(StandardCharsets.UTF_8).retrieve().bodyToMono(Object.class);
    }
}
