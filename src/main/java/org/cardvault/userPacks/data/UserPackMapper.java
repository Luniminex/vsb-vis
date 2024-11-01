package org.cardvault.userPacks.data;

import org.cardvault.packTypes.data.PackTypeDTO;
import org.cardvault.user.data.UserDTO;

public class UserPackMapper {
    public static UserPackDTO fromDOM(UserPackDOM userPackDOM, PackTypeDTO packType) {
        return new UserPackDTO(
            packType,
            userPackDOM.quantity()
        );
    }
}
