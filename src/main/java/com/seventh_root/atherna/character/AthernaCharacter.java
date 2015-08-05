package com.seventh_root.atherna.character;

import com.seventh_root.atherna.Atherna;
import com.seventh_root.atherna.player.AthernaPlayer;
import org.bukkit.Location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.INTEGER;
import static java.util.logging.Level.SEVERE;

public class AthernaCharacter {

    private final Atherna plugin;

    private int id;
    private String name;
    private boolean nameHidden;
    private int age;
    private boolean ageHidden;
    private String gender;
    private boolean genderHidden;
    private String description;
    private boolean descriptionHidden;
    private int playerId;
    private double health;
    private double maxHealth;
    private int mana;
    private int maxMana;
    private int foodLevel;
    private Location location;
    private boolean dead;

    public static class Builder {

        private final Atherna plugin;

        private int id;
        private String name;
        private boolean nameHidden;
        private int age;
        private boolean ageHidden;
        private String gender;
        private boolean genderHidden;
        private String description;
        private boolean descriptionHidden;
        private int playerId;
        private double health;
        private double maxHealth;
        private int mana;
        private int maxMana;
        private int foodLevel;
        private Location location;
        private boolean dead;

        public Builder(Atherna plugin) {
            this.plugin = plugin;
        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder nameHidden(boolean nameHidden) {
            this.nameHidden = nameHidden;
            return this;
        }

        public Builder age(int age) {
            this.age = age;
            return this;
        }

        public Builder ageHidden(boolean ageHidden){
            this.ageHidden = ageHidden;
            return this;
        }

        public Builder gender(String gender) {
            this.gender = gender;
            return this;
        }

        public Builder genderHidden(boolean genderHidden) {
            this.genderHidden = genderHidden;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder descriptionHidden(boolean descriptionHidden) {
            this.descriptionHidden = descriptionHidden;
            return this;
        }

        public Builder player(AthernaPlayer player) {
            this.playerId = player.getId();
            return this;
        }

        public Builder health(double health) {
            this.health = health;
            return this;
        }

        public Builder maxHealth(double maxHealth) {
            this.maxHealth = maxHealth;
            return this;
        }

        public Builder mana(int mana) {
            this.mana = mana;
            return this;
        }

        public Builder maxMana(int maxMana) {
            this.maxMana = maxMana;
            return this;
        }

        public Builder foodLevel(int foodLevel) {
            this.foodLevel = foodLevel;
            return this;
        }

        public Builder location(Location location) {
            this.location = location;
            return this;
        }

        public Builder dead(boolean dead) {
            this.dead = dead;
            return this;
        }

        public AthernaCharacter build() {
            if (id == 0) {
                return new AthernaCharacter(plugin, name, nameHidden, age, ageHidden, gender, genderHidden, description,
                        descriptionHidden, plugin.getPlayerManager().getById(playerId), health, maxHealth, mana,
                        maxMana, foodLevel, location, dead);
            } else {
                return new AthernaCharacter(plugin, id, name, nameHidden, age, ageHidden, gender, genderHidden,
                        description, descriptionHidden, plugin.getPlayerManager().getById(playerId), health, maxHealth,
                        mana, maxMana, foodLevel, location, dead);
            }
        }

    }

    private AthernaCharacter(Atherna plugin, int id, String name, boolean nameHidden, int age, boolean ageHidden,
                             String gender, boolean genderHidden, String description, boolean descriptionHidden,
                             AthernaPlayer player, double health, double maxHealth, int mana, int maxMana,
                             int foodLevel, Location location, boolean dead) {
        this.plugin = plugin;
        this.id = id;
        this.name = name;
        this.nameHidden = nameHidden;
        this.age = age;
        this.ageHidden = ageHidden;
        this.gender = gender;
        this.genderHidden = genderHidden;
        this.description = description;
        this.descriptionHidden = descriptionHidden;
        this.playerId = player.getId();
        this.health = health;
        this.maxHealth = maxHealth;
        this.mana = mana;
        this.maxMana = maxMana;
        this.foodLevel = foodLevel;
        this.location = location;
        this.dead = dead;
    }

    private AthernaCharacter(Atherna plugin, String name, boolean nameHidden, int age, boolean ageHidden, String gender,
                             boolean genderHidden, String description, boolean descriptionHidden, AthernaPlayer player,
                             double health, double maxHealth, int mana, int maxMana, int foodLevel, Location location,
                             boolean dead) {
        this(plugin, 0, name, nameHidden, age, ageHidden, gender, genderHidden, description, descriptionHidden, player,
                health, maxHealth, mana, maxMana, foodLevel, location, dead);
        insert();
    }

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        update();
    }

    public boolean isNameHidden() {
        return nameHidden;
    }

    public void setNameHidden(boolean nameHidden) {
        this.nameHidden = nameHidden;
        update();
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
        update();
    }

    public boolean isAgeHidden() {
        return ageHidden;
    }

    public void setAgeHidden(boolean ageHidden) {
        this.ageHidden = ageHidden;
        update();
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
        update();
    }

