package org.cardvault.core.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Singular;
import org.cardvault.core.startup.Config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

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
        try (Connection conn = DriverManager.getConnection(dbPath);
             Statement stmt = conn.createStatement();
             BufferedReader br = new BufferedReader(new InputStreamReader(
                     Objects.requireNonNull(SQLConnectionPool.class
                             .getClassLoader()
                             .getResourceAsStream("database/schema.sql")))
             )) {

            StringBuilder sql = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sql.append(line);
                if (line.trim().endsWith(";")) {
                    stmt.executeUpdate(sql.toString());
                    sql.setLength(0);
                }
            }
            System.out.println("Database schema created successfully.");
        } catch (IOException | SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }
    }
}

