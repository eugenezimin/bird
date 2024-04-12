package com.ziminpro.ums.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.ziminpro.ums.dao.UmsRepository;
import com.ziminpro.ums.dtos.Constants;
import com.ziminpro.ums.dtos.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class UserController {

    @Autowired
    private UmsRepository umsRepository;

    Map<String, Object> response = new HashMap<>();

    @RequestMapping(method = RequestMethod.GET, path = "/users")
    public Mono<ResponseEntity<Map<String, Object>>> getAllUsers() {
        Map<UUID, User> users = umsRepository.findAllUsers();
        if (users == null) {
            response.put(Constants.CODE, "500");
            response.put(Constants.MESSAGE, "Users have not been retrieved");
            response.put(Constants.DATA, new HashMap<>());
        } else {
            response.put(Constants.CODE, "200");
            response.put(Constants.MESSAGE, "List of Users has been requested successfully");
            response.put(Constants.DATA, new ArrayList<>(users.values()));
        }
        return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
                .header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/users/user/{user-id}")
    public Mono<ResponseEntity<Map<String, Object>>> getUser(@PathVariable(value = "user-id", required = true) String userId) {
        User user = umsRepository.findUserByID(UUID.fromString(userId));
        if (user.getId() == null) {
            response.put(Constants.CODE, "404");
            response.put(Constants.MESSAGE, "User have not been found");
            response.put(Constants.DATA, new User());
        } else {
            response.put(Constants.CODE, "200");
            response.put(Constants.MESSAGE, "User has been retrieved successfully");
            response.put(Constants.DATA, user);
        }
        return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
                .header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
    }

    @RequestMapping(method = RequestMethod.POST, path = "/users/user", consumes = Constants.APPLICATION_JSON)
    public Mono<ResponseEntity<Map<String, Object>>> createUser(@RequestBody User user) {
        UUID userId = umsRepository.createUser(user);
        if (userId == null) {
            response.put(Constants.CODE, "500");
            response.put(Constants.MESSAGE, "User has not been created");
            response.put(Constants.DATA, "Check email for duplicates first");
        } else {
            response.put(Constants.CODE, "201");
            response.put(Constants.MESSAGE, "User created");
            response.put(Constants.DATA, userId.toString());
        }
        return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
                .header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/users/user/{user-id}")
    public Mono<ResponseEntity<Map<String, Object>>> deleteUser(@PathVariable(value = "user-id", required = true) String userId) {
        int result = umsRepository.deleteUser(UUID.fromString(userId));
        if (result != 1) {
            response.put(Constants.CODE, "500");
            response.put(Constants.MESSAGE, "Error happened while deleting user");
            response.put(Constants.DATA, userId);
        } else {
            response.put(Constants.CODE, "200");
            response.put(Constants.MESSAGE, "User deleted");
            response.put(Constants.DATA, userId.toString());
        }
        return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
                .header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
    }
}