    public boolean isGenderHidden() {
        return genderHidden;
    }

    public void setGenderHidden(boolean genderHidden) {
        this.genderHidden = genderHidden;
        update();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        update();
    }

    public boolean isDescriptionHidden() {
        return descriptionHidden;
    }

    public void setDescriptionHidden(boolean descriptionHidden) {
        this.descriptionHidden = descriptionHidden;
        update();
    }

    public AthernaPlayer getPlayer() {
        return plugin.getPlayerManager().getById(playerId);
    }

    public void setPlayer(AthernaPlayer player) {
        this.playerId = player.getId();
        update();
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
        update();
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(double maxHealth) {
        this.maxHealth = maxHealth;
        update();
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
        update();
    }

    public int getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(int maxMana) {
        this.maxMana = maxMana;
        update();
    }

    public int getFoodLevel() {
        return foodLevel;
    }

    public void setFoodLevel(int foodLevel) {
        this.foodLevel = foodLevel;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
        update();
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
        update();
    }

    public void insert() {
        Connection connection = plugin.getDatabaseConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO atherna_character(name, name_hidden, age, age_hidden, gender, gender_hidden, " +
                        "description, description_hidden, player_id, health, max_health, mana, max_mana, food_level, " +
                        "world, x, y, z, yaw, pitch, dead) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                        "?, ?, ?, ?)",
                RETURN_GENERATED_KEYS
        )) {
            statement.setString(1, getName());
            statement.setBoolean(2, isNameHidden());
            statement.setInt(3, getAge());
            statement.setBoolean(4, isAgeHidden());
            statement.setString(5, getGender());
            statement.setBoolean(6, isGenderHidden());
            statement.setString(7, getDescription());
            statement.setBoolean(8, isDescriptionHidden());
            if (getPlayer() != null)
                statement.setInt(9, getPlayer().getId());
            else
                statement.setNull(9, INTEGER);
            statement.setDouble(10, getHealth());
            statement.setDouble(11, getMaxHealth());
            statement.setInt(12, getMana());
            statement.setInt(13, getMaxMana());
            statement.setInt(14, getFoodLevel());
            statement.setString(15, getLocation().getWorld().getName());
            statement.setDouble(16, getLocation().getX());
            statement.setDouble(17, getLocation().getY());
            statement.setDouble(18, getLocation().getZ());
            statement.setFloat(19, getLocation().getYaw());
            statement.setFloat(20, getLocation().getPitch());
            statement.setBoolean(21, isDead());
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                setId(generatedKeys.getInt(1));
            }
        } catch (SQLException exception) {
            plugin.getLogger().log(SEVERE, "An SQL exception occurred while attempting to create a character");
        }
    }

    public void update() {
        try (PreparedStatement statement = plugin.getDatabaseConnection().prepareStatement(
                "UPDATE atherna_character SET name = ?, name_hidden = ?, age = ?, age_hidden = ?, gender = ?, " +
                        "gender_hidden = ?, description = ?, description_hidden = ?, player_id = ?, health = ?, " +
                        "max_health = ?, mana = ?, max_mana = ?, food_level = ?, world = ?, x = ?, y = ?, z = ?, " +
                        "yaw = ?, pitch = ?, dead = ? WHERE id = ?"
        )) {
            statement.setString(1, getName());
            statement.setBoolean(2, isNameHidden());
            statement.setInt(3, getAge());
            statement.setBoolean(4, isAgeHidden());
            statement.setString(5, getGender());
            statement.setBoolean(6, isGenderHidden());
            statement.setString(7, getDescription());
            statement.setBoolean(8, isDescriptionHidden());
            if (getPlayer() != null)
                statement.setInt(9, getPlayer().getId());
            else
                statement.setNull(9, INTEGER);
            statement.setDouble(10, getHealth());
            statement.setDouble(11, getMaxHealth());
            statement.setInt(12, getMana());
            statement.setInt(13, getMaxMana());
            statement.setInt(14, getFoodLevel());
            statement.setString(15, getLocation().getWorld().getName());
            statement.setDouble(16, getLocation().getX());
            statement.setDouble(17, getLocation().getY());
            statement.setDouble(18, getLocation().getZ());
            statement.setFloat(19, getLocation().getYaw());
            statement.setFloat(20, getLocation().getPitch());
            statement.setBoolean(21, isDead());
            statement.setInt(22, getId());
            statement.executeUpdate();
        } catch (SQLException exception) {
            plugin.getLogger().log(SEVERE, "An SQL exception occurred while attempting to update a character");
        }
    }

    public void delete() {
        Connection connection = plugin.getDatabaseConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM atherna_character WHERE id = ?"
        )) {
            statement.setInt(1, getId());
            statement.executeUpdate();
            plugin.getCharacterManager().uncache(this);
        } catch (SQLException exception) {
            plugin.getLogger().log(SEVERE, "An SQL exception occurred while attempting to delete a character",
                    exception);
        }
    }

}
