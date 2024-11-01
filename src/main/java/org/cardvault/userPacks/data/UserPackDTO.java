package org.cardvault.userPacks.data;

import org.cardvault.packTypes.data.PackTypeDTO;
import org.cardvault.user.data.UserDTO;

import java.sql.Timestamp;

public record UserPackDTO(PackTypeDTO packType,
                          int quantity) {
}
