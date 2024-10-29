package org.cardvault.packTypes.core;

import org.cardvault.cards.core.CardRepository;
import org.cardvault.cards.data.BuyPackDTO;
import org.cardvault.core.dependencyInjection.annotations.Initialization;
import org.cardvault.core.dependencyInjection.annotations.Injected;
import org.cardvault.packTypes.data.PackTypeDOM;
import org.cardvault.packTypes.data.PackTypeDTO;
import org.cardvault.packTypes.data.PackTypeMapper;
import org.cardvault.user.core.UserService;
import org.cardvault.user.data.UserDOM;
import org.cardvault.user.data.UserDTO;

import java.util.List;

public class PackTypeService {

    private PackTypeRepository packTypeRepository;

    private UserService userService;

    @Injected
    public void setPackTypeRepository(PackTypeRepository packTypeRepository) {
        this.packTypeRepository = packTypeRepository;
    }

    @Injected
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Initialization
    public void init() {
        packTypeRepository.loadUpPackTypes("cards/pack_types.json");
    }

    public PackTypeDTO getPackType(int id) {
        return PackTypeMapper.toDTO(packTypeRepository.getPackType(id));
    }

    public boolean buyPack(UserDTO userDTO, BuyPackDTO buyPackDTO) {
        PackTypeDOM packType = packTypeRepository.getPackType(buyPackDTO.id());
        userDTO = userService.getUser(userDTO);

        if (!hasSufficientFunds(userDTO.currency(), packType.price(), buyPackDTO.quantity())) {
            return false;
        }

        return packTypeRepository.buyPack(userDTO.username(), packType, buyPackDTO);
    }

    private boolean hasSufficientFunds(int userFunds, int price, int quantity) {
        return userFunds >= price * quantity;
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
}
