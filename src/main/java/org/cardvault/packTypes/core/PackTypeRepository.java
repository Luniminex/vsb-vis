package org.cardvault.packTypes.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cardvault.packTypes.data.BuyPackDTO;
import org.cardvault.core.database.SQLConnectionPool;
import org.cardvault.core.dependencyInjection.annotations.Injected;
import org.cardvault.core.logging.Logger;
import org.cardvault.packTypes.data.PackTypeDOM;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class PackTypeRepository {
    private SQLConnectionPool connectionPool;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Injected
    public void setSQLConnectionPool(SQLConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public void loadUpPackTypes(final String path) {
        try {
            File file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource(path)).getFile());
            List<PackTypeDOM> packTypes = objectMapper.readValue(file, new TypeReference<List<PackTypeDOM>>() {
            });

            try (Connection conn = connectionPool.getConnection()) {
                for (PackTypeDOM packType : packTypes) {
                    String checkSql = "SELECT 1 FROM pack_types WHERE id = ? LIMIT 1";
                    try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                        checkStmt.setInt(1, packType.id());
                        ResultSet rs = checkStmt.executeQuery();
                        if (!rs.next()) {
                            String insertSql = "INSERT INTO pack_types (id, name, collection, price, cards_per_pack, common_chance, uncommon_chance, rare_chance, epic_chance, legendary_chance, mythic_chance) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                                insertStmt.setInt(1, packType.id());
                                insertStmt.setString(2, packType.name());
                                insertStmt.setString(3, packType.collection());
                                insertStmt.setInt(4, packType.price());
                                insertStmt.setInt(5, packType.cards_per_pack());
                                insertStmt.setFloat(6, packType.common_chance());
                                insertStmt.setFloat(7, packType.uncommon_chance());
                                insertStmt.setFloat(8, packType.rare_chance());
                                insertStmt.setFloat(9, packType.epic_chance());
                                insertStmt.setFloat(10, packType.legendary_chance());
                                insertStmt.setFloat(11, packType.mythic_chance());
                                insertStmt.executeUpdate();
                            }
                        }
                    }
                }
            }
        } catch (IOException | SQLException e) {
            Logger.error("Error loading pack types: " + e.getMessage());
        }
    }

    public PackTypeDOM getPackType(int id) {
        String sql = "SELECT * FROM pack_types WHERE id = ? LIMIT 1";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new PackTypeDOM(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("collection"),
                        rs.getInt("price"),
                        rs.getInt("cards_per_pack"),
                        rs.getFloat("common_chance"),
                        rs.getFloat("uncommon_chance"),
                        rs.getFloat("rare_chance"),
                        rs.getFloat("epic_chance"),
                        rs.getFloat("legendary_chance"),
                        rs.getFloat("mythic_chance")
                );
            }
            return null;
        } catch (SQLException e) {
            Logger.error("Error getting pack type: " + e.getMessage());
            return null;
        }
    }

    public boolean buyPack(String username, PackTypeDOM packType, BuyPackDTO buyPackDTO) {
        String selectSql = "SELECT id, quantity FROM user_packs WHERE user_id = (SELECT id FROM users WHERE username = ?) AND pack_type_id = ?";
        String insertSql = "INSERT INTO user_packs (user_id, pack_type_id, quantity) VALUES ((SELECT id FROM users WHERE username = ?), ?, ?)";
        String updateSql = "UPDATE user_packs SET quantity = quantity + ? WHERE id = ?";
        String updateCurrencySql = "UPDATE users SET currency = currency - ? WHERE username = ?";

        try (Connection conn = connectionPool.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql);
                 PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                 PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                 PreparedStatement updateCurrencyStmt = conn.prepareStatement(updateCurrencySql)) {

                selectStmt.setString(1, username);
                selectStmt.setInt(2, packType.id());
                ResultSet rs = selectStmt.executeQuery();

                if (rs.next()) {
                    int existingPackId = rs.getInt("id");
                    updateStmt.setInt(1, buyPackDTO.quantity());
                    updateStmt.setInt(2, existingPackId);
                    updateStmt.executeUpdate();
                } else {
                    insertStmt.setString(1, username);
                    insertStmt.setInt(2, packType.id());
                    insertStmt.setInt(3, buyPackDTO.quantity());
                    insertStmt.executeUpdate();
                }

                int totalCost = packType.price() * buyPackDTO.quantity();
                updateCurrencyStmt.setInt(1, totalCost);
                updateCurrencyStmt.setString(2, username);
                updateCurrencyStmt.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                Logger.error("Error buying pack: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            Logger.error("Error getting connection: " + e.getMessage());
            return false;
        }
    }

    public List<PackTypeDOM> getPacksByCollection(String collectionName) {
        String sql = "SELECT * FROM pack_types WHERE collection = ?";
        List<PackTypeDOM> packTypes = new ArrayList<>();

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, collectionName);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                PackTypeDOM packType = new PackTypeDOM(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("collection"),
                        rs.getInt("price"),
                        rs.getInt("cards_per_pack"),
                        rs.getFloat("common_chance"),
                        rs.getFloat("uncommon_chance"),
                        rs.getFloat("rare_chance"),
                        rs.getFloat("epic_chance"),
                        rs.getFloat("legendary_chance"),
                        rs.getFloat("mythic_chance")
                );
                packTypes.add(packType);
            }
        } catch (SQLException e) {
            Logger.error("Error getting packs by collection: " + e.getMessage());
        }

        return packTypes;
    }

    public Collection<PackTypeDOM> getAllPacks() {
        String sql = "SELECT * FROM pack_types";
        List<PackTypeDOM> packTypes = new ArrayList<>();

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                PackTypeDOM packType = new PackTypeDOM(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("collection"),
                        rs.getInt("price"),
                        rs.getInt("cards_per_pack"),
                        rs.getFloat("common_chance"),
                        rs.getFloat("uncommon_chance"),
                        rs.getFloat("rare_chance"),
                        rs.getFloat("epic_chance"),
                        rs.getFloat("legendary_chance"),
                        rs.getFloat("mythic_chance")
                );
                packTypes.add(packType);
            }
        } catch (SQLException e) {
            Logger.error("Error getting all packs: " + e.getMessage());
        }

        return packTypes;
    }
}
