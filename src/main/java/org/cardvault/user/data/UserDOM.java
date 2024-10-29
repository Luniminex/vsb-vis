package org.cardvault.user.data;

public record UserDOM(String username, String password, Integer currency) {
    public UserDOM {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }
        if (currency == null) {
            throw new IllegalArgumentException("Currency cannot be null.");
        }
    }
}
