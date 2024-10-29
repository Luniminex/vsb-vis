package org.cardvault.user.data;

public class UserMapper {
    public static UserDOM toUserDOM(UserDTO userDTO) {
        return new UserDOM(
                userDTO.username(),
                userDTO.password(),
                0);
    }

    public static UserDTO toUserDTO(UserDOM userDOM) {
        return new UserDTO(
                userDOM.username(),
                userDOM.password(),
                userDOM.currency(),
                null,
                null);
    }

    public static UserDTO toUserDTO(UserDOM userDOM, int totalCards, int finishedCollections) {
        return new UserDTO(
                userDOM.username(),
                userDOM.password(),
                userDOM.currency(),
                totalCards,
                finishedCollections);
    }
}
