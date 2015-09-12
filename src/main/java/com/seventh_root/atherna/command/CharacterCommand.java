package com.seventh_root.atherna.command;

import com.seventh_root.atherna.Atherna;
import com.seventh_root.atherna.character.AthernaCharacter;
import com.seventh_root.atherna.player.AthernaPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

import static org.bukkit.ChatColor.*;

public class CharacterCommand implements CommandExecutor {

    private final Atherna plugin;

    public CharacterCommand(Atherna plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("card") || args[0].equalsIgnoreCase("view")) {
                characterCard(sender, args);
            } else if (args[0].equalsIgnoreCase("set")) {
                characterSet(sender, label, args);
            } else if (args[0].equalsIgnoreCase("hide")) {
                characterHide(sender, label, args);
            } else if (args[0].equalsIgnoreCase("unhide")) {
                characterUnhide(sender, label, args);
            } else if (args[0].equalsIgnoreCase("new")) {
                characterNew(sender);
            } else if (args[0].equalsIgnoreCase("switch")) {
                characterSwitch(sender, args);
            } else if (args[0].equalsIgnoreCase("extenddescription")) {
                characterExtendDescription(sender, args);
            } else if (args[0].equalsIgnoreCase("list")) {
                characterList(sender);
            } else {
                sender.sendMessage(RED + "Usage: /" + label + " [card|set|hide|unhide|new|switch|extenddescription]");
            }
        } else {
            sender.sendMessage(RED + "Usage: /" + label + " [card|set|hide|unhide|new|switch|extenddescription]");
        }
        return true;
    }

    private void characterList(CommandSender sender) {
        if (sender instanceof Player) {
            Player bukkitPlayer = (Player) sender;
            AthernaPlayer player = plugin.getPlayerManager().getByBukkitPlayer(bukkitPlayer);
            if (player != null) {
                sender.sendMessage(GOLD + "Character list:");
                plugin.getCharacterManager().getByPlayer(player).forEach(character -> sender.sendMessage(GRAY + " - " + character.getName()));
            } else {
                sender.sendMessage(RED + "You do not currently have an Atherna player associated");
            }
        } else {
            sender.sendMessage(RED + "You must be a player to perform this command");
        }
    }

    private void characterSet(CommandSender sender, String label, String[] args) {
        if (sender instanceof Player) {
            Player bukkitPlayer = (Player) sender;
            AthernaPlayer player = plugin.getPlayerManager().getByBukkitPlayer(bukkitPlayer);
            AthernaCharacter character = player.getActiveCharacter();
            if (args.length > 1) {
                if (args[1].equalsIgnoreCase("name")) {
                    characterSetName(sender, args, character);
                } else if (args[1].equalsIgnoreCase("age")) {
                    characterSetAge(sender, args, character);
                } else if (args[1].equalsIgnoreCase("gender")) {
                    characterSetGender(sender, args, character);
                } else if (args[1].equalsIgnoreCase("description")) {
                    characterSetDescription(sender, args, character);
                } else if (args[1].equalsIgnoreCase("dead")) {
                    characterSetDead(sender, player, character);
                } else {
                    sender.sendMessage(RED + "Usage: /" + label + " set [name|age|gender|description|dead]");
                }
            } else {
                sender.sendMessage(RED + "Usage: /" + label + " set [name|age|gender|description|dead]");
            }
        } else {
            sender.sendMessage(RED + "You must be a player to perform this command");
        }
    }

    private void characterCard(CommandSender sender, String[] args) {
        Player bukkitPlayer = null;
        if (sender instanceof Player) {
            bukkitPlayer = (Player) sender;
        }
        if (args.length > 0) {
            Player argsPlayer = plugin.getServer().getPlayer(args[0]);
            if (argsPlayer != null) {
                bukkitPlayer = argsPlayer;
            }
        }
        if (bukkitPlayer != null) {
            AthernaPlayer player = plugin.getPlayerManager().getByBukkitPlayer(bukkitPlayer);
            AthernaCharacter character = player.getActiveCharacter();
            sender.sendMessage(GRAY + "=== " + GOLD + character.getName() + GRAY + " ===");
            sender.sendMessage(GRAY + "Age: " + GOLD + character.getAge());
            sender.sendMessage(GRAY + "Gender: " + GOLD + character.getGender());
            sender.sendMessage(GRAY + "Description: " + GOLD + character.getDescription());
        } else {
            sender.sendMessage(RED + "You must specify a player if using this command from console");
        }
    }

    private void characterSetName(CommandSender sender, String[] args, AthernaCharacter character) {
        if (args.length > 2) {
            StringBuilder nameBuilder = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                nameBuilder.append(args[i]).append(" ");
            }
            String name = nameBuilder.toString().substring(0, nameBuilder.length() - 1);
            character.setName(name);
            sender.sendMessage(GREEN + "Character name set to " + name);
        } else {
            sender.sendMessage(RED + "You must specify a name");
        }
    }

    private void characterSetAge(CommandSender sender, String[] args, AthernaCharacter character) {
        if (args.length > 2) {
            try {
                int age = Integer.parseInt(args[2]);
                int minimumAge = plugin.getConfig().getInt("characters.restrictions.age.minimum", 0);
                int maximumAge = plugin.getConfig().getInt("characters.restrictions.age.maximum", 1000);
                if (age >= minimumAge) {
                    if (age <= maximumAge) {
                        character.setAge(age);
                        sender.sendMessage(GREEN + "Character age set to " + age);
                    } else {
                        sender.sendMessage(RED + "You may not set an age greater than " + maximumAge);
                    }
                } else {
                    sender.sendMessage(RED + "You may not set an age smaller than " + minimumAge);
                }
            } catch (NumberFormatException exception) {
                sender.sendMessage(RED + "Age must be an integer");
            }
        } else {
            sender.sendMessage(RED + "You must specify an age");
        }
    }

    private void characterSetGender(CommandSender sender, String[] args, AthernaCharacter character) {
        if (args.length > 2) {
            List<String> acceptableGenders = plugin.getConfig().getStringList("characters.restrictions.gender");
            if (acceptableGenders.contains(args[2])) {
                character.setGender(args[2]);
                sender.sendMessage(GREEN + "Character gender set to " + args[2]);
            } else {
                sender.sendMessage(RED + "Gender must be one of the following (case-sensitive): ");
                for (String gender : acceptableGenders) {
                    sender.sendMessage(RED + " - " + gender);
                }
            }
        } else {
            sender.sendMessage(RED + "You must specify a gender");
        }
    }

    private void characterSetDescription(CommandSender sender, String[] args, AthernaCharacter character) {
        if (args.length > 2) {
            StringBuilder descriptionBuilder = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                descriptionBuilder.append(args[i]).append(" ");
            }
            String description = descriptionBuilder.toString();
            character.setDescription(description);
            sender.sendMessage(GREEN + "Character description set to \"" + description + "\"");
        } else {
            sender.sendMessage(RED + "You must specify a description");
        }
    }

    private void characterSetDead(CommandSender sender, AthernaPlayer player, AthernaCharacter character) {
        character.setDead(true);
        sender.sendMessage(GREEN + "Character set to dead.");
        AthernaCharacter newCharacter = plugin.getCharacterManager().createDefaultCharacter();
        newCharacter.setPlayer(player);
        player.setActiveCharacter(newCharacter);
    }

    private void characterHide(CommandSender sender, String label, String[] args) {
        if (sender instanceof Player) {
            Player bukkitPlayer = (Player) sender;
            AthernaPlayer player = plugin.getPlayerManager().getByBukkitPlayer(bukkitPlayer);
            AthernaCharacter character = player.getActiveCharacter();
            if (args.length > 1) {
                if (args[1].equalsIgnoreCase("name")) {
                    character.setNameHidden(true);
                    sender.sendMessage(GREEN + "Character name hidden");
                } else if (args[1].equalsIgnoreCase("age")) {
                    character.setAgeHidden(true);
                    sender.sendMessage(GREEN + "Character age hidden");
                } else if (args[1].equalsIgnoreCase("gender")) {
                    character.setGenderHidden(true);
                    sender.sendMessage(GREEN + "Character gender hidden");
                } else if (args[1].equalsIgnoreCase("description")) {
                    character.setDescriptionHidden(true);
                    sender.sendMessage(GREEN + "Character description hidden");
                } else {
                    sender.sendMessage(RED + "Usage: /" + label + " hide [name|age|gender|description]");
                }
            } else {
                sender.sendMessage(RED + "Usage: /" + label + " hide [name|age|gender|description]");
            }
        } else {
            sender.sendMessage(RED + "You must be a player to perform this command");
        }
    }

    private void characterUnhide(CommandSender sender, String label, String[] args) {
        if (sender instanceof Player) {
            Player bukkitPlayer = (Player) sender;
            AthernaPlayer player = plugin.getPlayerManager().getByBukkitPlayer(bukkitPlayer);
            AthernaCharacter character = player.getActiveCharacter();
            if (args.length > 1) {
                if (args[1].equalsIgnoreCase("name")) {
                    character.setNameHidden(false);
                    sender.sendMessage(GREEN + "Character name hidden");
                } else if (args[1].equalsIgnoreCase("age")) {
                    character.setAgeHidden(false);
                    sender.sendMessage(GREEN + "Character age hidden");
                } else if (args[1].equalsIgnoreCase("gender")) {
                    character.setGenderHidden(false);
                    sender.sendMessage(GREEN + "Character gender hidden");
                } else if (args[1].equalsIgnoreCase("description")) {
                    character.setDescriptionHidden(false);
                    sender.sendMessage(GREEN + "Character description hidden");
                } else {
                    sender.sendMessage(RED + "Usage: /" + label + " unhide [name|age|gender|description]");
                }
            } else {
                sender.sendMessage(RED + "Usage: /" + label + " unhide [name|age|gender|description]");
            }
        } else {
            sender.sendMessage(RED + "You must be a player to perform this command");
        }
    }

    private void characterNew(CommandSender sender) {
        if (sender instanceof Player) {
            Player bukkitPlayer = (Player) sender;
            AthernaPlayer player = plugin.getPlayerManager().getByBukkitPlayer(bukkitPlayer);
            AthernaCharacter character = plugin.getCharacterManager().createDefaultCharacter();
            character.setPlayer(player);
            sender.sendMessage(GREEN + "Created a new character");
        } else {
            sender.sendMessage(RED + "You must be a player to perform this command");
        }
    }

    private void characterSwitch(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player bukkitPlayer = (Player) sender;
            if (args.length > 1) {
                AthernaPlayer player = plugin.getPlayerManager().getByBukkitPlayer(bukkitPlayer);
                List<AthernaCharacter> characters = plugin.getCharacterManager().getByPlayer(player);
                StringBuilder searchTermBuilder = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    searchTermBuilder.append(args[i]);
                }
                List<AthernaCharacter> filteredCharacters = characters.stream()
                        .filter(character -> character.getName().toLowerCase().contains(
                                searchTermBuilder.toString().toLowerCase()
                        ))
                        .collect(Collectors.toList());
                if (filteredCharacters.size() == 1) {
                    AthernaCharacter character = filteredCharacters.get(0);
                    player.setActiveCharacter(character);
                    sender.sendMessage(GREEN + "Character switched to " + character.getName());
                } else if (filteredCharacters.size() == 0) {
                    sender.sendMessage(RED + "Could not find any characters by that name.");
                } else {
                    sender.sendMessage(RED + "Multiple characters by that name were found: ");
                    for (AthernaCharacter character : filteredCharacters) {
                        sender.sendMessage(RED + " - " + character.getName());
                    }
                    sender.sendMessage(RED + "Please be more specific!");
                }
            } else {
                sender.sendMessage(RED + "You must specify the name of the character");
            }
        } else {
            sender.sendMessage(RED + "You must be a player to perform this command");
        }
    }

    public void characterExtendDescription(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player bukkitPlayer = (Player) sender;
            AthernaPlayer player = plugin.getPlayerManager().getByBukkitPlayer(bukkitPlayer);
            if (player != null) {
                AthernaCharacter character = player.getActiveCharacter();
                if (character != null) {
                    if (args.length > 1) {
                        StringBuilder descriptionBuilder = new StringBuilder();
                        for (int i = 1; i < args.length; i++) {
                            descriptionBuilder.append(args[i]).append(" ");
                        }
                        String description = descriptionBuilder.toString();
                        character.setDescription(character.getDescription() + description);
                        sender.sendMessage(GREEN + "Character description set to \"" + character.getDescription() + "\"");
                    } else {
                        sender.sendMessage(RED + "You must specify a description");
                    }
                } else {
                    sender.sendMessage(RED + "You do not currently have a character");
                }
            } else {
                sender.sendMessage(RED + "You do not currently have an Atherna player associated");
            }
        } else {
            sender.sendMessage(RED + "You must be a player to perform that command");
        }
    }

}
