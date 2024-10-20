package org.cardvault.core.routing;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Router {
    private Map<String, HttpHandler> routes = new HashMap<>();

    public void addRoute(String path, HttpHandler handler) {
        routes.put(path, handler);
    }

    public void handleRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        HttpHandler handler = routes.get(path);
        if (handler != null) {
            try {
                handler.handle(exchange);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            String response = "404 (Not Found)";
            exchange.sendResponseHeaders(404, response.length());
            exchange.getResponseBody().write(response.getBytes());
            exchange.getResponseBody().close();
        }
    }
}
