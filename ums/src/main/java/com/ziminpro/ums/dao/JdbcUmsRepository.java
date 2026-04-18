package com.ziminpro.ums.dao;

import com.ziminpro.ums.dtos.Constants;
import com.ziminpro.ums.dtos.LastSession;
import com.ziminpro.ums.dtos.Roles;
import com.ziminpro.ums.dtos.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class JdbcUmsRepository implements UmsRepository {

    @Autowired
    private JdbcTemplate jdbc;

    // ----------------------------------------------------------------
    // Row mappers
    // ----------------------------------------------------------------

    /**
     * Maps a single JOIN row (user + one role) to a User object.
     * Callers are responsible for merging multi-role rows.
     */
    private static final RowMapper<User> USER_ROW_MAPPER = (rs, rowNum) -> {
        User user = new User();
        user.setId(DaoHelper.bytesArrayToUuid(rs.getBytes("u.id")));
        user.setName(rs.getString("u.name"));
        user.setEmail(rs.getString("u.email"));
        user.setPasswordHash(rs.getString("u.password_hash"));
        user.setCreatedAt(rs.getObject("u.created_at", java.time.LocalDateTime.class));

        // Role column may be NULL when user has no roles (LEFT JOIN)
        byte[] roleIdBytes = rs.getBytes("role_id");
        if (roleIdBytes != null) {
            Roles role = new Roles(
                    DaoHelper.bytesArrayToUuid(roleIdBytes),
                    rs.getString("role_name"),
                    rs.getString("role_desc"));
            user.addRole(role);
        }
        return user;
    };

    private static final RowMapper<Roles> ROLE_ROW_MAPPER = (rs, rowNum) -> new Roles(
            DaoHelper.bytesArrayToUuid(rs.getBytes("id")),
            rs.getString("name"),
            rs.getString("description"));

    private static final RowMapper<LastSession> SESSION_ROW_MAPPER = (rs, rowNum) -> new LastSession(
            DaoHelper.bytesArrayToUuid(rs.getBytes("id")),
            rs.getObject("logged_in_at", java.time.LocalDateTime.class),
            rs.getObject("logged_out_at", java.time.LocalDateTime.class));

    // ----------------------------------------------------------------
    // Users
    // ----------------------------------------------------------------

    @Override
    public Map<UUID, User> findAllUsers() {
        // Query returns one row per user-role combination; we merge here.
        List<User> rows = jdbc.query(Constants.GET_ALL_USERS, USER_ROW_MAPPER);
        return mergeUserRows(rows);
    }

    @Override
    public User findUserByID(UUID userId) {
        List<User> rows = jdbc.query(Constants.GET_USER_BY_ID,
                USER_ROW_MAPPER, userId.toString());

        if (rows.isEmpty()) {
            return new User(); // empty sentinel — id will be null
        }

        // Merge multi-role rows into one User
        User merged = new User();
        for (User row : rows) {
            if (merged.getId() == null) {
                merged.setId(row.getId());
                merged.setName(row.getName());
                merged.setEmail(row.getEmail());
                merged.setPasswordHash(row.getPasswordHash());
                merged.setCreatedAt(row.getCreatedAt());
            }
            row.getRoles().forEach(merged::addRole);
        }

        // Attach the last session (best-effort — null is acceptable)
        merged.setLastSession(findLastSessionForUser(userId));
        return merged;
    }

    @Override
    public UUID createUser(User user) {
        UUID userId = UUID.randomUUID();
        Map<String, Roles> roleMap = findAllRoles();

        try {
            jdbc.update(Constants.CREATE_USER,
                    userId.toString(),
                    user.getName(),
                    user.getEmail(),
                    user.getPasswordHash());

            for (Roles role : user.getRoles()) {
                Roles canonical = roleMap.get(role.getRole().toUpperCase());
                if (canonical == null) {
                    // Unknown role name — skip rather than crash
                    continue;
                }
                jdbc.update(Constants.ASSIGN_ROLE,
                        userId.toString(),
                        canonical.getRoleId().toString());
            }
        } catch (Exception e) {
            // Likely a duplicate e-mail — caller gets null as "failed" signal
            return null;
        }
        return userId;
    }

    @Override
    public int updateUser(User user) {
        int rows = jdbc.update(Constants.UPDATE_USER,
                user.getName(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getId().toString());

        // Re-sync roles: remove all then re-assign
        if (rows == 1 && user.getRoles() != null) {
            Map<String, Roles> roleMap = findAllRoles();
            jdbc.update(Constants.REMOVE_USER_ROLES, user.getId().toString());
            for (Roles role : user.getRoles()) {
                Roles canonical = roleMap.get(role.getRole().toUpperCase());
                if (canonical != null) {
                    jdbc.update(Constants.ASSIGN_ROLE,
                            user.getId().toString(),
                            canonical.getRoleId().toString());
                }
            }
        }
        return rows;
    }

    @Override
    public int deleteUser(UUID userId) {
        // Cascade deletes handle users_roles and sessions rows automatically
        return jdbc.update(Constants.DELETE_USER, userId.toString());
    }

    // ----------------------------------------------------------------
    // Roles
    // ----------------------------------------------------------------

    @Override
    public Map<String, Roles> findAllRoles() {
        Map<String, Roles> result = new HashMap<>();
        jdbc.query(Constants.GET_ALL_ROLES, ROLE_ROW_MAPPER)
                .forEach(r -> result.put(r.getRole(), r));
        return result;
    }

    @Override
    public boolean roleExists(UUID roleId) {
        Integer count = jdbc.queryForObject(
                Constants.GET_ROLE_BY_ID,
                Integer.class,
                roleId.toString());
        return count != null && count > 0;
    }

    @Override
    public void assignRole(UUID userId, UUID roleId) {
        // INSERT IGNORE means a duplicate pair is silently skipped — no exception
        // thrown
        jdbc.update(Constants.ASSIGN_ROLE_BY_ID,
                userId.toString(),
                roleId.toString());
    }

    // ----------------------------------------------------------------
    // Sessions
    // ----------------------------------------------------------------

    @Override
    public UUID openSession(UUID userId) {
        UUID sessionId = UUID.randomUUID();
        try {
            jdbc.update(Constants.CREATE_SESSION,
                    sessionId.toString(),
                    userId.toString());
        } catch (Exception e) {
            return null;
        }
        return sessionId;
    }

    @Override
    public int closeSession(UUID sessionId) {
        return jdbc.update(Constants.CLOSE_SESSION, sessionId.toString());
    }

    @Override
    public LastSession findLastSessionForUser(UUID userId) {
        List<LastSession> sessions = jdbc.query(
                Constants.GET_LAST_SESSION_FOR_USER,
                SESSION_ROW_MAPPER,
                userId.toString());
        return sessions.isEmpty() ? null : sessions.get(0);
    }

    // ----------------------------------------------------------------
    // Private helpers
    // ----------------------------------------------------------------

    /**
     * Collapses the flat JOIN result (one row per user+role) into a
     * map keyed by user ID, accumulating roles as we go.
     */
    private Map<UUID, User> mergeUserRows(List<User> rows) {
        Map<UUID, User> result = new HashMap<>();
        for (User row : rows) {
            result.compute(row.getId(), (id, existing) -> {
                if (existing == null) {
                    existing = new User();
                    existing.setId(row.getId());
                    existing.setName(row.getName());
                    existing.setEmail(row.getEmail());
                    existing.setPasswordHash(row.getPasswordHash());
                    existing.setCreatedAt(row.getCreatedAt());
                }
                row.getRoles().forEach(existing::addRole);
                return existing;
            });
        }
        return result;
    }
}