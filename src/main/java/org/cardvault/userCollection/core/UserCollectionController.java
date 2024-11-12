package org.cardvault.userCollection.core;

import com.sun.net.httpserver.HttpExchange;
import org.cardvault.cards.data.CardDOM;
import org.cardvault.core.dependencyInjection.annotations.Injected;
import org.cardvault.core.logging.Logger;
import org.cardvault.core.routing.Response;
import org.cardvault.core.routing.annotations.Authorized;
import org.cardvault.core.routing.annotations.Controller;
import org.cardvault.core.routing.annotations.Route;
import org.cardvault.user.data.UserDTO;

import java.util.List;

@Controller(path = "/usercollection")
public class UserCollectionController {
    public UserCollectionService userCollectionService;

    @Injected
    public void setUserCollectionService(UserCollectionService userCollectionService) {
        this.userCollectionService = userCollectionService;
    }

    @Route(path = "/get")
    @Authorized
    public Response getUserCollection(HttpExchange exchange, UserDTO userDTO) {
        Logger.debug("Received get user collection request.");
        List<CardDOM> cards = userCollectionService.getUserCollection(userDTO);
        if(cards != null) {
            Logger.debug(userDTO.username() + " got their collection.");
            return Response.ok(cards);
        } else {
            Logger.debug("Failed to get user collection.");
            return Response.error(400, "Failed to get user collection.");
        }
    }
}
