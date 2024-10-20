package org.cardvault.core.startup;

import org.cardvault.core.database.SQLConnection;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
        Connection connection = SQLConnection.connect();


    }
}