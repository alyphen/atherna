package com.seventh_root.atherna.listener;

import com.seventh_root.atherna.Atherna;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private Atherna plugin;

    public PlayerJoinListener(Atherna plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getClassManager().updateExperience(event.getPlayer());
    }

}
