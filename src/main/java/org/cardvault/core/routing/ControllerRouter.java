package org.cardvault.core.routing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.Getter;
import org.cardvault.core.dependencyInjection.annotations.Injected;
import org.cardvault.core.logging.Logger;
import org.cardvault.core.routing.annotations.Route;
import org.cardvault.core.routing.annotations.Authorized;
import org.cardvault.core.routing.annotations.Controller;
import org.cardvault.core.utils.DefaultObjectMapper;
import org.cardvault.user.data.UserDTO;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ControllerRouter {
    @Getter
    private final Map<String, HttpHandler> routes = new HashMap<>();
    @Getter
    private final Map<Class<?>, Object> controllerInstances = new HashMap<>();
    private final ObjectMapper objectMapper = DefaultObjectMapper.createObjectMapper();

    private AuthHandler authHandler;
    @Injected
    public void setAuthHandler(AuthHandler authHandler) {
        this.authHandler = authHandler;
    }

    public ControllerRouter() {}

    public void registerController(Class<?> controllerClass) {
        try {
            Object controllerInstance = controllerClass.getDeclaredConstructor().newInstance();
            String rootPath = "";

            if (controllerClass.isAnnotationPresent(Controller.class)) {
                Controller controllerAnnotation = controllerClass.getAnnotation(Controller.class);
                rootPath = controllerAnnotation.path();
            }

            Method[] methods = controllerClass.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(Route.class)) {
                    Route route = method.getAnnotation(Route.class);
                    String path = rootPath + route.path();
                    String httpMethod = route.method();

                    routes.put(path + "::" + httpMethod, exchange -> {
                        try {
                            if (method.isAnnotationPresent(Authorized.class) && !isAuthorized(exchange)) {
                                sendResponse(exchange, Response.error(401, "Unauthorized"));
                                return;
                            }

                            Object[] params;
                            try {
                                params = mapParameters(method, exchange);
                            } catch (IOException e) {
                                Logger.error("Parameter mapping error: " + e.getMessage());
                                sendResponse(exchange, Response.error(400, "Bad Request"));
                                return;
                            }

                            Object result = method.invoke(controllerInstance, params);

                            if (result instanceof Response response) {
                                sendResponse(exchange, response);
                            }
                        } catch (Exception e) {
                            Logger.error(e.toString());
                            sendResponse(exchange, Response.error(500, "Internal Server Error"));
                        }
                    });
                }
            }

            controllerInstances.put(controllerClass, controllerInstance);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isAuthorized(HttpExchange exchange) {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            return false;
        }

        return authHandler.verifyCredentials(authHeader);
    }

    private Object[] mapParameters(Method method, HttpExchange exchange) throws IOException {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];

        boolean isAuthorizedMethod = method.isAnnotationPresent(Authorized.class);

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> paramType = parameter.getType();

            if (paramType.isAssignableFrom(HttpExchange.class)) {
                args[i] = exchange;
            } else if (isAuthorizedMethod && paramType.isAssignableFrom(UserDTO.class)) {
                String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
                args[i] = authHandler.getUserDTOFromAuth(authHeader);
            } else {
                if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    InputStream requestBody = exchange.getRequestBody();
                    String body = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
                    args[i] = objectMapper.readValue(body, paramType);
                }
            }
        }
        return args;
    }

    private void sendResponse(HttpExchange exchange, Response response) throws IOException {
        addCorsHeaders(exchange);
        exchange.sendResponseHeaders(response.getStatus(), response.getBody().getBytes().length);
        exchange.getResponseBody().write(response.getBody().getBytes());
        exchange.getResponseBody().close();
    }

    private void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    public void handleRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        HttpHandler handler = routes.get(path + "::" + method);
        Logger.debug("Handling request: " + method + " " + path);
        if ("OPTIONS".equalsIgnoreCase(method)) {
            addCorsHeaders(exchange);
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (handler != null) {
            handler.handle(exchange);
        } else {
            sendResponse(exchange, Response.error(404, "404 Not Found"));
        }
    }
}
