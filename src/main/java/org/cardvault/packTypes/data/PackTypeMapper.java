package org.cardvault.packTypes.data;

public class PackTypeMapper {
    public static PackTypeDTO toDTO(PackTypeDOM packTypeDOM) {
        return new PackTypeDTO(
                packTypeDOM.id(),
                packTypeDOM.name(),
                packTypeDOM.collection(),
                packTypeDOM.price(),
                packTypeDOM.cards_per_pack()
        );
    }

    public static PackTypeDOM toDOM(PackTypeDTO packTypeDTO) {
        return new PackTypeDOM(
                packTypeDTO.id(),
                packTypeDTO.name(),
                packTypeDTO.collection(),
                packTypeDTO.price(),
                packTypeDTO.cardsQuantity(),
                0,
                0,
                0,
                0,
                0,
                0
        );
    }
}
