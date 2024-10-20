package org.cardvault.core.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.cardvault.core.routing.AnnotationRouter;
import org.cardvault.core.routing.Router;
import org.cardvault.user.controller.UserController;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class APIServer {
    private static Integer PORT = 8000;
    private static HttpServer server;
    private static Router router;
    private static AnnotationRouter annotationRouter;

    public static void startup() throws IOException {
        System.out.println("Starting API server...");
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        router = new Router();
        annotationRouter = new AnnotationRouter();

        registerHandlers();

        server.createContext("/", annotationRouter::handleRequest);
        server.setExecutor(Executors.newFixedThreadPool(10));

        server.start();
        System.out.printf("API server started on port %s.%n", PORT);
    }

   private static void registerHandlers() {
        annotationRouter.registerRoutes(new UserController());
   }

}
