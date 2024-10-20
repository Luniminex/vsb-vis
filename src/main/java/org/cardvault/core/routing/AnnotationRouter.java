package org.cardvault.core.routing;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.cardvault.core.routing.annotations.Route;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class AnnotationRouter {
    private Map<String, HttpHandler> routes = new HashMap<>();

    public void registerRoutes(Object handler) {
        Method[] methods = handler.getClass().getDeclaredMethods();

        for (Method method : methods) {
            if (method.isAnnotationPresent(Route.class)) {
                Route route = method.getAnnotation(Route.class);
                String path = route.path();
                String httpMethod = route.method();

                routes.put(path + "::" + httpMethod, exchange -> {
                    try {
                        method.invoke(handler, exchange);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    public void handleRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        HttpHandler handler = routes.get(path + "::" + method);

        if (handler != null) {
            handler.handle(exchange);
        } else {
            String response = "404 Not Found";
            exchange.sendResponseHeaders(404, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
            exchange.getResponseBody().close();
        }
    }
}