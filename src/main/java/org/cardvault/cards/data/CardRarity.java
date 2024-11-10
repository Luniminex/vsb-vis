package org.cardvault.cards.data;

public enum CardRarity {
    COMMON,
    UNCOMMON,
    RARE,
    EPIC,
    LEGENDARY,
    MYTHIC;

    public static CardRarity fromString(String rarity) {
        return switch (rarity) {
            case "Common" -> COMMON;
            case "Uncommon" -> UNCOMMON;
            case "Rare" -> RARE;
            case "Epic" -> EPIC;
            case "Legendary" -> LEGENDARY;
            case "Mythic" -> MYTHIC;
            default -> throw new IllegalArgumentException("Invalid rarity: " + rarity);
        };
    }
}