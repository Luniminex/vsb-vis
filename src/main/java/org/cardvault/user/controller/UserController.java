package org.cardvault.user.controller;

import com.sun.net.httpserver.HttpExchange;
import org.cardvault.core.routing.annotations.Route;

import java.io.IOException;

public class UserController {
    @Route(path = "/user", method = "GET")
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
        String response = "User registered";
        exchange.sendResponseHeaders(200, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
        exchange.getResponseBody().close();
    }
}
