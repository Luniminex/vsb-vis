package org.cardvault.packTypes.data;

import org.cardvault.cards.data.CardRarity;

import java.util.Map;

public record PackTypeDOM(int id,
                          String name,
                          String collection,
                          int price,
                          int cards_per_pack,
                          float common_chance,
                          float uncommon_chance,
                          float rare_chance,
                          float epic_chance,
                          float legendary_chance,
                          float mythic_chance
) {
    public Map<CardRarity, Float> getPackChances() {
        return Map.of(
                CardRarity.COMMON, common_chance,
                CardRarity.UNCOMMON, uncommon_chance,
                CardRarity.RARE, rare_chance,
                CardRarity.EPIC, epic_chance,
                CardRarity.LEGENDARY, legendary_chance,
                CardRarity.MYTHIC, mythic_chance
        );
    }
}
