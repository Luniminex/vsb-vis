package org.cardvault.cards.core;

import org.cardvault.core.dependencyInjection.annotations.Initialization;
import org.cardvault.core.dependencyInjection.annotations.Injected;

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

}
