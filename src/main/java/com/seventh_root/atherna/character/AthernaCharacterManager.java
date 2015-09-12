package com.seventh_root.atherna.character;

import com.seventh_root.atherna.Atherna;
import com.seventh_root.atherna.player.AthernaPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.util.Collections.unmodifiableList;
import static java.util.logging.Level.SEVERE;

public class AthernaCharacterManager {

    private Atherna plugin;

    public AthernaCharacterManager(Atherna plugin) {
        this.plugin = plugin;
    }

    public AthernaCharacter getById(int id) {
        Connection connection = plugin.getDatabaseConnection();
        if (connection != null) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT id, name, age, gender, description, player_id, health, max_health, mana, " +
                            "max_mana, food_level, class_id, world, x, y, z, yaw, pitch, dead FROM atherna_character " +
                            "WHERE id = ? LIMIT 1"
            )) {
                statement.setInt(1, id);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return new AthernaCharacter.Builder(plugin)
                            .id(resultSet.getInt("id"))
                            .name(resultSet.getString("name"))
                            .age(resultSet.getInt("age"))
                            .gender(resultSet.getString("gender"))
                            .description(resultSet.getString("description"))
                            .playerId(resultSet.getInt("player_id"))
                            .health(resultSet.getDouble("health"))
                            .maxHealth(resultSet.getDouble("max_health"))
                            .mana(resultSet.getInt("mana"))
                            .maxMana(resultSet.getInt("max_mana"))
                            .foodLevel(resultSet.getInt("food_level"))
                            .athernaClassId(resultSet.getInt("class_id"))
                            .location(new Location(
                                    Bukkit.getWorld(resultSet.getString("world")),
                                    resultSet.getDouble("x"),
                                    resultSet.getDouble("y"),
                                    resultSet.getDouble("z"),
                                    resultSet.getFloat("yaw"),
                                    resultSet.getFloat("pitch")
                            ))
                            .dead(resultSet.getBoolean("dead"))
                            .build();
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        } else {
            plugin.getLogger().log(SEVERE, "Database connection is not available. Cannot load character.");
        }
        return null;
    }

    public List<AthernaCharacter> getByPlayer(AthernaPlayer player) {
        Connection connection = plugin.getDatabaseConnection();
        if (connection != null) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT id, name, age, gender, description, player_id, health, max_health, mana, " +
                            "max_mana, food_level, class_id, world, x, y, z, yaw, pitch, dead FROM atherna_character " +
                            "WHERE player_id = ?"
            )) {
                statement.setInt(1, player.getId());
                ResultSet resultSet = statement.executeQuery();
                List<AthernaCharacter> characters = new CopyOnWriteArrayList<>();
                while (resultSet.next()) {
                    characters.add(
                            new AthernaCharacter.Builder(plugin)
                                    .id(resultSet.getInt("id"))
                                    .name(resultSet.getString("name"))
                                    .age(resultSet.getInt("age"))
                                    .gender(resultSet.getString("gender"))
                                    .description(resultSet.getString("description"))
                                    .player(plugin.getPlayerManager().getById(resultSet.getInt("player_id")))
                                    .health(resultSet.getDouble("health"))
                                    .maxHealth(resultSet.getDouble("max_health"))
                                    .mana(resultSet.getInt("mana"))
                                    .maxMana(resultSet.getInt("max_mana"))
                                    .foodLevel(resultSet.getInt("food_level"))
                                    .athernaClass(plugin.getClassManager().getById(resultSet.getInt("class_id")))
                                    .location(new Location(
                                            Bukkit.getWorld(resultSet.getString("world")),
                                            resultSet.getDouble("x"),
                                            resultSet.getDouble("y"),
                                            resultSet.getDouble("z"),
                                            resultSet.getFloat("yaw"),
                                            resultSet.getFloat("pitch")
                                    ))
                                    .dead(resultSet.getBoolean("dead"))
                                    .build()
                    );
                }
                return unmodifiableList(characters);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        } else {
            plugin.getLogger().log(SEVERE, "Database connection is not available. Cannot load character.");
        }
        return null;
    }

    public AthernaCharacter createDefaultCharacter() {
        return new AthernaCharacter.Builder(plugin)
                .name(plugin.getConfig().getString("characters.defaults.name", "New Character"))
                .age(plugin.getConfig().getInt("characters.defaults.age", 21))
                .gender(plugin.getConfig().getString("characters.defaults.gender", "Unknown"))
                .description(plugin.getConfig().getString("characters.defaults.description", ""))
                .health(plugin.getConfig().getInt("characters.defaults.health", 20))
                .maxHealth(plugin.getConfig().getInt("characters.defaults.max-health", 20))
                .mana(plugin.getConfig().getInt("characters.defaults.mana", 20))
                .maxMana(plugin.getConfig().getInt("characters.defaults.max-mana", 20))
                .foodLevel(plugin.getConfig().getInt("characters.defaults.food-level", 20))
                .location(plugin.getServer().getWorlds().get(0).getSpawnLocation())
                .dead(false)
                .build();
    }

}
