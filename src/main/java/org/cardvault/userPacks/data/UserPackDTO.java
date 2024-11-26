package org.cardvault.userPacks.data;

import org.cardvault.packTypes.data.PackTypeDTO;

public record UserPackDTO(PackTypeDTO packType,
                          int quantity) {
}
