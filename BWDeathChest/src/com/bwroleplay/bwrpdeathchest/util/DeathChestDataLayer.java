package com.bwroleplay.bwrpdeathchest.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DeathChestDataLayer {
    private static DeathChestDataLayer singleton;
    public static DeathChestDataLayer getDataLayer(){
        if(singleton == null){
            singleton = new DeathChestDataLayer();
        }
        return singleton;
    }

    private List<UUID> worlds;

    private HashMap<UUID, String> deathCauses;

    private DeathChestDataLayer(){
        worlds = new ArrayList<>();
        deathCauses = new HashMap<>();
    }

    public List<UUID> getWorlds() {
        return worlds;
    }

    public void setWorlds(List<UUID> worlds) {
        this.worlds = worlds;
    }

    public HashMap<UUID, String> getDeathCauses() {
        return deathCauses;
    }

    public void setDeathCauses(HashMap<UUID, String> deathCauses) {
        this.deathCauses = deathCauses;
    }
}
