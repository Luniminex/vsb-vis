package org.cardvault.userCollection.core;

import org.cardvault.core.dependencyInjection.annotations.Injected;
import org.cardvault.user.data.UserDTO;

public class UserCollectionService {
    private UserCollectionRepository userCollectionRepository;

    @Injected
    public void setUserCollectionRepository(UserCollectionRepository userCollectionRepository) {
        this.userCollectionRepository = userCollectionRepository;
    }

    public boolean hasAtleastOnePackOfType(UserDTO userDTO, int packTypeId) {
        return userCollectionRepository.hasAtleastOnePackOfType(userDTO, packTypeId);
    }
}
