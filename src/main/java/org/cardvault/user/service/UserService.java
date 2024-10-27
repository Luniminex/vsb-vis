package org.cardvault.user.service;

import org.cardvault.core.dependencyInjection.annotations.Injected;
import org.cardvault.user.dto.User;
import org.cardvault.user.repository.UserRepository;

//domain model
public class UserService {

    private UserRepository userRepository;

    public UserService() {}

    @Injected
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public User register(final User user) {
        System.out.println(user.username() + " registered.");
        return userRepository.save(user);
    }

    public void login() {
        System.out.println("User logged in.");
    }

    public void logout() {
        System.out.println("User logged out.");
    }

}
