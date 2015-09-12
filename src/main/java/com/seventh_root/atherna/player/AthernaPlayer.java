package com.seventh_root.atherna.player;

import com.seventh_root.atherna.Atherna;
import com.seventh_root.atherna.character.AthernaCharacter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.util.logging.Level.SEVERE;

public class AthernaPlayer {

    private final Atherna plugin;

    private int id;
    private UUID minecraftUUID;
    private int activeCharacterId;

    public static class Builder {

        private final Atherna plugin;

        private int id;
        private UUID minecraftUUID;
        private int activeCharacterId;

        public Builder(Atherna plugin) {
            this.plugin = plugin;
        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder bukkitPlayer(OfflinePlayer bukkitPlayer) {
            this.minecraftUUID = bukkitPlayer.getUniqueId();
            return this;
        }

        public Builder activeCharacter(AthernaCharacter activeCharacter) {
            this.activeCharacterId = activeCharacter.getId();
            return this;
        }

        public Builder activeCharacterId(int activeCharacterId) {
            this.activeCharacterId = activeCharacterId;
            return this;
        }

        public AthernaPlayer build() {
            if (activeCharacterId == 0) {
                activeCharacter(plugin.getCharacterManager().createDefaultCharacter());
            }
            if (id == 0) {
                return new AthernaPlayer(plugin, minecraftUUID, activeCharacterId);
            } else {
                return new AthernaPlayer(plugin, id, minecraftUUID, activeCharacterId);
            }
        }

    }

    private AthernaPlayer(Atherna plugin, int id, UUID minecraftUUID, int activeCharacterId) {
        this.plugin = plugin;
        this.id = id;
        this.minecraftUUID = minecraftUUID;
        this.activeCharacterId = activeCharacterId;
    }

    private AthernaPlayer(Atherna plugin, UUID minecraftUUID, int activeCharacterId) {
        this(plugin, 0, minecraftUUID, activeCharacterId);
        insert();
    }

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public OfflinePlayer getBukkitPlayer() {
        return Bukkit.getPlayer(minecraftUUID);
    }

    public void setBukkitPlayer(OfflinePlayer bukkitPlayer) {
        this.minecraftUUID = bukkitPlayer.getUniqueId();
        update();
    }

    public AthernaCharacter getActiveCharacter() {
        return plugin.getCharacterManager().getById(activeCharacterId);
    }

    public void setActiveCharacter(AthernaCharacter character) {
        OfflinePlayer offlineBukkitPlayer = getBukkitPlayer();
        if (offlineBukkitPlayer.isOnline()) {
            Player bukkitPlayer = offlineBukkitPlayer.getPlayer();
            AthernaCharacter oldCharacter = plugin.getCharacterManager().getById(activeCharacterId);
            oldCharacter.setLocation(bukkitPlayer.getLocation());
            oldCharacter.setMaxHealth(bukkitPlayer.getMaxHealth());
            oldCharacter.setHealth(bukkitPlayer.getHealth());
            this.activeCharacterId = character.getId();
            bukkitPlayer.teleport(character.getLocation());
            bukkitPlayer.setMaxHealth(character.getMaxHealth());
            bukkitPlayer.setHealth(character.getHealth());
        }
        update();
    }

    public void insert() {
        Connection connection = plugin.getDatabaseConnection();
        if (connection != null) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO atherna_player(minecraft_uuid, active_character_id) VALUES(?, ?)",
                    RETURN_GENERATED_KEYS
            )) {
                statement.setString(1, getBukkitPlayer().getUniqueId().toString());
                statement.setInt(2, getActiveCharacter().getId());
                statement.executeUpdate();
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    setId(generatedKeys.getInt(1));
                }
            } catch (SQLException exception) {
                plugin.getLogger().log(SEVERE, "An SQL exception occurred while attempting to create a player", exception);
            }
        } else {
            plugin.getLogger().log(SEVERE, "Database connection is not available. Cannot create player.");
        }
    }

    public void update() {
        Connection connection = plugin.getDatabaseConnection();
        if (connection != null) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "UPDATE atherna_player SET minecraft_uuid = ?, active_character_id = ? WHERE id = ?"
            )) {
                statement.setString(1, getBukkitPlayer().getUniqueId().toString());
                statement.setInt(2, getActiveCharacter().getId());
                statement.setInt(3, getId());
                statement.executeUpdate();
            } catch (SQLException exception) {
                plugin.getLogger().log(SEVERE, "An SQL exception occurred while attempting to update a player", exception);
            }
        } else {
            plugin.getLogger().log(SEVERE, "Database connection is not available. Cannot update player.");
        }
    }

    public void delete() {
        Connection connection = plugin.getDatabaseConnection();
        if (connection != null) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM atherna_player WHERE id = ?"
            )) {
                statement.setInt(1, getId());
                statement.executeUpdate();
            } catch (SQLException exception) {
                plugin.getLogger().log(SEVERE, "An SQL exception occurred while attempting to delete a player", exception);
            }
        } else {
            plugin.getLogger().log(SEVERE, "Database connection is not available. Cannot delete player.");
        }
    }

}
