package org.cst8277.twitter.dtos;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Roles {
    private UUID roleId;
    private String role;
    private String description;

    public static final String ADMIN = "ADMIN";
    public static final String PRODUCER = "PRODUCER";
    public static final String SUBSCRIBER = "SUBSCRIBER";
}
