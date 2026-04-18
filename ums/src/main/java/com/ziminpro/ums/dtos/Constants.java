package com.ziminpro.ums.dtos;

public class Constants {
    private Constants() {
        throw new IllegalStateException("Constants only class");
    }

    // HTTP Section
    public static final String CODE    = "code";
    public static final String MESSAGE = "message";
    public static final String DATA    = "data";

    // Headers
    public static final String APPLICATION_JSON = "application/json";
    public static final String CONTENT_TYPE     = "Content-Type";
    public static final String ACCEPT           = "Accept";

    // Table names — new schema
    private static final String TABLE_USERS       = "`users`";
    private static final String TABLE_ROLES       = "`roles`";
    private static final String TABLE_USERS_ROLES = "`users_roles`";
    private static final String TABLE_SESSIONS    = "`sessions`";

    // ----------------------------------------------------------------
    // User queries
    // ----------------------------------------------------------------
    public static final String GET_ALL_USERS =
        "SELECT " +
        "  u.id, u.name, u.email, u.password_hash, u.created_at, " +
        "  r.id AS role_id, r.name AS role_name, r.description AS role_desc " +
        "FROM " + TABLE_USERS + " u " +
        "LEFT JOIN " + TABLE_USERS_ROLES + " ur ON ur.user_id = u.id " +
        "LEFT JOIN " + TABLE_ROLES + " r ON r.id = ur.role_id " +
        "ORDER BY u.created_at";

    public static final String GET_USER_BY_ID =
        "SELECT " +
        "  u.id, u.name, u.email, u.password_hash, u.created_at, " +
        "  r.id AS role_id, r.name AS role_name, r.description AS role_desc " +
        "FROM " + TABLE_USERS + " u " +
        "LEFT JOIN " + TABLE_USERS_ROLES + " ur ON ur.user_id = u.id " +
        "LEFT JOIN " + TABLE_ROLES + " r ON r.id = ur.role_id " +
        "WHERE u.id = UUID_TO_BIN(?)";

    public static final String CREATE_USER =
        "INSERT INTO " + TABLE_USERS +
        " (id, name, email, password_hash, created_at) " +
        "VALUES (UUID_TO_BIN(?), ?, ?, ?, NOW())";

    public static final String UPDATE_USER =
        "UPDATE " + TABLE_USERS +
        " SET name = ?, email = ?, password_hash = ?, updated_at = NOW() " +
        "WHERE id = UUID_TO_BIN(?)";

    public static final String DELETE_USER =
        "DELETE FROM " + TABLE_USERS + " WHERE id = UUID_TO_BIN(?)";

    // ----------------------------------------------------------------
    // Role queries
    // ----------------------------------------------------------------
    public static final String GET_ALL_ROLES =
        "SELECT id, name, description FROM " + TABLE_ROLES;

    public static final String ASSIGN_ROLE =
        "INSERT IGNORE INTO " + TABLE_USERS_ROLES +
        " (user_id, role_id) VALUES (UUID_TO_BIN(?), UUID_TO_BIN(?))";

    public static final String REMOVE_USER_ROLES =
        "DELETE FROM " + TABLE_USERS_ROLES + " WHERE user_id = UUID_TO_BIN(?)";

    /** Check whether a role UUID exists — returns a count (0 or 1). */
    public static final String GET_ROLE_BY_ID =
        "SELECT COUNT(*) FROM `roles` WHERE id = UUID_TO_BIN(?)";

    /**
     * Assign a role to a user directly by both UUIDs.
     * INSERT IGNORE means a duplicate (userId, roleId) pair is a safe no-op.
     */
    public static final String ASSIGN_ROLE_BY_ID =
        "INSERT IGNORE INTO `users_roles` (user_id, role_id) " +
        "VALUES (UUID_TO_BIN(?), UUID_TO_BIN(?))";
    // ----------------------------------------------------------------
    // Session queries
    // ----------------------------------------------------------------
    public static final String GET_LAST_SESSION_FOR_USER =
        "SELECT id, user_id, logged_in_at, logged_out_at " +
        "FROM " + TABLE_SESSIONS +
        " WHERE user_id = UUID_TO_BIN(?) " +
        "ORDER BY logged_in_at DESC LIMIT 1";

    public static final String CREATE_SESSION =
        "INSERT INTO " + TABLE_SESSIONS +
        " (id, user_id, logged_in_at) VALUES (UUID_TO_BIN(?), UUID_TO_BIN(?), NOW())";

    public static final String CLOSE_SESSION =
        "UPDATE " + TABLE_SESSIONS +
        " SET logged_out_at = NOW() WHERE id = UUID_TO_BIN(?)";
}