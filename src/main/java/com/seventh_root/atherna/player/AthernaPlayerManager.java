package com.seventh_root.atherna.player;

import com.seventh_root.atherna.Atherna;
import com.seventh_root.atherna.character.AthernaCharacter;
import com.seventh_root.atherna.character.AthernaCharacterManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static java.util.logging.Level.SEVERE;

public class AthernaPlayerManager {

    private Atherna plugin;

    public AthernaPlayerManager(Atherna plugin) {
        this.plugin = plugin;
    }

    public AthernaPlayer getById(int id) {
        Connection connection = plugin.getDatabaseConnection();
        if (connection != null) {
            try (PreparedStatement statement  = connection.prepareStatement(
                    "SELECT id, minecraft_uuid, active_character_id FROM atherna_player WHERE id = ? LIMIT 1"
            )) {
                statement.setInt(1, id);
                ResultSet resultSet = statement.executeQuery();
                AthernaCharacterManager characterManager = plugin.getCharacterManager();
                if (characterManager != null) {
                    if (resultSet.next()) {
                        return new AthernaPlayer.Builder(plugin)
                                .id(resultSet.getInt("id"))
                                .bukkitPlayer(Bukkit.getOfflinePlayer(UUID.fromString(resultSet.getString("minecraft_uuid"))))
                                .activeCharacterId(resultSet.getInt("active_character_id"))
                                .build();
                    }
                }
            } catch (SQLException exception) {
                plugin.getLogger().log(SEVERE, "An SQL exception occurred while attempting to retrieve a player", exception);
            }
        } else {
            plugin.getLogger().log(SEVERE, "Database connection is not available. Cannot load player.");
        }
        return null;
    }

    public AthernaPlayer getByBukkitPlayer(OfflinePlayer bukkitPlayer) {
        Connection connection = plugin.getDatabaseConnection();
        if (connection != null) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT id, minecraft_uuid, active_character_id FROM atherna_player WHERE minecraft_uuid = ? LIMIT 1"
            )) {
                statement.setString(1, bukkitPlayer.getUniqueId().toString());
                ResultSet resultSet = statement.executeQuery();
                AthernaCharacterManager characterManager = plugin.getCharacterManager();
                if (characterManager != null) {
                    if (resultSet.next()) {
                        return new AthernaPlayer.Builder(plugin)
                                .id(resultSet.getInt("id"))
                                .bukkitPlayer(Bukkit.getOfflinePlayer(UUID.fromString(resultSet.getString("minecraft_uuid"))))
                                .activeCharacterId(resultSet.getInt("active_character_id"))
                                .build();
                    } else {
                        AthernaCharacter character = plugin.getCharacterManager().createDefaultCharacter();
                        AthernaPlayer player = new AthernaPlayer.Builder(plugin)
                                .bukkitPlayer(Bukkit.getOfflinePlayer(UUID.fromString(bukkitPlayer.getUniqueId().toString())))
                                .activeCharacter(character)
                                .build();
                        character.setPlayer(player);
                        return player;
                    }
                }
            } catch (SQLException exception) {
                plugin.getLogger().log(SEVERE, "An SQL exception occurred while attempting to retrieve a player", exception);
            }
        } else {
            plugin.getLogger().log(SEVERE, "Database connection is not available. Cannot load player.");
        }
        return null;
    }

}
