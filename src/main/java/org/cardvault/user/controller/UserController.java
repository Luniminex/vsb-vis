package org.cardvault.user.controller;

import com.sun.net.httpserver.HttpExchange;
import org.cardvault.core.dependencyInjection.annotations.Injected;
import org.cardvault.core.routing.Response;
import org.cardvault.core.routing.annotations.Authorized;
import org.cardvault.core.routing.annotations.Controller;
import org.cardvault.core.routing.annotations.Route;
import org.cardvault.user.dto.User;
import org.cardvault.user.service.UserService;


//transaction script
@Controller(path = "/user")
public class UserController {

    private UserService userService;

    @Injected
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Route(path = "/get")
    @Authorized
    public Response getUser(HttpExchange exchange) {
        System.out.println("Getting user");
        return Response.ok("User retrieved.");
    }

    @Route(path = "/register", method = "POST")
    public Response registerUser(HttpExchange exchange, User user) {
        System.out.println("Registering user");

        return Response.ok("User " + user.username() + " registered.");
    }
}
