package org.cardvault.userCollection.data;

public record CollectionData(int totalCards,
                             int totalCardsCollected,
                             int commonCount,
                             int uncommonCount,
                             int rareCount,
                             int epicCount,
                             int legendaryCount,
                             int mythicCount) {

}
