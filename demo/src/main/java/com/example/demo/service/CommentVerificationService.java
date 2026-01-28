package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CommentVerificationService {

    private static final Logger logger = LoggerFactory.getLogger(CommentVerificationService.class);

    private static class TokenEntry {
        String email;
        String token;
        LocalDateTime expiresAt;
    }

    // token -> TokenEntry
    private final Map<String, TokenEntry> tokenStore = new ConcurrentHashMap<>();

    // generate a short numeric token (6 digits) and store mapping
    public String generateTokenForEmail(String email, int minutesValid) {
        int code = Math.abs(UUID.randomUUID().hashCode()) % 1000000;
        String token = String.format("%06d", code);
        TokenEntry entry = new TokenEntry();
        entry.email = email;
        entry.token = token;
        entry.expiresAt = LocalDateTime.now().plusMinutes(minutesValid);
        tokenStore.put(keyFor(email, token), entry);
        logger.info("Generated verification token for {}: {} (expires at {})", email, token, entry.expiresAt);
        return token;
    }

    // validate token for email and action (action isn't used here but kept for future expansion)
    public boolean validateToken(String email, String token) {
        TokenEntry entry = tokenStore.get(keyFor(email, token));
        if (entry == null) return false;
        if (LocalDateTime.now().isAfter(entry.expiresAt)) {
            tokenStore.remove(keyFor(email, token));
            return false;
        }
        // one-time use
        tokenStore.remove(keyFor(email, token));
        return true;
    }

    private String keyFor(String email, String token) {
        return email + ":" + token;
    }
}
