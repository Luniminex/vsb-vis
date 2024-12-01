package org.cardvault.user.core;

import lombok.NonNull;
import org.cardvault.core.database.SQLConnectionPool;
import org.cardvault.core.dependencyInjection.annotations.Injected;
import org.cardvault.core.logging.Logger;
import org.cardvault.user.data.UserDOM;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

//Data mapper
public class UserRepository {
    private SQLConnectionPool connectionPool;

    public UserRepository() {
    }

    @Injected
    public void setSQLConnectionPool(SQLConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public UserDOM getUser(final String username) {
        String sql = "SELECT username, password, currency FROM users WHERE username = ? LIMIT 1";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                return new UserDOM(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getInt("currency"));
            }
            return null;
        } catch (SQLException e) {
            Logger.error("Error getting user: " + e.getMessage());
            return null;
        }
    }

    public UserDOM save(final UserDOM userDOM) {
        String checkSql = "SELECT 1 FROM users WHERE username = ? LIMIT 1";
        String insertSql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

            checkStmt.setString(1, userDOM.username());
            if (checkStmt.executeQuery().next()) {
                Logger.info("User already exists.");
                return null;
            }

            insertStmt.setString(1, userDOM.username());
            insertStmt.setString(2, userDOM.password());
            insertStmt.executeUpdate();

            Logger.info("User registered.");
            return userDOM;
        } catch (SQLException e) {
            Logger.error("Error saving user: " + e.getMessage());
            return null;
        }
    }

    public boolean exists(final String username) {
        String sql = "SELECT 1 FROM users WHERE username = ? LIMIT 1";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            return pstmt.executeQuery().next();
        } catch (SQLException e) {
            Logger.error("Error checking if user exists: " + e.getMessage());
            return false;
        }
    }

    public boolean verifyCredentials(@NonNull final String username, @NonNull final String password) {
        String sql = "SELECT 1 FROM users WHERE username = ? AND password = ? LIMIT 1";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            return pstmt.executeQuery().next();
        } catch (SQLException e) {
            System.out.println("Error verifying credentials: " + e.getMessage());
            return false;
        }
    }

}
