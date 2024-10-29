package org.cardvault.packTypes.data;

public record PackTypeDTO(int id,
                          String name,
                          String collection,
                          int price,
                          int cardsQuantity) {
}
