package com.ziminpro.twitter.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Mirror of the UMS {@code User} DTO.
 * Used to deserialise the JSON envelope returned by UMSConnector
 * ({@code { "code": "200", "message": "...", "data": { ... } }}).
 *
 * <p><b>Note:</b> {@code passwordHash} is intentionally not used in
 * any business logic here — it is carried only so Jackson can round-trip
 * the full UMS response without throwing unknown-field errors.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private UUID          id;
    private String        name;
    private String        email;
    private String        passwordHash;
    private LocalDateTime createdAt;
    private List<Roles>   roles       = new ArrayList<>();
    private LastSession   lastSession;

    public void addRole(Roles role) {
        if (role != null && role.getRoleId() != null) {
            this.roles.add(role);
        }
    }

    public boolean hasRole(String roleName) {
        return roles.stream()
                    .anyMatch(r -> r.getRole().equalsIgnoreCase(roleName));
    }
}