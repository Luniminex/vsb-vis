package org.cardvault.core.server;

import com.sun.net.httpserver.HttpServer;
import org.cardvault.core.database.SQLConnectionPool;
import org.cardvault.core.dependencyInjection.DIContainer;
import org.cardvault.core.routing.ControllerRouter;
import org.cardvault.core.startup.Config;
import org.cardvault.user.controller.UserController;
import org.cardvault.user.repository.UserRepository;
import org.cardvault.user.service.UserService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class APIServer {

    private static Config config;
    private static HttpServer server;
    private static ControllerRouter controllerRouter;
    private static DIContainer diContainer;

    public APIServer() {
    }
    public static void startup(final Config config) throws IOException {
        System.out.println("Starting API server...");
        APIServer.config = config;
        server = HttpServer.create(new InetSocketAddress(config.getServer().getPort()), 0);
        controllerRouter = new ControllerRouter();
        diContainer = new DIContainer();

        registerRepositories();
        registerServices();
        registerHandlers();

        injectServices();

        server.createContext("/", controllerRouter::handleRequest);
        server.setExecutor(Executors.newFixedThreadPool(10));

        server.start();
        System.out.printf("API server started on port %s.%n", config.getServer().getPort());
    }

    private static void registerRepositories() {
        diContainer.register(SQLConnectionPool.class, new SQLConnectionPool(config.getDatabase()));
        diContainer.register(UserRepository.class, new UserRepository());
    }
    private static void registerServices() {
        diContainer.register(UserService.class, new UserService());
    }

    private static void registerHandlers() {
        controllerRouter.registerController(UserController.class);
    }

    private static void injectServices() {
        diContainer.getRegisteredServices().forEach((key, service) -> diContainer.injectDependencies(service));

        controllerRouter.getControllerInstances()
                .forEach((key, controller) -> diContainer.injectDependencies(controller));
    }
}
