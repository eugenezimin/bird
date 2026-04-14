package com.ziminpro.twitter.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Roles {
    private UUID   roleId;
    private String role;
    private String description;

    public static final String ADMIN      = "ADMIN";
    public static final String PRODUCER   = "PRODUCER";
    public static final String SUBSCRIBER = "SUBSCRIBER";
}