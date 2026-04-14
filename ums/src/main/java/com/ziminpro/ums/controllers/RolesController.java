package com.ziminpro.ums.controllers;

import com.ziminpro.ums.dao.UmsRepository;
import com.ziminpro.ums.dtos.Constants;
import com.ziminpro.ums.dtos.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

import java.util.*;

@RestController
public class RolesController {

    @Autowired
    private UmsRepository umsRepository;

    @GetMapping("/roles")
    public Mono<ResponseEntity<Map<String, Object>>> getAllRoles() {
        // Local variable — no shared mutable state
        Map<String, Object> response = new HashMap<>();
        Map<String, Roles> roles = umsRepository.findAllRoles();

        if (roles == null || roles.isEmpty()) {
            response.put(Constants.CODE,    "500");
            response.put(Constants.MESSAGE, "Roles have not been retrieved");
            response.put(Constants.DATA,    Collections.emptyList());
        } else {
            response.put(Constants.CODE,    "200");
            response.put(Constants.MESSAGE, "List of roles retrieved successfully");
            response.put(Constants.DATA,    new ArrayList<>(roles.values()));
        }

        return Mono.just(
            ResponseEntity.ok()
                .header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
                .header(Constants.ACCEPT,       Constants.APPLICATION_JSON)
                .body(response)
        );
    }
}