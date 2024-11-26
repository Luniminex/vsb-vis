package org.cardvault.userCollection.data;

import org.cardvault.cards.data.CardDOM;

import java.time.LocalDateTime;

public record UserCardDataDTO(CardDOM card,
                              int quantity,
                              LocalDateTime firstAcquired) {
}
