package com.ziminpro.ums.dao;

import com.ziminpro.ums.dtos.LastSession;
import com.ziminpro.ums.dtos.Roles;
import com.ziminpro.ums.dtos.User;

import java.util.Map;
import java.util.UUID;

public interface UmsRepository {

    // ---- Users ----
    Map<UUID, User> findAllUsers();
    User            findUserByID(UUID userId);
    UUID            createUser(User user);
    int             updateUser(User user);
    int             deleteUser(UUID userId);

    // ---- Roles ----
    Map<String, Roles> findAllRoles();

    // ---- Sessions ----
    UUID        openSession(UUID userId);
    int         closeSession(UUID sessionId);
    LastSession findLastSessionForUser(UUID userId);
}