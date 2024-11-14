package org.cardvault.userCollection.data;

import org.cardvault.cards.data.CardDOM;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public record UserCardData(CardDOM card,
                           int quantity,
                           LocalDateTime firstAcquired) {
}
