package org.cardvault.userPacks.core;

import org.cardvault.core.dependencyInjection.annotations.Injected;
import org.cardvault.packTypes.core.PackTypeService;
import org.cardvault.packTypes.data.PackTypeDTO;
import org.cardvault.user.data.UserDTO;
import org.cardvault.userPacks.data.UserPackDOM;
import org.cardvault.userPacks.data.UserPackDTO;
import org.cardvault.userPacks.data.UserPackMapper;

import java.util.List;

public class UserPacksService {
    private UserPacksRepository userPacksRepository;
    private PackTypeService packTypeService;

    @Injected
    public void setUserPacksRepository(UserPacksRepository userPacksRepository) {
        this.userPacksRepository = userPacksRepository;
    }

    @Injected
    public void setPackTypeRepository(PackTypeService packTypeService) {
        this.packTypeService = packTypeService;
    }

    public List<UserPackDTO> getUserPacks(UserDTO userDTO) {
        List<UserPackDOM> userpacks = userPacksRepository.getUserPacks(userDTO.username());
        return userpacks.stream()
                .map(userPackDom -> {
                    PackTypeDTO packType = packTypeService.getPackType(userPackDom.packTypeId());
                    return UserPackMapper.fromDOM(userPackDom, packType);
                })
                .toList();
    }

    public void removePack(UserDTO userDTO, int id) {
        userPacksRepository.removePack(userDTO, id);
    }

    public boolean hasAtleastOnePackOfType(UserDTO userDTO, int packTypeId) {
        return userPacksRepository.hasAtleastOnePackOfType(userDTO, packTypeId);
    }
}
