package com.seventh_root.atherna.command;

import com.seventh_root.atherna.Atherna;
import com.seventh_root.atherna.character.AthernaCharacter;
import com.seventh_root.atherna.classes.AthernaClass;
import com.seventh_root.atherna.player.AthernaPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static java.lang.Integer.parseInt;
import static org.bukkit.ChatColor.*;

public class ClassCommand implements CommandExecutor {

    private Atherna plugin;

    public ClassCommand(Atherna plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("set")) {
                classSet(sender, args);
            } else if (args[0].equalsIgnoreCase("info")) {
                if (classInfo(sender, label, args)) return true;
            } else if (args[0].equalsIgnoreCase("list")) {
                classList(sender);
            } else if (args[0].equalsIgnoreCase("setlevel")) {
                if (classSetLevel(sender, label, args)) return true;
            } else if (args[0].equalsIgnoreCase("addexp")) {
                if (classAddExp(sender, label, args)) return true;
            } else {
                sender.sendMessage(RED + "Usage: /" + label + " [set|info|list" +
                        (sender.hasPermission("atherna.command.class.setlevel") ? "|setlevel" : "") +
                        (sender.hasPermission("atherna.command.class.addexp") ? "|addexp" : "") +
                        "]"
                );
            }
        } else {
            sender.sendMessage(RED + "Usage: /" + label + " [set|info|list" +
                            (sender.hasPermission("atherna.command.class.setlevel") ? "|setlevel" : "") +
                            (sender.hasPermission("atherna.command.class.addexp") ? "|addexp" : "") +
                            "]"
            );
        }
        return true;
    }

    private boolean classAddExp(CommandSender sender, String label, String[] args) {
        if (sender.hasPermission("atherna.command.class.addexp")) {
            Player bukkitPlayer = null;
            int experience;
            if (args.length > 2) {
                String playerName = args[1];
                bukkitPlayer = plugin.getServer().getPlayer(playerName);
                experience = parseInt(args[2]);
            } else if (args.length > 1) {
                experience = parseInt(args[1]);
            } else {
                return true;
            }
            if (bukkitPlayer == null) {
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
                        int initialExperience = plugin.getClassManager().getTotalExperience(character, athernaClass);
                        plugin.getClassManager().setTotalExperience(character, athernaClass,
                                plugin.getClassManager().getTotalExperience(character, athernaClass) + experience);
                        int finalExperience = plugin.getClassManager().getTotalExperience(character, athernaClass);
                        int experienceDiff = finalExperience - initialExperience;
                        sender.sendMessage(GREEN + "Gave " + character.getName() + " " + experienceDiff + " experience");
                    } else {
                        if (bukkitPlayer == sender) {
                            sender.sendMessage(RED + "You do not currently have a class. Set one with /" + label + " set [class]");
                        } else {
                            sender.sendMessage(RED + "That player does not currently have a class");
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
                    sender.sendMessage(RED + "That player currently does not have an Atherna player associated");
                }
            }
        } else {
            sender.sendMessage(RED + "You do not have permission to perform that command");
        }
        return false;
    }

    private boolean classSetLevel(CommandSender sender, String label, String[] args) {
        if (sender.hasPermission("atherna.command.class.setlevel")) {
            Player bukkitPlayer = null;
            int level;
            if (args.length > 2) {
                String playerName = args[1];
                bukkitPlayer = plugin.getServer().getPlayer(playerName);
                level = parseInt(args[2]);
            } else if (args.length > 1) {
                level = parseInt(args[1]);
            } else {
                return true;
            }
            if (bukkitPlayer == null) {
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
                        plugin.getClassManager().setLevel(character, athernaClass, level);
                        sender.sendMessage(GREEN + "Set " + character.getName() + "'s level to " +
                                plugin.getClassManager().getLevel(character, athernaClass));
                    } else {
                        if (bukkitPlayer == sender) {
                            sender.sendMessage(RED + "You do not currently have a class. Set one with /" + label + " set [class]");
                        } else {
                            sender.sendMessage(RED + "That player does not currently have a class");
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
                    sender.sendMessage(RED + "That player currently does not have an Atherna player associated");
                }
            }
        } else {
            sender.sendMessage(RED + "You do not have permission to perform that command");
        }
        return false;
    }

    private void classList(CommandSender sender) {
        sender.sendMessage(GOLD + "Class list:");
        plugin.getClassManager().getAthernaClasses().forEach(athernaClass -> sender.sendMessage(GRAY + " - " + athernaClass.getName()));
    }

    private boolean classInfo(CommandSender sender, String label, String[] args) {
        Player bukkitPlayer;
        if (args.length > 1) {
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
                AthernaClass currentClass = character.getAthernaClass();
                if (currentClass != null) {
                    sender.sendMessage(GRAY + "Current class: " + GOLD + currentClass.getName() + GRAY + " - " +
                            GOLD + "Lv" + plugin.getClassManager().getLevel(character, currentClass) +
                            (plugin.getClassManager().getLevel(character, currentClass) < currentClass.getMaxLevel() ? (
                                GRAY + " [ " +
                                GOLD + plugin.getClassManager().getExperienceTowardsNextLevel(character, currentClass) +
                                GRAY + " / " +
                                GOLD + plugin.getClassManager().getExperienceForLevel(plugin.getClassManager().getLevel(character, currentClass) + 1) +
                                GRAY + " exp ]"
                            ) : (GRAY + " [ " + GOLD + "MAX LEVEL" + GRAY + " ]"))
                    );
                } else {
                    if (bukkitPlayer == sender) {
                        sender.sendMessage(RED + "You do not currently have a class. Set one with /" + label + " set [class]");
                    } else {
                        sender.sendMessage(RED + "That player does not currently have a class");
                    }
                }
                sender.sendMessage(GRAY + "All classes levelled on this character: ");
                plugin.getClassManager().getAthernaClasses().stream().filter(
                        athernaClass -> plugin.getClassManager().getTotalExperience(character, athernaClass) > 0)
                        .forEach(athernaClass -> {
                            sender.sendMessage(GRAY + athernaClass.getName() + ": " +
                                    GOLD + "Lv" + plugin.getClassManager().getLevel(character, athernaClass) +
                                    (plugin.getClassManager().getLevel(character, athernaClass) < athernaClass.getMaxLevel() ? (
                                        GRAY + " [ " +
                                        GOLD + plugin.getClassManager().getExperienceTowardsNextLevel(character, athernaClass) +
                                        GRAY + " / " +
                                        GOLD + plugin.getClassManager().getExperienceForLevel(plugin.getClassManager().getLevel(character, athernaClass) + 1) +
                                        GRAY + " exp ]"
                                    ) : (GRAY + " [ " + GOLD + "MAX LEVEL" + GRAY + " ]"))
                                );
                        });
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
                sender.sendMessage(RED + "That player currently does not have an Atherna player associated");
            }
        }
        return false;
    }

    private void classSet(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player bukkitPlayer = (Player) sender;
            AthernaPlayer player = plugin.getPlayerManager().getByBukkitPlayer(bukkitPlayer);
            if (player != null) {
                AthernaCharacter character = player.getActiveCharacter();
                if (character != null) {
                    if (args.length > 1) {
                        AthernaClass athernaClass = plugin.getClassManager().getByName(args[1]);
                        if (athernaClass != null) {
                            character.setAthernaClass(athernaClass);
                            sender.sendMessage(GREEN + "Class set to " + athernaClass.getName());
                        } else {
                            sender.sendMessage(RED + "That class does not exist");
                        }
                    } else {
                        sender.sendMessage(RED + "You must specify a class");
                    }
                } else {
                    sender.sendMessage(RED + "You do not currently have a character");
                }
            } else {
                sender.sendMessage(RED + "You do not currently have a player");
            }
        } else {
            sender.sendMessage(RED + "You are not a player");
        }
    }

}
