package com.seventh_root.atherna.player;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.seventh_root.atherna.Atherna;
import com.seventh_root.atherna.character.AthernaCharacterManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.logging.Level.SEVERE;

public class AthernaPlayerManager {

    private Atherna plugin;

    public AthernaPlayerManager(Atherna plugin) {
        this.plugin = plugin;
    }

    private LoadingCache<Integer, AthernaPlayer> playerLoadingCache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterAccess(5, MINUTES)
            .build(new CacheLoader<Integer, AthernaPlayer>() {
                @Override
                public AthernaPlayer load(Integer id) {
                    Connection connection = plugin.getDatabaseConnection();
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
                                        .activeCharacter(plugin.getCharacterManager().getById(resultSet.getInt("active_character_id")))
                                        .build();
                            }
                        }
                    } catch (SQLException exception) {
                        plugin.getLogger().log(SEVERE, "An SQL exception occurred while attempting to retrieve a player", exception);
                    }
                    return null;
                }
            });

    private LoadingCache<String, AthernaPlayer> playerBukkitPlayerLoadingCache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterAccess(5, MINUTES)
            .build(new CacheLoader<String, AthernaPlayer>() {
                @Override
                public AthernaPlayer load(String bukkitPlayerUUID) {
                    Connection connection = plugin.getDatabaseConnection();
                    try (PreparedStatement statement  = connection.prepareStatement(
                            "SELECT id, minecraft_uuid, active_character_id FROM atherna_player WHERE minecraft_uuid = ? LIMIT 1"
                    )) {
                        statement.setString(1, bukkitPlayerUUID);
                        ResultSet resultSet = statement.executeQuery();
                        AthernaCharacterManager characterManager = plugin.getCharacterManager();
                        if (characterManager != null) {
                            if (resultSet.next()) {
                                return new AthernaPlayer.Builder(plugin)
                                        .id(resultSet.getInt("id"))
                                        .bukkitPlayer(Bukkit.getOfflinePlayer(UUID.fromString(resultSet.getString("minecraft_uuid"))))
                                        .activeCharacter(plugin.getCharacterManager().getById(resultSet.getInt("active_character_id")))
                                        .build();
                            } else {
                                return new AthernaPlayer.Builder(plugin)
                                        .bukkitPlayer(Bukkit.getOfflinePlayer(UUID.fromString(bukkitPlayerUUID)))
                                        .activeCharacter(plugin.getCharacterManager().createDefaultCharacter())
                                        .build();
                            }
                        }
                    } catch (SQLException exception) {
                        plugin.getLogger().log(SEVERE, "An SQL exception occurred while attempting to retrieve a player", exception);
                    }
                    return null;
                }
            });

    public AthernaPlayer getById(int id) {
        try {
            return playerLoadingCache.get(id);
        } catch (ExecutionException exception) {
            plugin.getLogger().log(SEVERE, "Failed to retrieve player from cache", exception);
        }
        return null;
    }

    public AthernaPlayer getByBukkitPlayer(OfflinePlayer bukkitPlayer) {
        try {
            return playerBukkitPlayerLoadingCache.get(bukkitPlayer.getUniqueId().toString());
        } catch (ExecutionException exception) {
            plugin.getLogger().log(SEVERE, "Failed to retrieve player from Bukkit player cache", exception);
        }
        return null;
    }

    public void uncache(AthernaPlayer player) {
        playerLoadingCache.invalidate(player.getId());
        playerBukkitPlayerLoadingCache.invalidate(player.getBukkitPlayer().getUniqueId().toString());
    }

}
