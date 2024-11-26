package org.cardvault.userCollection.data;

public record CollectionDataDTO(int totalCards,
                                int totalCardsCollected,
                                int commonCount,
                                int uncommonCount,
                                int rareCount,
                                int epicCount,
                                int legendaryCount,
                                int mythicCount) {

}
