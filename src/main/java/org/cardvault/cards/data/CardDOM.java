package org.cardvault.cards.data;

public record CardDOM (int id,
                       String name,
                       String rarity,
                       int hp,
                       int dmg,
                       String collection,
                       int releaseNumber) {
}

