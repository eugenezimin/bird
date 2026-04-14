package com.ziminpro.ums.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private UUID          id;
    private String        name;
    private String        email;
    /** Stored as a bcrypt hash — never expose this in API responses. */
    private String        passwordHash;
    private LocalDateTime createdAt;
    private List<Roles>   roles       = new ArrayList<>();
    private LastSession   lastSession;

    public void addRole(Roles role) {
        if (role != null && role.getRoleId() != null) {
            this.roles.add(role);
        }
    }

    /** Convenience check used by the twitter service via HTTP. */
    public boolean hasRole(String roleName) {
        return roles.stream()
                    .anyMatch(r -> r.getRole().equalsIgnoreCase(roleName));
    }
}