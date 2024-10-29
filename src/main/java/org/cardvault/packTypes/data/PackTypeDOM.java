package org.cardvault.packTypes.data;

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
}
