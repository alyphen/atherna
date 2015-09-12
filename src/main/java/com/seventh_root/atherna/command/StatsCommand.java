package com.seventh_root.atherna.command;

import com.seventh_root.atherna.Atherna;
import com.seventh_root.atherna.character.AthernaCharacter;
import com.seventh_root.atherna.classes.AthernaClass;
import com.seventh_root.atherna.player.AthernaPlayer;
import com.seventh_root.atherna.stat.AthernaStat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.RED;

public class StatsCommand implements CommandExecutor {

    private Atherna plugin;

    public StatsCommand(Atherna plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player bukkitPlayer;
        if (args.length > 0) {
            String playerName = args[0];
            bukkitPlayer = plugin.getServer().getPlayer(playerName);
        } else {
            if (sender instanceof Player) {
                bukkitPlayer = (Player) sender;
            } else {
                sender.sendMessage(RED + "You are not a player, please specify a valid player name");
                return true;
            }
        }
        AthernaPlayer player = plugin.getPlayerManager().getByBukkitPlayer(bukkitPlayer);
        if (player != null) {
            AthernaCharacter character = player.getActiveCharacter();
            if (character != null) {
                AthernaClass athernaClass = character.getAthernaClass();
                if (athernaClass != null) {
                    sender.sendMessage(GOLD + character.getName() + "'s stats:");
                    List<AthernaStat> stats = plugin.getStatManager().getStats();
                    if (stats != null) {
                        for (AthernaStat stat : plugin.getStatManager().getStats()) {
                            sender.sendMessage(GRAY + stat.getName() + ": " +
                                    GOLD + athernaClass.getStatValue(
                                        stat,
                                        plugin.getClassManager().getLevel(character, athernaClass)
                                    )
                            );
                        }
                    } else {
                        if (bukkitPlayer == sender) {
                            sender.sendMessage(RED + "Could not retrieve your stats");
                        } else {
                            sender.sendMessage(RED + "Could not retrieve that player's stats");
                        }
                    }
                } else {
                    if (bukkitPlayer == sender) {
                        sender.sendMessage(RED + "You must set your class first. Use /class set [class]. To view classes, use /class list");
                    } else {
                        sender.sendMessage(RED + "That player has not set their class yet");
                    }
                }
            } else {
                if (bukkitPlayer == sender) {
                    sender.sendMessage(RED + "You do not currently have a character");
                } else {
                    sender.sendMessage(RED + "That player does not currently have a character");
                }
            }
        } else {
            if (bukkitPlayer == sender) {
                sender.sendMessage(RED + "You do not currently have an Atherna player associated");
            } else {
                sender.sendMessage(RED + "That player does not currently have an Atherna player associated");
            }
        }
        return true;
    }

}
