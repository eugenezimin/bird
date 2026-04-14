package com.ziminpro.twitter.config;

import com.ziminpro.twitter.dtos.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;

import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.LinkedHashMap;

/**
 * Application-wide reactive exception handler.
 *
 * <p>Translates infrastructure exceptions into the standard
 * {@code { "code", "message", "data" }} response envelope so that
 * clients always receive well-formed JSON — never a raw stack trace.</p>
 *
 * <p>Covered cases:</p>
 * <ul>
 *   <li>{@link WebClientResponseException} — UMS returned a non-2xx status
 *       (e.g. user not found → 404, UMS auth failure → 401).</li>
 *   <li>{@link io.netty.channel.ConnectException} /
 *       {@link java.net.ConnectException} — UMS is unreachable.</li>
 *   <li>{@link ServerWebInputException} — malformed request body or path
 *       variable (e.g. invalid UUID string).</li>
 *   <li>{@link Exception} — catch-all for any other unexpected error.</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** UMS returned a non-2xx response (user not found, unauthorized, etc.). */
    @ExceptionHandler(WebClientResponseException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleWebClientResponse(
            WebClientResponseException ex) {

        Map<String, Object> body = envelope(
            String.valueOf(ex.getStatusCode().value()),
            "UMS service error: " + ex.getMessage()
        );
        return Mono.just(ResponseEntity
            .status(ex.getStatusCode())
            .header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
            .body(body));
    }

    /** UMS is unreachable (connection refused, timeout, DNS failure, etc.). */
    @ExceptionHandler(java.net.ConnectException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleConnectException(
            java.net.ConnectException ex) {

        Map<String, Object> body = envelope("503", "UMS service is unavailable: " + ex.getMessage());
        return Mono.just(ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
            .body(body));
    }

    /** Malformed request — bad UUID in path, unparseable JSON body, etc. */
    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleBadInput(
            ServerWebInputException ex) {

        Map<String, Object> body = envelope("400", "Bad request: " + ex.getReason());
        return Mono.just(ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
            .body(body));
    }

    /** Propagated ResponseStatusException (e.g. from Spring internals). */
    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleResponseStatus(
            ResponseStatusException ex) {

        Map<String, Object> body = envelope(
            String.valueOf(ex.getStatusCode().value()),
            ex.getReason() != null ? ex.getReason() : ex.getMessage()
        );
        return Mono.just(ResponseEntity
            .status(ex.getStatusCode())
            .header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
            .body(body));
    }

    /** Catch-all for anything not matched above. */
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleGeneral(Exception ex) {
        Map<String, Object> body = envelope("500", "Internal server error: " + ex.getMessage());
        return Mono.just(ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
            .body(body));
    }

    // ----------------------------------------------------------------
    // Private helpers
    // ----------------------------------------------------------------

    private static Map<String, Object> envelope(String code, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(Constants.CODE,    code);
        body.put(Constants.MESSAGE, message);
        body.put(Constants.DATA,    null);
        return body;
    }
}