package com.ziminpro.ums.controllers;

import com.ziminpro.ums.dao.UmsRepository;
import com.ziminpro.ums.dtos.Constants;
import com.ziminpro.ums.dtos.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Mono;

import java.util.*;

/**
 * Handles role assignment and partial user updates.
 *
 * <pre>
 *   POST  /users/user/{userId}/role/{roleId}  — assign a role to a user
 *   PATCH /users/user/{userId}                — partial field update (name / email / password)
 * </pre>
 *
 * Role assignment uses {@code INSERT IGNORE} at the SQL level, so calling it
 * when the role is already assigned is a safe no-op — the response still
 * returns 200 so callers do not need to pre-check.
 *
 * Patch semantics: only fields that are non-null in the request body are
 * applied; absent fields are left unchanged.  This avoids the "send the
 * full object just to change one field" overhead of a full PUT.
 */
@RestController
@RequestMapping("/users")
public class UserRolesController {

    @Autowired
    private UmsRepository umsRepository;

    // ----------------------------------------------------------------
    // POST /users/user/{userId}/role/{roleId}
    // ----------------------------------------------------------------

    /**
     * Assigns an existing role to an existing user.
     *
     * <ul>
     *   <li>404 if the user is not found.</li>
     *   <li>404 if the role UUID does not exist in the {@code roles} table.</li>
     *   <li>200 if the role was assigned — or was already assigned (idempotent).</li>
     * </ul>
     */
    @PostMapping("/user/{userId}/role/{roleId}")
    public Mono<ResponseEntity<Map<String, Object>>> assignRole(
            @PathVariable UUID userId,
            @PathVariable UUID roleId) {

        Map<String, Object> response = new HashMap<>();

        // Guard: user must exist
        User user = umsRepository.findUserByID(userId);
        if (user.getId() == null) {
            response.put(Constants.CODE,    "404");
            response.put(Constants.MESSAGE, "User " + userId + " not found");
            response.put(Constants.DATA,    false);
            return jsonOk(response);
        }

        // Guard: role must exist
        boolean roleExists = umsRepository.roleExists(roleId);
        if (!roleExists) {
            response.put(Constants.CODE,    "404");
            response.put(Constants.MESSAGE, "Role " + roleId + " not found");
            response.put(Constants.DATA,    false);
            return jsonOk(response);
        }

        // INSERT IGNORE — safe even if the pair already exists
        umsRepository.assignRole(userId, roleId);

        response.put(Constants.CODE,    "200");
        response.put(Constants.MESSAGE, "Role assigned to user " + userId);
        response.put(Constants.DATA,    true);
        return jsonOk(response);
    }

    // ----------------------------------------------------------------
    // PATCH /users/user/{userId}
    // ----------------------------------------------------------------

    /**
     * Partially updates a user.  Only fields present (non-null) in the
     * request body are written; all others remain unchanged.
     *
     * <p>Accepted body fields (all optional):</p>
     * <pre>
     * {
     *   "name":         "New Name",
     *   "email":        "new@email.com",
     *   "passwordHash": "hashed_value"
     * }
     * </pre>
     *
     * <ul>
     *   <li>400 if the body contains no updatable fields.</li>
     *   <li>404 if the user does not exist.</li>
     *   <li>200 with the refreshed user on success.</li>
     * </ul>
     */
    @PatchMapping(value = "/user/{userId}", consumes = Constants.APPLICATION_JSON)
    public Mono<ResponseEntity<Map<String, Object>>> patchUser(
            @PathVariable UUID userId,
            @RequestBody Map<String, String> fields) {

        Map<String, Object> response = new HashMap<>();

        // Guard: at least one patchable field must be present
        String newName     = fields.get("name");
        String newEmail    = fields.get("email");
        String newPassword = fields.get("passwordHash");

        if (newName == null && newEmail == null && newPassword == null) {
            response.put(Constants.CODE,    "400");
            response.put(Constants.MESSAGE, "Request body must contain at least one of: name, email, passwordHash");
            response.put(Constants.DATA,    false);
            return jsonOk(response);
        }

        // Load current state so we can fill in unchanged fields
        User existing = umsRepository.findUserByID(userId);
        if (existing.getId() == null) {
            response.put(Constants.CODE,    "404");
            response.put(Constants.MESSAGE, "User " + userId + " not found");
            response.put(Constants.DATA,    false);
            return jsonOk(response);
        }

        // Merge: apply only the fields that were sent
        existing.setName        (newName     != null ? newName     : existing.getName());
        existing.setEmail       (newEmail    != null ? newEmail    : existing.getEmail());
        existing.setPasswordHash(newPassword != null ? newPassword : existing.getPasswordHash());

        // Roles are not touched by PATCH — use PUT /users/user/{id} for full replacement
        int rows = umsRepository.updateUser(existing);

        if (rows != 1) {
            response.put(Constants.CODE,    "500");
            response.put(Constants.MESSAGE, "User could not be updated — check for duplicate email");
            response.put(Constants.DATA,    false);
        } else {
            // Return the refreshed user so the caller doesn't need a second GET
            User refreshed = umsRepository.findUserByID(userId);
            response.put(Constants.CODE,    "200");
            response.put(Constants.MESSAGE, "User " + userId + " patched successfully");
            response.put(Constants.DATA,    refreshed);
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