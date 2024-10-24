package org.cardvault.core.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Singular;
import org.cardvault.core.startup.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLConnectionPool {

    private final HikariDataSource dataSource;

    public SQLConnectionPool(Config.Database databaseConfig) {
        initializeDatabase(databaseConfig.getUrl());

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(databaseConfig.getUrl());
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setIdleTimeout(60000);
        config.setMaxLifetime(1800000);

        // Inicializace poolu
        dataSource = new HikariDataSource(config);
    }
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    private static void initializeDatabase(String dbPath) {
        String url = dbPath;
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                System.out.println("SQLite database has been initialized at " + dbPath);
                Statement stmt = conn.createStatement();
                String createTableSQL = "CREATE TABLE IF NOT EXISTS example_table ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "name TEXT NOT NULL);";
                stmt.execute(createTableSQL);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}

