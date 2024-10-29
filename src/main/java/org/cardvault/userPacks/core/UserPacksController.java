package org.cardvault.userPacks.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import org.cardvault.core.dependencyInjection.annotations.Injected;
import org.cardvault.core.logging.Logger;
import org.cardvault.core.routing.Response;
import org.cardvault.core.routing.annotations.Authorized;
import org.cardvault.core.routing.annotations.Controller;
import org.cardvault.core.routing.annotations.Route;
import org.cardvault.user.data.UserDTO;

@Controller(path = "/userpacks")
public class UserPacksController {

    private UserPacksService userPacksService;

    @Injected
    public void setUserPacksService(UserPacksService userPacksService) {
        this.userPacksService = userPacksService;
    }

    @Route(path = "/get")
    @Authorized
    public Response getUserPacks(HttpExchange exchange, UserDTO userDTO) {
        Logger.debug("Received get user request.");
        return Response.ok(userPacksService.getUserPacks(userDTO));
    }


}