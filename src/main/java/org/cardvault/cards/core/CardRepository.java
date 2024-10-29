package org.cardvault.cards.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cardvault.cards.data.CardDOM;
import org.cardvault.core.database.SQLConnectionPool;
import org.cardvault.core.dependencyInjection.annotations.Injected;
import org.cardvault.core.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class CardRepository {

    private SQLConnectionPool connectionPool;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Injected
    public void setSQLConnectionPool(SQLConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public void loadUpCards(final String path) {
        try {
            // Locate the resource
            File file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource(path)).getFile());
            List<CardDOM> cards = objectMapper.readValue(file, new TypeReference<List<CardDOM>>() {});

            // Insert cards into the database if they do not exist
            try (Connection conn = connectionPool.getConnection()) {
                for (CardDOM card : cards) {
                    String checkSql = "SELECT 1 FROM cards WHERE id = ? LIMIT 1";
                    try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                        checkStmt.setInt(1, card.id());
                        ResultSet rs = checkStmt.executeQuery();
                        if (!rs.next()) {
                            String insertSql = "INSERT INTO cards (id, name, rarity, hp, dmg, collection, release_number) VALUES (?, ?, ?, ?, ?, ?, ?)";
                            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                                insertStmt.setInt(1, card.id());
                                insertStmt.setString(2, card.name());
                                insertStmt.setString(3, card.rarity());
                                insertStmt.setInt(4, card.hp());
                                insertStmt.setInt(5, card.dmg());
                                insertStmt.setString(6, card.collection());
                                insertStmt.setInt(7, card.releaseNumber());
                                insertStmt.executeUpdate();
                            }
                        }
                    }
                }
            }
        } catch (IOException | SQLException e) {
            Logger.error("Error loading cards: " + e.getMessage());
        }
    }
}
