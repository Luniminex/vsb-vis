package org.cardvault.userCollection.core;

import org.cardvault.core.database.SQLConnectionPool;
import org.cardvault.core.dependencyInjection.annotations.Injected;
import org.cardvault.core.logging.Logger;
import org.cardvault.user.data.UserDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserCollectionRepository {

    private SQLConnectionPool connectionPool;

    @Injected
    public void setSQLConnectionPool(SQLConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public boolean hasAtleastOnePackOfType(UserDTO userDTO, int packTypeId) {
        String sql = "SELECT 1 FROM user_packs WHERE user_id = (SELECT id FROM users WHERE username = ?) AND pack_type_id = ? AND quantity > 0 LIMIT 1";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userDTO.username());
            pstmt.setInt(2, packTypeId);
            return pstmt.executeQuery().next();
        } catch (SQLException e) {
            Logger.error("Error checking if user has at least one pack of type: " + e.getMessage());
            return false;
        }
    }
}
