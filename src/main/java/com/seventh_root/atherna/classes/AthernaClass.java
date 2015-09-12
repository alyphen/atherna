package com.seventh_root.atherna.classes;

import com.seventh_root.atherna.stat.AthernaStat;

import java.util.HashMap;
import java.util.Map;

public class AthernaClass {

    private int id;
    private String name;
    private Map<Integer, Map<Integer, Integer>> statValues;
    private int maxLevel;

    public AthernaClass(int id, String name, Map<AthernaStat, Map<Integer, Integer>> statValues, int maxLevel) {
        this.id = id;
        this.name = name;
        this.statValues = new HashMap<>();
        statValues.forEach((stat, levelStatValues) -> levelStatValues.forEach((level, value) -> setStatValue(stat, level, value)));
        this.maxLevel = maxLevel;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getStatValue(AthernaStat stat, int level) {
        if (!statValues.containsKey(stat.getId())) {
            statValues.put(stat.getId(), new HashMap<>());
        }
        if (!statValues.get(stat.getId()).containsKey(level)) {
            if (level > 0) {
                statValues.get(stat.getId()).put(level, getStatValue(stat, level - 1));
            } else {
                statValues.get(stat.getId()).put(level, 0);
            }
        }
        return statValues.get(stat.getId()).get(level);
    }

    public void setStatValue(AthernaStat stat, int level, int value) {
        if (!statValues.containsKey(stat.getId())) {
            statValues.put(stat.getId(), new HashMap<>());
        }
        statValues.get(stat.getId()).put(level, value);
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

}
