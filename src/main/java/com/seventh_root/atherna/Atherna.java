package com.seventh_root.atherna;

import com.seventh_root.atherna.character.AthernaCharacterManager;
import com.seventh_root.atherna.command.CharacterCommand;
import com.seventh_root.atherna.player.AthernaPlayerManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static java.util.logging.Level.SEVERE;

public class Atherna extends JavaPlugin {

    private Connection databaseConnection;

    private AthernaCharacterManager characterManager;
    private AthernaPlayerManager playerManager;

    @Override
    public void onEnable() {
        try {
            databaseConnection = DriverManager.getConnection(
                    "jdbc:mysql://" + getConfig().getString("url") + "/" + getConfig().getString("database"),
                    getConfig().getString("username"),
                    getConfig().getString("password")
            );
        } catch (SQLException exception) {
            getLogger().log(SEVERE, "Failed to connect to database", exception);
        }
        characterManager = new AthernaCharacterManager(this);
        playerManager = new AthernaPlayerManager(this);
        getCommand("character").setExecutor(new CharacterCommand(this));
    }

    public Connection getDatabaseConnection() {
        return databaseConnection;
    }

    public AthernaCharacterManager getCharacterManager() {
        return characterManager;
    }

    public AthernaPlayerManager getPlayerManager() {
        return playerManager;
    }

}
