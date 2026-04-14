package com.ziminpro.ums.controllers;

import com.ziminpro.ums.dao.UmsRepository;
import com.ziminpro.ums.dtos.Constants;
import com.ziminpro.ums.dtos.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Mono;

import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UmsRepository umsRepository;

    // NOTE: response map is now a LOCAL variable in every method, not a
    // shared field.  The original shared field caused a race condition:
    // concurrent requests would overwrite each other's data because Spring
    // beans are singletons.

    @GetMapping
    public Mono<ResponseEntity<Map<String, Object>>> getAllUsers() {
        Map<String, Object> response = new HashMap<>();
        Map<UUID, User> users = umsRepository.findAllUsers();

        if (users == null) {
            response.put(Constants.CODE,    "500");
            response.put(Constants.MESSAGE, "Users have not been retrieved");
            response.put(Constants.DATA,    Collections.emptyList());
        } else {
            response.put(Constants.CODE,    "200");
            response.put(Constants.MESSAGE, "List of users retrieved successfully");
            response.put(Constants.DATA,    new ArrayList<>(users.values()));
        }
        return jsonOk(response);
    }

    @GetMapping("/user/{userId}")
    public Mono<ResponseEntity<Map<String, Object>>> getUser(@PathVariable UUID userId) {

        Map<String, Object> response = new HashMap<>();
        User user = umsRepository.findUserByID(userId);

        if (user.getId() == null) {
            response.put(Constants.CODE,    "404");
            response.put(Constants.MESSAGE, "User not found");
            response.put(Constants.DATA,    new User());
        } else {
            response.put(Constants.CODE,    "200");
            response.put(Constants.MESSAGE, "User retrieved successfully");
            response.put(Constants.DATA,    user);
        }
        return jsonOk(response);
    }

    @PostMapping(value = "/user", consumes = Constants.APPLICATION_JSON)
    public Mono<ResponseEntity<Map<String, Object>>> createUser(@RequestBody User user) {

        Map<String, Object> response = new HashMap<>();
        UUID userId = umsRepository.createUser(user);

        if (userId == null) {
            response.put(Constants.CODE,    "500");
            response.put(Constants.MESSAGE, "User has not been created — check for duplicate e-mail");
            response.put(Constants.DATA,    null);
        } else {
            response.put(Constants.CODE,    "201");
            response.put(Constants.MESSAGE, "User created");
            response.put(Constants.DATA,    userId.toString());
        }
        return jsonOk(response);
    }

    @PutMapping(value = "/user/{userId}", consumes = Constants.APPLICATION_JSON)
    public Mono<ResponseEntity<Map<String, Object>>> updateUser(
            @PathVariable UUID userId,
            @RequestBody User user) {

        Map<String, Object> response = new HashMap<>();
        user.setId(userId);
        int rows = umsRepository.updateUser(user);

        if (rows != 1) {
            response.put(Constants.CODE,    "404");
            response.put(Constants.MESSAGE, "User not found or not updated");
            response.put(Constants.DATA,    false);
        } else {
            response.put(Constants.CODE,    "200");
            response.put(Constants.MESSAGE, "User updated");
            response.put(Constants.DATA,    true);
        }
        return jsonOk(response);
    }

    @DeleteMapping("/user/{userId}")
    public Mono<ResponseEntity<Map<String, Object>>> deleteUser(
            @PathVariable UUID userId) {

        Map<String, Object> response = new HashMap<>();
        int result = umsRepository.deleteUser(userId);

        if (result != 1) {
            response.put(Constants.CODE,    "404");
            response.put(Constants.MESSAGE, "User not found or already deleted");
            response.put(Constants.DATA,    false);
        } else {
            response.put(Constants.CODE,    "200");
            response.put(Constants.MESSAGE, "User deleted");
            response.put(Constants.DATA,    true);
        }
        return jsonOk(response);
    }

    // ----------------------------------------------------------------
    // Private helpers
    // ----------------------------------------------------------------

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