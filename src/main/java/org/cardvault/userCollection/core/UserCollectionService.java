package org.cardvault.userCollection.core;

import org.cardvault.core.dependencyInjection.annotations.Injected;
import org.cardvault.user.core.UserService;
import org.cardvault.user.data.UserDTO;
import org.cardvault.userCollection.data.CollectionDataDTO;
import org.cardvault.userCollection.data.UserCardDataDTO;

import java.util.List;

public class UserCollectionService {
    private UserCollectionRepository userCollectionRepository;
    private UserService userService;
    @Injected
    public void setUserCollectionRepository(UserCollectionRepository userCollectionRepository) {
        this.userCollectionRepository = userCollectionRepository;
    }

    @Injected
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void addCardToCollection(UserDTO userDTO, int cardId) {
        userCollectionRepository.addCardToCollection(userDTO, cardId);
    }

    public List<UserCardDataDTO> getUserCollection(UserDTO userDTO) {
        return userCollectionRepository.getUserCollection(userDTO.username());
    }

    public CollectionDataDTO getUserCollectionData(UserDTO userDTO) {
        return userCollectionRepository.getUserCollectionData(userDTO.username());
    }
}
