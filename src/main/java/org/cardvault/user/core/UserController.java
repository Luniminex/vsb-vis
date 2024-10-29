package org.cardvault.user.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import org.cardvault.core.dependencyInjection.annotations.Injected;
import org.cardvault.core.logging.Logger;
import org.cardvault.core.routing.Response;
import org.cardvault.core.routing.annotations.Authorized;
import org.cardvault.core.routing.annotations.Controller;
import org.cardvault.core.routing.annotations.Route;
import org.cardvault.user.data.UserDTO;


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
    public Response getUser(HttpExchange exchange, UserDTO userDTO) {
        Logger.debug("Received get user request.");

        UserDTO user = userService.getUser(userDTO);
        if(user == null) {
            return Response.error(404, "User not found.");
        }

        return Response.ok(user);
    }

    @Route(path = "/login", method = "POST")
    public Response login(HttpExchange exchange, final UserDTO userDTO) {
        Logger.debug("Received login request.");
        return userService.login(userDTO);
    }

    @Route(path = "/register", method = "POST")
    public Response registerUser(HttpExchange exchange, final UserDTO userDTO) {
        Logger.debug("Received register request.");
        UserDTO user = userService.register(userDTO);
        if(user == null) {
            return Response.error(409, "User already exists with this username.");
        }
        return Response.ok(user);
    }
}
