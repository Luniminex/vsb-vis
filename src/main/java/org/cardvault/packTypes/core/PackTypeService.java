package org.cardvault.packTypes.core;

import org.cardvault.cards.core.CardsService;
import org.cardvault.cards.data.BuyPackDTO;
import org.cardvault.cards.data.CardDOM;
import org.cardvault.cards.data.CardRarity;
import org.cardvault.core.dependencyInjection.annotations.Initialization;
import org.cardvault.core.dependencyInjection.annotations.Injected;
import org.cardvault.packTypes.data.PackTypeDOM;
import org.cardvault.packTypes.data.PackTypeDTO;
import org.cardvault.packTypes.data.PackTypeMapper;
import org.cardvault.user.core.UserService;
import org.cardvault.user.data.UserDTO;
import org.cardvault.userCollection.core.UserCollectionService;
import org.cardvault.userPacks.core.UserPacksService;

import java.util.*;
import java.util.stream.Collectors;

public class PackTypeService {

    private PackTypeRepository packTypeRepository;
    private UserCollectionService userCollectionService;
    private UserPacksService userPacksService;
    private CardsService cardsService;
    private UserService userService;

    @Injected
    public void setPackTypeRepository(PackTypeRepository packTypeRepository) {
        this.packTypeRepository = packTypeRepository;
    }

    @Injected
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Injected
    public void setUserCollectionService(UserCollectionService userCollectionService) {
        this.userCollectionService = userCollectionService;
    }

    @Injected
    public void setCardsService(CardsService cardsService) {
        this.cardsService = cardsService;
    }

    @Injected
    public void setUserPacksService(UserPacksService userPacksService) {
        this.userPacksService = userPacksService;
    }

    @Initialization
    public void init() {
        packTypeRepository.loadUpPackTypes("cards/pack_types.json");
    }

    public PackTypeDTO getPackType(int id) {
        return PackTypeMapper.toDTO(packTypeRepository.getPackType(id));
    }

    public List<CardDOM> openPack(UserDTO userDTO, int packId) {
        PackTypeDOM packType = packTypeRepository.getPackType(packId);
        userDTO = userService.getUser(userDTO);

        if (!userCollectionService.hasAtleastOnePackOfType(userDTO, packType.id())) {
            return List.of();
        }

        Map<CardRarity, Float> packChances = packType.getPackChances();
        Map<CardRarity, Set<CardDOM>> packCards = cardsService.getCardsByCollection(packType.collection()).stream()
                .collect(Collectors.groupingBy(card -> CardRarity.fromString(card.rarity()), Collectors.toSet()));
        final int noCardsToDraw = packType.cards_per_pack();

        userPacksService.removePack(userDTO, packType.id());
        return drawCards(noCardsToDraw, packChances, packCards);
    }

    public boolean buyPack(UserDTO userDTO, BuyPackDTO buyPackDTO) {
        PackTypeDOM packType = packTypeRepository.getPackType(buyPackDTO.id());
        userDTO = userService.getUser(userDTO);

        if (!hasSufficientFunds(userDTO.currency(), packType.price(), buyPackDTO.quantity())) {
            return false;
        }

        return packTypeRepository.buyPack(userDTO.username(), packType, buyPackDTO);
    }

    public List<PackTypeDTO> getPacksByCollection(String collectionName) {
        return packTypeRepository.getPacksByCollection(collectionName).stream()
                .map(PackTypeMapper::toDTO)
                .toList();
    }

    public List<PackTypeDTO> getAllPacks() {
        return packTypeRepository.getAllPacks().stream()
                .map(PackTypeMapper::toDTO)
                .toList();
    }

    private static List<CardDOM> drawCards(int noCardsToDraw, Map<CardRarity, Float> packChances, Map<CardRarity, Set<CardDOM>> packCards) {
        List<CardDOM> selectedCards = new ArrayList<>();
        Random random = new Random();

        float totalChance = packChances.values().stream().reduce(0.0f, Float::sum);
        Map<CardRarity, Float> normalizedChances = packChances.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue() / totalChance));

        for (int i = 0; i < noCardsToDraw; i++) {
            float chance = random.nextFloat();
            float cumulativeChance = 0.0f;
            CardRarity selectedRarity = null;

            for (Map.Entry<CardRarity, Float> entry : normalizedChances.entrySet()) {
                cumulativeChance += entry.getValue();
                if (chance <= cumulativeChance) {
                    selectedRarity = entry.getKey();
                    break;
                }
            }

            if (selectedRarity != null && packCards.containsKey(selectedRarity)) {
                List<CardDOM> cardsOfSelectedRarity = new ArrayList<>(packCards.get(selectedRarity));
                if (!cardsOfSelectedRarity.isEmpty()) {
                    CardDOM selectedCard = cardsOfSelectedRarity.get(random.nextInt(cardsOfSelectedRarity.size()));
                    selectedCards.add(selectedCard);
                }
            }
        }
        return selectedCards;
    }

    private boolean hasSufficientFunds(int userFunds, int price, int quantity) {
        return userFunds >= price * quantity;
    }
}
