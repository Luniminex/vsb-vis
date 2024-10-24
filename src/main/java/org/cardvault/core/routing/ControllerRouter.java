package org.cardvault.core.routing;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.cardvault.core.routing.annotations.Route;
import org.cardvault.core.routing.annotations.Controller;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ControllerRouter {
    private Map<String, HttpHandler> routes = new HashMap<>();
    private Map<Class<?>, Object> controllerInstances = new HashMap<>();

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
                            method.invoke(controllerInstance, exchange);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }

            controllerInstances.put(controllerClass, controllerInstance);
        } catch (Exception e) {
            e.printStackTrace();
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

    public Map<String, HttpHandler> getRoutes() {
        return routes;
    }

    public Map<Class<?>, Object> getControllerInstances() {
        return controllerInstances;
    }
}
