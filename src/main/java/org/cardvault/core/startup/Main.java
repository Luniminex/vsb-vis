package org.cardvault.core.startup;

import org.cardvault.core.database.SQLConnection;
import org.cardvault.core.server.APIServer;

import java.io.IOException;
import java.sql.Connection;
import java.util.concurrent.CountDownLatch;

public class Main {
    public static void main(String[] args) {
        //Connection connection = SQLConnection.connect();
        try {
            APIServer.startup();
            new CountDownLatch(1).await();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}