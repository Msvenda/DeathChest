package com.bwroleplay.bwrpdeathchest.listeners;

import com.bwroleplay.bwrpdeathchest.util.DeathChestDataLayer;
import com.bwroleplay.bwtime.TimeManagement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class OnDeathListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if(DeathChestDataLayer.getDataLayer().getDeathCauses().containsKey(event.getEntity().getUniqueId())) {
            //EntityDamageEvent e = deathCauses.get(((Player) event.getEntity()).getUniqueId());
            if(DeathChestDataLayer.getDataLayer().getWorlds().contains(event.getEntity().getLocation().getWorld().getUID())){
                String cause = DeathChestDataLayer.getDataLayer().getDeathCauses().get(((Player) event.getEntity()).getUniqueId());
                placeChest(event, cause);
                DeathChestDataLayer.getDataLayer().getDeathCauses().remove(((Player) event.getEntity()).getUniqueId());
                //disable drops
                event.getDrops().clear();
            }
        }
    }

    private void placeChest(PlayerDeathEvent event, String cause) {
        Player p = event.getEntity();
        PlayerInventory inv = p.getInventory();
        Location loc = p.getLocation();

        //do placement check
        if(!isOkToReplace(loc)) {
            loc = getNewLoc(loc);
        }

        //place chest
        if(loc.getBlock().getType().equals(Material.BEDROCK)) {
            return;
        }
        for(ItemStack i : loc.getBlock().getDrops()) {
            loc.getWorld().dropItem(loc, i);
        }
        loc.getBlock().setType(Material.CHEST);
        Chest chest = (Chest) loc.getBlock().getState();
        String pName = p.getDisplayName();
        ChatColor.stripColor(pName);
        pName = ChatColor.DARK_RED + pName;
        chest.setCustomName(pName);
        chest.update();
        Inventory chestInv = chest.getBlockInventory();


        //get dropped items (armor -> hotbar -> offhand -> inv)
        List<ItemStack> chestItems = new ArrayList<>();

        //armor slots
        for(ItemStack i : inv.getArmorContents()) {
            if(i != null) {
                chestItems.add(i);
            }
        }

        //offhand
        ItemStack offHandItem = inv.getItemInOffHand();
        if(offHandItem != null) {
            chestItems.add(offHandItem);
        }

        //hotbar
        for(int i = 0; i < 9; i++) {
            if(inv.getItem(i) != null) {
                chestItems.add(inv.getItem(i));
            }
        }

        //inventory
        for(int i = 35; i > 8; i--) {
            if(inv.getItem(i) != null) {
                chestItems.add(inv.getItem(i));
            }
        }

        //put items in inv, get list of excess items
        ArrayList<ItemStack> droppedItems = new ArrayList<>();
        for(ItemStack chestItem : chestItems){
            droppedItems.addAll(chestInv.addItem(chestItem).values());
        }

        //drop excess items
        for(ItemStack i : droppedItems) {
            loc.getWorld().dropItem(loc, i);
        }

        //place sign
        loc.setY(loc.getY()+1);

        for(ItemStack i : loc.getBlock().getDrops()) {
            loc.getWorld().dropItem(loc, i);
        }
        loc.getBlock().setType(Material.SIGN_POST);
        if(loc.getBlock().getType().equals(Material.SIGN_POST)){
            TimeManagement timePlugin = (com.bwroleplay.bwtime.TimeManagement) JavaPlugin.getPlugin(TimeManagement.class);

            Sign s = (Sign) loc.getBlock().getState();
            s.setLine(0, p.getDisplayName());
            s.setLine(1, "Died to " + cause);
            s.setLine(2, "Died on:");
            String l3 = String.format("%s of %d",
                    timePlugin.getDataLayer().getServerTime().getMonthByName(),
                    timePlugin.getDataLayer().getServerTime().getYear(),
                    timePlugin);
            s.setLine(3, l3);
            s.update();
        }
        else{
            Bukkit.getLogger().info("Error, sign not found");
        }
    }


    private Location getNewLoc(Location loc) {
        Location returnLoc = loc;
        for(int y = -3; y < 4; y++) {
            loc.setY(loc.getY()+y);
            for(int x = -3; x < 4; x++) {
                loc.setX(loc.getX()+x);
                for(int z = -3; z < 4; z++) {
                    loc.setZ(loc.getZ()+z);
                    if(isOkToReplace(loc)) {
                        returnLoc = loc;
                        loc.setY(loc.getY()+1);
                        if(isOkToReplace(loc)) {
                            return loc;
                        }
                        loc.setY(loc.getY()-1);
                    }
                    loc.setZ(loc.getZ()-z);
                }
                loc.setX(loc.getX()-x);
            }
            loc.setY(loc.getY()-y);
        }
        return returnLoc;
    }

    private boolean isOkToReplace(Location loc) {
        Material mat = loc.getBlock().getType();
        if(mat.isSolid()) {
            return false;
        }
        return true;
    }
}
