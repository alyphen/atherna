package com.seventh_root.atherna.classes;

import com.seventh_root.atherna.Atherna;
import com.seventh_root.atherna.character.AthernaCharacter;
import com.seventh_root.atherna.player.AthernaPlayer;
import com.seventh_root.atherna.stat.AthernaStat;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.logging.Level.SEVERE;
import static java.util.stream.Collectors.toList;

public class AthernaClassManager {

    private Atherna plugin;
    private List<AthernaClass> classes;

    public AthernaClassManager(Atherna plugin) {
        this.plugin = plugin;
        classes = new ArrayList<>();
        Connection connection = plugin.getDatabaseConnection();
        if (connection != null) {
            try {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT id, name, max_level FROM atherna_class"
                );
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    int classId = resultSet.getInt("id");
                    String className = resultSet.getString("name");
                    int maxLevel = resultSet.getInt("max_level");
                    Map<AthernaStat, Map<Integer, Integer>> statValues = new HashMap<>();
                    PreparedStatement statsStatement = connection.prepareStatement(
                            "SELECT class_id, stat_id, level, value FROM atherna_class_stat WHERE class_id = ?"
                    );
                    statsStatement.setInt(1, classId);
                    ResultSet statsResultSet = statsStatement.executeQuery();
                    while (statsResultSet.next()) {
                        AthernaStat stat = plugin.getStatManager().getById(statsResultSet.getInt("stat_id"));
                        int level = statsResultSet.getInt("level");
                        int value = statsResultSet.getInt("value");
                        if (!statValues.containsKey(stat)) {
                            statValues.put(stat, new HashMap<>());
                        }
                        statValues.get(stat).put(level, value);
                    }
                    classes.add(new AthernaClass(classId, className, statValues, maxLevel));
                }
            } catch (SQLException exception) {
                plugin.getLogger().log(SEVERE, "Failed to load one or more classes", exception);
            }
        } else {
            plugin.getLogger().log(SEVERE, "Database connection is not available. Cannot load classes.");
        }
    }

    public AthernaClass getById(int id) {
        List<AthernaClass> filteredClasses = getAthernaClasses().stream()
                .filter(athernaClass -> athernaClass.getId() == id)
                .collect(toList());
        if (filteredClasses.size() > 0) {
            return filteredClasses.get(0);
        }
        return null;
    }

    public AthernaClass getByName(String name) {
        List<AthernaClass> filteredClasses = getAthernaClasses().stream()
                .filter(athernaClass -> athernaClass.getName().equalsIgnoreCase(name))
                .collect(toList());
        if (filteredClasses.size() > 0) {
            return filteredClasses.get(0);
        }
        return null;
    }

    public int getTotalExperience(AthernaCharacter character, AthernaClass athernaClass) {
        try {
            Connection connection = plugin.getDatabaseConnection();
            if (connection != null) {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT experience FROM atherna_character_class_experience WHERE character_id = ? AND class_id = ?"
                );
                statement.setInt(1, character.getId());
                statement.setInt(2, athernaClass.getId());
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt("experience");
                }
            } else {
                plugin.getLogger().log(SEVERE, "Database connection is not available. Cannot get character class experience.");
            }
        } catch (SQLException exception) {
            plugin.getLogger().log(SEVERE, "Failed to retrieve experience for character", exception);
        }
        return 0;
    }

    public void setTotalExperience(AthernaCharacter character, AthernaClass athernaClass, int experience) {
        Connection connection = plugin.getDatabaseConnection();
        if (connection != null) {
            try {
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO atherna_character_class_experience(character_id, class_id, experience) " +
                                "VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE experience = VALUES(experience)"
                );
                statement.setInt(1, character.getId());
                statement.setInt(2, athernaClass.getId());
                statement.setInt(3, max(min(experience, getTotalExperienceForLevel(athernaClass.getMaxLevel())), 0));
                statement.executeUpdate();
                AthernaPlayer player = character.getPlayer();
                if (player != null) {
                    OfflinePlayer offlineBukkitPlayer = player.getBukkitPlayer();
                    if (offlineBukkitPlayer.isOnline()) {
                        updateExperience(offlineBukkitPlayer.getPlayer());
                    }
                }
            } catch (SQLException exception) {
                plugin.getLogger().log(SEVERE, "Failed to set experience for character", exception);
            }
        } else {
            plugin.getLogger().log(SEVERE, "Database connection is not available. Cannot set character class experience.");
        }
    }

    public int getLevel(AthernaCharacter character, AthernaClass athernaClass) {
        int level = 1;
        while (getTotalExperienceForLevel(level + 1) <= getTotalExperience(character, athernaClass))  {
            level += 1;
        }
        return level;
    }

    public void setLevel(AthernaCharacter character, AthernaClass athernaClass, int level) {
        setTotalExperience(character, athernaClass, getTotalExperienceForLevel(max(min(level, athernaClass.getMaxLevel()), 0)));
    }

    public int getTotalExperienceForLevel(int level) {
        return level * (level - 1) * 500;
    }

    public int getExperienceForLevel(int level) {
        return (level - 1) * 1000;
    }

    public int getExperienceTowardsNextLevel(AthernaCharacter character, AthernaClass athernaClass) {
        return getTotalExperience(character, athernaClass) - getTotalExperienceForLevel(getLevel(character, athernaClass));
    }

    public void updateExperience(Player bukkitPlayer) {
        if (bukkitPlayer != null) {
            AthernaPlayer player = plugin.getPlayerManager().getByBukkitPlayer(bukkitPlayer);
            if (player != null) {
                AthernaCharacter character = player.getActiveCharacter();
                if (character != null && character.getAthernaClass() != null) {
                    bukkitPlayer.setExp((float) getExperienceTowardsNextLevel(character, character.getAthernaClass()) / (float) getExperienceForLevel(getLevel(character, character.getAthernaClass()) + 1));
                    bukkitPlayer.setLevel(getLevel(character, character.getAthernaClass()));
                } else {
                    bukkitPlayer.setExp(0);
                    bukkitPlayer.setLevel(1);
                }
            }
        }
    }

    public List<AthernaClass> getAthernaClasses() {
        return classes;
    }

}
