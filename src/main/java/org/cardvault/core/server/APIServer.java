package org.cardvault.core.server;

import com.sun.net.httpserver.HttpServer;
import org.cardvault.cards.core.CardRepository;
import org.cardvault.cards.core.CardsService;
import org.cardvault.core.database.SQLConnectionPool;
import org.cardvault.core.dependencyInjection.DIContainer;
import org.cardvault.core.routing.AuthHandler;
import org.cardvault.core.routing.ControllerRouter;
import org.cardvault.core.startup.Config;
import org.cardvault.packTypes.core.PackTypeRepository;
import org.cardvault.packTypes.core.PackTypeService;
import org.cardvault.packTypes.core.PackTypesController;
import org.cardvault.user.core.UserController;
import org.cardvault.user.core.UserRepository;
import org.cardvault.user.core.UserService;
import org.cardvault.userCollection.core.UserCollectionController;
import org.cardvault.userCollection.core.UserCollectionRepository;
import org.cardvault.userCollection.core.UserCollectionService;
import org.cardvault.userPacks.core.UserPacksController;
import org.cardvault.userPacks.core.UserPacksRepository;
import org.cardvault.userPacks.core.UserPacksService;

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
        diContainer.register(CardRepository.class, new CardRepository());
        diContainer.register(PackTypeRepository.class, new PackTypeRepository());
        diContainer.register(UserPacksRepository.class, new UserPacksRepository());
        diContainer.register(UserCollectionRepository.class, new UserCollectionRepository());
    }
    private static void registerServices() {
        diContainer.register(AuthHandler.class, new AuthHandler());
        diContainer.register(UserService.class, new UserService());
        diContainer.register(CardsService.class, new CardsService());
        diContainer.register(PackTypeService.class, new PackTypeService());
        diContainer.register(UserPacksService.class, new UserPacksService());
        diContainer.register(UserCollectionService.class, new UserCollectionService());
    }

    private static void registerHandlers() {
        controllerRouter.registerController(UserController.class);
        controllerRouter.registerController(PackTypesController.class);
        controllerRouter.registerController(UserPacksController.class);
        controllerRouter.registerController(UserCollectionController.class);
    }
    private static void injectServices() {
        // Inject dependencies into all registered services and controllers
        diContainer.getRegisteredServices().forEach((key, service) -> diContainer.injectDependencies(service));

        // Inject dependencies into all registered controllers
        controllerRouter.getControllerInstances()
                .forEach((key, controller) -> diContainer.injectDependencies(controller));

        // Inject dependencies into the controller router
        diContainer.injectDependencies(controllerRouter);

        diContainer.init();
    }
}
