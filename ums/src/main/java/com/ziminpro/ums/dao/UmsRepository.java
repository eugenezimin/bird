package com.ziminpro.ums.dao;

import java.util.Map;
import java.util.UUID;

import com.ziminpro.ums.dtos.Roles;
import com.ziminpro.ums.dtos.User;

public interface UmsRepository {

    Map<UUID, User> findAllUsers();

    Map<String, Roles> findAllRoles();

    User findUserByID(UUID userId);

    UUID createUser(User user);

	int deleteUser(UUID userId);
}
