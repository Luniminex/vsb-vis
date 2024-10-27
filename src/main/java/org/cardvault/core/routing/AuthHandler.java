package org.cardvault.core.routing;

import org.cardvault.core.dependencyInjection.annotations.Injected;
import org.cardvault.core.logging.Logger;
import org.cardvault.user.repository.UserRepository;
import org.cardvault.user.service.UserService;

public class AuthHandler {

    private UserRepository userRepository;

    @Injected
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public boolean verifyCredentials(String encodedCredentials) {
        Logger.debug("Verifying credentials");
        //decode
        String decodedCredentials = new String(java.util.Base64.getDecoder().decode(encodedCredentials.substring(6)));
        String[] credentials = decodedCredentials.split(":");
        if (credentials.length != 2) {
            return false;
        }

        String username = credentials[0];
        String password = credentials[1];
        Logger.debug("Credentials: " + username + " " + password);
        return true;
    }
}
