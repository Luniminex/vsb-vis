package org.cardvault.user.dto;

public record User(String username, String password) {
    public User {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }
    }

}
