package org.cardvault.core.routing;

import org.cardvault.core.dependencyInjection.annotations.Injected;
import org.cardvault.core.logging.Logger;
import org.cardvault.user.data.UserDTO;
import org.cardvault.user.core.UserRepository;

public class AuthHandler {

    private UserRepository userRepository;

    @Injected
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    record Credentials(String username, String password) {}

    private Credentials extractCredentials(String encodedCredentials) {
        //decode
        String decodedCredentials = new String(java.util.Base64.getDecoder().decode(encodedCredentials.substring(6)));
        String[] credentials = decodedCredentials.split(":");
        if (credentials.length != 2) {
            return null;
        }

        return new Credentials(credentials[0], credentials[1]);
    }

    public boolean verifyCredentials(String encodedCredentials) {
        Logger.debug("Verifying credentials");
        Credentials credentials = extractCredentials(encodedCredentials);
        if(credentials == null) {
            return false;
        }

        return userRepository.verifyCredentials(credentials.username(), credentials.password());
    }

    public UserDTO getUserDTOFromAuth(String encodedCredentials) {
        Credentials credentials = extractCredentials(encodedCredentials);
        if(credentials == null) {
            return null;
        }

        String username = credentials.username();
        String password = credentials.password();
        //TODO: when you have card collection repo get correct values
        return new UserDTO(username, password, 0, 0, 0);
    }
}
