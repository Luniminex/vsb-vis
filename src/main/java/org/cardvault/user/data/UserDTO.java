package org.cardvault.user.data;

public record UserDTO(String username,
                      String password,
                      Integer currency,
                      Integer totalCards,
                      Integer finishedCollections) {
}
