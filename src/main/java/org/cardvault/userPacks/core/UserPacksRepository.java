package org.cardvault.userPacks.core;

import org.cardvault.core.database.SQLConnectionPool;
import org.cardvault.core.dependencyInjection.annotations.Injected;
import org.cardvault.core.logging.Logger;
import org.cardvault.user.data.UserDTO;
import org.cardvault.userPacks.data.UserPackDOM;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserPacksRepository {

    private SQLConnectionPool sqlConnectionPool;

    @Injected
    public void setSqlConnectionPool(SQLConnectionPool sqlConnectionPool) {
        this.sqlConnectionPool = sqlConnectionPool;
    }

    public List<UserPackDOM> getUserPacks(String username) {
        String sql = "SELECT up.id, up.pack_type_id, up.quantity " +
                "FROM user_packs up " +
                "JOIN users u ON up.user_id = u.id " +
                "WHERE u.username = ?";
        List<UserPackDOM> userPacks = new ArrayList<>();

        try (Connection conn = sqlConnectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                UserPackDOM userPack = new UserPackDOM(
                        rs.getInt("id"),
                        rs.getInt("pack_type_id"),
                        rs.getInt("quantity")
                );
                userPacks.add(userPack);
            }
        } catch (SQLException e) {
            Logger.error("Error getting user packs: " + e.getMessage());
        }

        return userPacks;
    }

    public void removePack(UserDTO userDTO, int id) {
        String sql = "UPDATE user_packs SET quantity = quantity - 1 WHERE user_id = (SELECT id FROM users WHERE username = ?) AND pack_type_id = ? AND quantity > 0";
        try (Connection conn = sqlConnectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userDTO.username());
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Logger.error("Error removing pack from user: " + e.getMessage());
        }
    }
}

