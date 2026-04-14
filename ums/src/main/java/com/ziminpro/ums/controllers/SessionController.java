package com.ziminpro.ums.controllers;

import com.ziminpro.ums.dao.UmsRepository;
import com.ziminpro.ums.dtos.Constants;
import com.ziminpro.ums.dtos.LastSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Exposes session lifecycle endpoints backed by the new `sessions` table.
 *
 * POST   /sessions/user/{userId}      — open a new session, returns sessionId
 * DELETE /sessions/{sessionId}        — close an existing session
 * GET    /sessions/user/{userId}/last — retrieve last session for a user
 */
@RestController
@RequestMapping("/sessions")
public class SessionController {

    @Autowired
    private UmsRepository umsRepository;

    @PostMapping("/user/{userId}")
    public Mono<ResponseEntity<Map<String, Object>>> openSession(
            @PathVariable UUID userId) {

        Map<String, Object> response = new HashMap<>();
        UUID sessionId = umsRepository.openSession(userId);

        if (sessionId == null) {
            response.put(Constants.CODE,    "500");
            response.put(Constants.MESSAGE, "Could not open session for user " + userId);
            response.put(Constants.DATA,    null);
        } else {
            response.put(Constants.CODE,    "201");
            response.put(Constants.MESSAGE, "Session opened");
            response.put(Constants.DATA,    sessionId.toString());
        }
        return jsonOk(response);
    }

    @DeleteMapping("/{sessionId}")
    public Mono<ResponseEntity<Map<String, Object>>> closeSession(
            @PathVariable UUID sessionId) {

        Map<String, Object> response = new HashMap<>();
        int rows = umsRepository.closeSession(sessionId);

        if (rows != 1) {
            response.put(Constants.CODE,    "404");
            response.put(Constants.MESSAGE, "Session not found or already closed");
            response.put(Constants.DATA,    false);
        } else {
            response.put(Constants.CODE,    "200");
            response.put(Constants.MESSAGE, "Session closed");
            response.put(Constants.DATA,    true);
        }
        return jsonOk(response);
    }

    @GetMapping("/user/{userId}/last")
    public Mono<ResponseEntity<Map<String, Object>>> getLastSession(
            @PathVariable UUID userId) {

        Map<String, Object> response = new HashMap<>();
        LastSession session = umsRepository.findLastSessionForUser(userId);

        if (session == null) {
            response.put(Constants.CODE,    "404");
            response.put(Constants.MESSAGE, "No session found for user " + userId);
            response.put(Constants.DATA,    null);
        } else {
            response.put(Constants.CODE,    "200");
            response.put(Constants.MESSAGE, "Last session retrieved");
            response.put(Constants.DATA,    session);
        }
        return jsonOk(response);
    }

    private static Mono<ResponseEntity<Map<String, Object>>> jsonOk(
            Map<String, Object> body) {
        return Mono.just(
            ResponseEntity.ok()
                .header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
                .header(Constants.ACCEPT,       Constants.APPLICATION_JSON)
                .body(body)
        );
    }
}