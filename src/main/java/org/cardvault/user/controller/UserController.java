package org.cardvault.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import org.cardvault.core.dependencyInjection.annotations.Injected;
import org.cardvault.core.routing.annotations.Controller;
import org.cardvault.core.routing.annotations.Route;
import org.cardvault.user.dto.User;
import org.cardvault.user.service.UserService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

//transaction script
@Controller(path = "/user")
public class UserController {

    private UserService userService;

    @Injected
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Route(path = "/get", method = "GET")
    public void getUser(HttpExchange exchange) throws IOException {
        System.out.println("Getting user");
        String response = "User data";
        exchange.sendResponseHeaders(200, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
        exchange.getResponseBody().close();
    }

    @Route(path = "/register", method = "POST")
    public void registerUser(HttpExchange exchange) throws IOException {
        System.out.println("Registering user");
        InputStream requestBody = exchange.getRequestBody();
        String body = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);

        ObjectMapper objectMapper = new ObjectMapper();
        User user = objectMapper.readValue(body, User.class);
        User registered = userService.register(user);

        String response = "User " + registered.username() + " registered.";
        exchange.sendResponseHeaders(200, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
        exchange.getResponseBody().close();
    }
}
