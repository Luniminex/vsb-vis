package org.cardvault.userCollection.core;

import org.cardvault.cards.data.CardDOM;
import org.cardvault.core.database.SQLConnectionPool;
import org.cardvault.core.dependencyInjection.annotations.Injected;
import org.cardvault.core.logging.Logger;
import org.cardvault.user.data.UserDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserCollectionRepository {

    private SQLConnectionPool connectionPool;

    @Injected
    public void setSQLConnectionPool(SQLConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }


    public void addCardToCollection(UserDTO userDTO, int cardId) {
        String checkSql = "SELECT quantity FROM user_collection WHERE user_id = (SELECT id FROM users WHERE username = ?) AND card_id = ?";
        String insertSql = "INSERT INTO user_collection (user_id, card_id, quantity, first_acquired) VALUES ((SELECT id FROM users WHERE username = ?), ?, 1, CURRENT_TIMESTAMP)";
        String updateSql = "UPDATE user_collection SET quantity = quantity + 1 WHERE user_id = (SELECT id FROM users WHERE username = ?) AND card_id = ?";

        try (Connection conn = connectionPool.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, userDTO.username());
                checkStmt.setInt(2, cardId);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setString(1, userDTO.username());
                        updateStmt.setInt(2, cardId);
                        updateStmt.executeUpdate();
                    }
                } else {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, userDTO.username());
                        insertStmt.setInt(2, cardId);
                        insertStmt.executeUpdate();
                    }
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                Logger.error("Error adding card to collection: " + e.getMessage());
            }
        } catch (SQLException e) {
            Logger.error("Error adding card to collection: " + e.getMessage());
        }
    }

    public List<CardDOM> getUserCollection(String username) {
        String sql = "SELECT c.id, c.name, c.rarity, c.hp, c.dmg, c.collection, c.release_number, uc.quantity, uc.first_acquired " +
                "FROM user_collection uc " +
                "JOIN cards c ON uc.card_id = c.id " +
                "WHERE uc.user_id = (SELECT id FROM users WHERE username = ?)";
        List<CardDOM> userCollection = new ArrayList<>();

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                CardDOM card = new CardDOM(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("rarity"),
                        rs.getInt("hp"),
                        rs.getInt("dmg"),
                        rs.getString("collection"),
                        rs.getInt("release_number")
                );
                userCollection.add(card);
            }
        } catch (SQLException e) {
            Logger.error("Error fetching user collection: " + e.getMessage());
        }

        return userCollection;
    }
}
