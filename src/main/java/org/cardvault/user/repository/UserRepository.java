package org.cardvault.user.repository;

import lombok.NonNull;
import org.cardvault.core.database.SQLConnectionPool;
import org.cardvault.core.dependencyInjection.annotations.Injected;
import org.cardvault.user.dto.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

//active record
public class UserRepository {
    private SQLConnectionPool connectionPool;

    public UserRepository() {}

    @Injected
    public void setSQLConnectionPool(SQLConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public User save(final User user) {
        String sql = "INSERT INTO example_table(name) VALUES(?)";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.username());

            pstmt.executeUpdate();

            System.out.println("User '" + user.username() + "' saved to database.");
        } catch (SQLException e) {
            System.out.println("Error saving user: " + e.getMessage());
        }
        return user;
    }

    public boolean verifyCredentials(@NonNull final String username, @NonNull final String password) {
        //check if user exists in database
        return true;
    }

}
