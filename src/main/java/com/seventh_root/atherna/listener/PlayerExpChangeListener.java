package com.seventh_root.atherna.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

import static org.bukkit.event.EventPriority.HIGHEST;

public class PlayerExpChangeListener implements Listener {

    @EventHandler(priority = HIGHEST)
    public void onPlayerExpChange(PlayerExpChangeEvent event) {
        event.setAmount(0);
    }

}
