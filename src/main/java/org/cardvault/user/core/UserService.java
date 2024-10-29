package org.cardvault.user.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cardvault.core.dependencyInjection.annotations.Injected;
import org.cardvault.core.routing.Response;
import org.cardvault.user.data.UserDOM;
import org.cardvault.user.data.UserDTO;
import org.cardvault.user.data.UserMapper;

//domain model
public class UserService {

    private UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserService() {
    }

    @Injected
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO getUser(final UserDTO userDTO) {
        UserDOM user = userRepository.getUser(userDTO.username());
        return UserMapper.toUserDTO(user);
    }

    public UserDTO register(final UserDTO userDTO) {
        UserDOM saved = userRepository.save(UserMapper.toUserDOM(userDTO));
        return UserMapper.toUserDTO(saved);
    }

    public Response login(final UserDTO userDTO) {
        if (!userRepository.exists(userDTO.username())) {
            return Response.error(404, "User not found.");
        }
        if (!userRepository.verifyCredentials(userDTO.username(), userDTO.password())) {
            return Response.error(401, "Failed to log in. Incorrect password.");
        }
        return Response.ok("User logged in.");
    }
}
