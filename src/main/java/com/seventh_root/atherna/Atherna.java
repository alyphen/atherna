package com.seventh_root.atherna;

import com.seventh_root.atherna.character.AthernaCharacterManager;
import com.seventh_root.atherna.classes.AthernaClassManager;
import com.seventh_root.atherna.command.CharacterCommand;
import com.seventh_root.atherna.command.ClassCommand;
import com.seventh_root.atherna.command.StatsCommand;
import com.seventh_root.atherna.listener.PlayerExpChangeListener;
import com.seventh_root.atherna.listener.PlayerJoinListener;
import com.seventh_root.atherna.player.AthernaPlayerManager;
import com.seventh_root.atherna.stat.AthernaStatManager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static java.util.logging.Level.SEVERE;

public class Atherna extends JavaPlugin {

    private Connection databaseConnection;

    private AthernaCharacterManager characterManager;
    private AthernaPlayerManager playerManager;
    private AthernaStatManager statManager;
    private AthernaClassManager classManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        try {
            databaseConnection = DriverManager.getConnection(
                    "jdbc:mysql://" + getConfig().getString("database.url") + "/" + getConfig().getString("database.database"),
                    getConfig().getString("database.user"),
                    getConfig().getString("database.password")
            );
        } catch (SQLException exception) {
            getLogger().log(SEVERE, "Failed to connect to database", exception);
        }
        characterManager = new AthernaCharacterManager(this);
        playerManager = new AthernaPlayerManager(this);
        statManager = new AthernaStatManager(this);
        classManager = new AthernaClassManager(this);
        getCommand("character").setExecutor(new CharacterCommand(this));
        getCommand("class").setExecutor(new ClassCommand(this));
        getCommand("stats").setExecutor(new StatsCommand(this));
        registerListeners(new PlayerExpChangeListener(), new PlayerJoinListener(this));
    }

    private void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
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

    public AthernaStatManager getStatManager() {
        return statManager;
    }

    public AthernaClassManager getClassManager() {
        return classManager;
    }

}
