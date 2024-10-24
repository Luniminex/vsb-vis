package org.cardvault.core.startup;

import org.cardvault.core.server.APIServer;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class Main {
    public static void main(String[] args) {
        try {
            Config conf = ConfigLoader.loadConfig("application-local.yaml");
            APIServer.startup(conf);
            new CountDownLatch(1).await();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}