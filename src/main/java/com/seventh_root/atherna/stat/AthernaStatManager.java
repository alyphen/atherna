package com.seventh_root.atherna.stat;

import com.seventh_root.atherna.Atherna;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.util.logging.Level.SEVERE;

public class AthernaStatManager {

    private Atherna plugin;

    public AthernaStatManager(Atherna plugin) {
        this.plugin = plugin;
    }

    public AthernaStat getById(int id) {
        Connection connection = plugin.getDatabaseConnection();
        if (connection != null) {
            try {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT id, name FROM atherna_stat WHERE id = ? LIMIT 1"
                );
                statement.setInt(1, id);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return new AthernaStat(resultSet.getInt("id"), resultSet.getString("name"));
                }
            } catch (SQLException exception) {
                plugin.getLogger().log(SEVERE, "An SQL exception occurred while attempting to retrieve a stat", exception);
            }
        } else {
            plugin.getLogger().log(SEVERE, "Database connection is not available. Cannot load stat.");
        }
        return null;
    }

    public List<AthernaStat> getStats() {
        Connection connection = plugin.getDatabaseConnection();
        if (connection != null) {
            try {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT id, name FROM atherna_stat"
                );
                ResultSet resultSet = statement.executeQuery();
                List<AthernaStat> stats = new ArrayList<>();
                while (resultSet.next()) {
                    stats.add(new AthernaStat(resultSet.getInt("id"), resultSet.getString("name")));
                }
                return stats;
            } catch (SQLException exception) {
                plugin.getLogger().log(SEVERE, "An SQL exception occurred while attempting to retrieve a stat", exception);
            }
        } else {
            plugin.getLogger().log(SEVERE, "Database connection is not available. Cannot load stat.");
        }
        return null;
    }
}
