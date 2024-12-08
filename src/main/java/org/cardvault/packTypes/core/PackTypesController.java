package org.cardvault.packTypes.core;

import com.sun.net.httpserver.HttpExchange;
import org.cardvault.packTypes.data.BuyPackDTO;
import org.cardvault.cards.data.CardDOM;
import org.cardvault.packTypes.data.OpenPackDTO;
import org.cardvault.core.dependencyInjection.annotations.Injected;
import org.cardvault.core.logging.Logger;
import org.cardvault.core.routing.Response;
import org.cardvault.core.routing.annotations.Authorized;
import org.cardvault.core.routing.annotations.Controller;
import org.cardvault.core.routing.annotations.Route;
import org.cardvault.packTypes.data.PackTypeDTO;
import org.cardvault.user.data.UserDTO;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller(path = "/packs")
public class PackTypesController {

    private PackTypeService packTypeService;

    @Injected
    public void setPackTypeService(PackTypeService packTypeService) {
        this.packTypeService = packTypeService;
    }

    @Route(path = "/buy", method = "POST")
    @Authorized
    public Response buyPack(HttpExchange exchange, UserDTO userDTO, BuyPackDTO buyPackDTO) {
        Logger.debug("Received buy pack request.");

        if(packTypeService.buyPack(userDTO, buyPackDTO)) {
            Logger.debug(userDTO.username() + " bought " + buyPackDTO.quantity() +" pack(s) " + buyPackDTO.id());
            return Response.ok("Packs bought.");
        } else {
            Logger.debug("Failed to buy pack.");
            return Response.error(400, "Failed to buy pack.");
        }
    }

    @Route(path ="/open", method = "POST")
    @Authorized
    public Response openPack(HttpExchange exchange, UserDTO userDTO, OpenPackDTO openPackDTO) {
        Logger.debug("Received open pack request.");
        List<CardDOM> cards = packTypeService.openPack(userDTO, openPackDTO.packTypeId());

        if(cards != null) {
            Logger.debug(userDTO.username() + " opened pack " + openPackDTO.packTypeId());
            return Response.ok(cards);
        } else {
            Logger.debug("Failed to open pack.");
            return Response.error(400, "Failed to open pack.");
        }
    }

    @Route(path = "/getAll")
    @Authorized
    public Response getAllPacks(HttpExchange exchange) {
        Logger.debug("Received get all packs request.");
        List<PackTypeDTO> packs = packTypeService.getAllPacks();
        return Response.ok(packs);
    }

    @Route(path = "/getbycollection")
    @Authorized
    public Response getPacksByCollection(HttpExchange exchange) {
        Logger.debug("Received get packs by collection request.");

        String query = exchange.getRequestURI().getQuery();
        Map<String, String> queryParams = parseQueryParams(query);

        String collectionName = queryParams.get("collection");

        if (collectionName == null || collectionName.isEmpty()) {
            return Response.error(400, "Collection name is required.");
        }

        List<PackTypeDTO> packs = packTypeService.getPacksByCollection(collectionName);
        return Response.ok(packs);
    }

    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> queryParams = new HashMap<>();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                if (idx > 0 && idx < pair.length() - 1) {
                    queryParams.put(URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8),
                            URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8));
                }
            }
        }
        return queryParams;
    }
}
