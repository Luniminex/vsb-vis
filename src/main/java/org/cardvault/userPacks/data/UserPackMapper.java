package org.cardvault.userPacks.data;

import org.cardvault.packTypes.data.PackTypeDTO;
import org.cardvault.user.data.UserDTO;

public class UserPackMapper {
    public static UserPackDTO fromDOM(UserPackDOM userPackDOM, UserDTO user, PackTypeDTO packType) {
        return new UserPackDTO(
            user,
            packType,
            userPackDOM.quantity()
        );
    }
}
