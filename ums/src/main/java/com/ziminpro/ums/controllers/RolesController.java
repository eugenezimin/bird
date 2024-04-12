package com.ziminpro.ums.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.ziminpro.ums.dao.UmsRepository;
import com.ziminpro.ums.dtos.Constants;
import com.ziminpro.ums.dtos.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class RolesController {

    @Autowired
    private UmsRepository umsRepository;

    Map<String, Object> response = new HashMap<>();

    @RequestMapping(method = RequestMethod.GET, path = "/roles")
    public Mono<ResponseEntity<Map<String, Object>>> getAllRoles() {
        Map<String, Roles> roles = umsRepository.findAllRoles();
        if (roles == null) {
            response.put(Constants.CODE, "500");
            response.put(Constants.MESSAGE, "Roles have not been retrieved");
            response.put(Constants.DATA, new HashMap<>());
        } else {
            response.put(Constants.CODE, "200");
            response.put(Constants.MESSAGE, "List of Roles has been requested successfully");
            response.put(Constants.DATA, new ArrayList<>(roles.values()));
        }
        return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
                .header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
    }
}
