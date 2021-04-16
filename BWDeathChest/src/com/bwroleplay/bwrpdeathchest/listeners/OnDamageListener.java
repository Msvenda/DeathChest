package com.bwroleplay.bwrpdeathchest.listeners;

import com.bwroleplay.bwrpdeathchest.util.DeathChestDataLayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class OnDamageListener implements Listener {
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if(event.getEntityType().equals(EntityType.PLAYER)) {
            if(((Player) event.getEntity()).getHealth() <= event.getFinalDamage()) {
                String cause = modifyCause(event.getCause());
                //EventSave es = new EventSave(((Player) event.getEntity()).getInventory(), cause);
                DeathChestDataLayer.getDataLayer().getDeathCauses().put(((Player) event.getEntity()).getUniqueId(), cause);
                //placeChest(event, cause);
            }
        }
    }

    private String modifyCause(EntityDamageEvent.DamageCause damageCause) {
        String cause = "";
        if(damageCause.equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) || damageCause.equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)) {
            cause = "assault";
        }
        else if(damageCause.equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) || damageCause.equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
            cause = "explosion";
        }
        else if(damageCause.equals(EntityDamageEvent.DamageCause.FIRE_TICK) || damageCause.equals(EntityDamageEvent.DamageCause.FIRE)) {
            cause = "fire";
        }
        else if(damageCause.equals(EntityDamageEvent.DamageCause.FLY_INTO_WALL)) {
            cause = "physics";
        }
        else if(damageCause.equals(EntityDamageEvent.DamageCause.CUSTOM)) {
            cause = "gods";
        }
        else if(damageCause.equals(EntityDamageEvent.DamageCause.SUFFOCATION)) {
            cause = "suffocation";
        }
        else {
            cause = damageCause.toString();
            cause = cause.replace('_', ' ');
            cause = cause.toLowerCase();
        }
        return cause;
    }
}
