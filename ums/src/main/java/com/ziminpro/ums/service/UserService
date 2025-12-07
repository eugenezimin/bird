package com.ziminpro.ums.service;

import com.ziminpro.ums.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService {

    private final Map<String, User> users = new HashMap<>();
    private Long idCounter = 1L;

    // Map to store tokens and their expiry
    private final Map<String, TokenInfo> tokens = new HashMap<>();

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Save a new user (local login)
    public void saveUser(User user) {
        user.setId(idCounter++);
        user.setPassword(passwordEncoder.encode(user.getPassword())); // hash password
        users.put(user.getUsername(), user);
    }

    // Find user by username
    public User findByUsername(String username) {
        return users.get(username);
    }

    // Check credentials (local login)
    public boolean checkCredentials(String username, String rawPassword) {
        User user = users.get(username);
        return user != null && passwordEncoder.matches(rawPassword, user.getPassword());
    }

    // -------------------- OAuth2 / Token Methods --------------------

    // Generate a UUID token for a user after OAuth login
    public String generateToken(User user) {
        String token = UUID.randomUUID().toString();
        Instant expiry = Instant.now().plus(Duration.ofMinutes(15)); // 15-minute expiry
        tokens.put(token, new TokenInfo(user.getUsername(), expiry));
        return token;
    }

    // Validate a token and return associated username if valid
    public String validateToken(String token) {
        TokenInfo info = tokens.get(token);
        if (info == null) return null;  // token not found

        if (Instant.now().isAfter(info.getExpiry())) {
            tokens.remove(token);  // remove expired token
            return null;
        }
        return info.getUsername();
    }

    // Optional: remove token manually (logout)
    public void revokeToken(String token) {
        tokens.remove(token);
    }

    // Inner class to store token info
    private static class TokenInfo {
        private final String username;
        private final Instant expiry;

        public TokenInfo(String username, Instant expiry) {
            this.username = username;
            this.expiry = expiry;
        }

        public String getUsername() {
            return username;
        }

        public Instant getExpiry() {
            return expiry;
        }
    }
}
