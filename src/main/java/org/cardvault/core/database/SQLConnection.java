package org.cardvault.core.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnection {

    private SQLConnection() {}
    public static Connection connect() {
        Connection connection = null;
        try {
            String url = "jdbc:sqlite:db/cardvault.db";
            connection = DriverManager.getConnection(url);
            System.out.println("Connected to SQLite database.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return connection;
    }
}
