package com.ziminpro.twitter.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents the most recent session for a user as returned by the UMS service.
 * Maps to the {@code sessions} table in the UMS database.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LastSession {
    private UUID          sessionId;
    private LocalDateTime loggedInAt;
    private LocalDateTime loggedOutAt;   // null when session is still open
}