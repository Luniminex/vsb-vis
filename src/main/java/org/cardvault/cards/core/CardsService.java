package org.cardvault.cards.core;

import org.cardvault.cards.data.CardDOM;
import org.cardvault.core.dependencyInjection.annotations.Initialization;
import org.cardvault.core.dependencyInjection.annotations.Injected;

import java.util.Collection;
import java.util.Set;

public class CardsService {
    private CardRepository cardRepository;

    @Injected
    public void setCardRepository(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Initialization
    public void init() {
        cardRepository.loadUpCards("cards/cards.json");
    }

    public Set<CardDOM> getCardsByCollection(String collection) {
        return cardRepository.getCardsByCollection(collection);
    }
}
