package com.ziminpro.ums.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents the most recent session record for a user.
 * Maps to the `sessions` table introduced in 01_ums_ddl.sql.
 * The old `last_visit` table (with epoch int columns) no longer exists.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LastSession {
    private UUID          sessionId;
    private LocalDateTime loggedInAt;
    private LocalDateTime loggedOutAt;   // null when session is still open
}