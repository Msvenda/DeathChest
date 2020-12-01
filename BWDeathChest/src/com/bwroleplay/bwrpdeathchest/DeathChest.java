package com.bwroleplay.bwrpdeathchest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import com.bwroleplay.bwtime.TimeManagement;

public class DeathChest extends JavaPlugin implements Listener{

	private static final String propertiesFile = "plugins/BWRPDeathChest/config.yml";
	private HashMap<UUID, String> deathCauses;
	private List<UUID> worlds;
	
	private TimeManagement timePlugin;
	
	@Override
	public void onEnable(){
		timePlugin = (com.bwroleplay.bwtime.TimeManagement) JavaPlugin.getPlugin(TimeManagement.class);
		getServer().getPluginManager().registerEvents(this, this);
		deathCauses = new HashMap<>();
		
		File propFile = new File(propertiesFile);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(propFile);
		worlds = config.getStringList("worlds").stream().map(w -> UUID.fromString(w)).collect(Collectors.toList());
		
	}
	
	@Override
	public void onDisable(){
		//save properties file
		File propFile = new File(propertiesFile);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(propFile);
		config.set("worlds", worlds.stream().map(w -> w.toString()).collect(Collectors.toList()));
		try {
			config.save(propFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//takes up to one argument
		if(cmd.getName().equalsIgnoreCase("doDeathHere")) {
			if(args.length > 1) {
				return false;
			}
			if(!(sender instanceof Player)) {
				sender.sendMessage("Only players may use this command");
			}
			Player p = (Player) sender;
			boolean doWorld = true;
			if(args.length == 1) {
				doWorld = Boolean.parseBoolean(args[0]);
			}
			if(doWorld && !worlds.contains(p.getWorld().getUID())) {
				worlds.add(p.getWorld().getUID());
			}
			else {
				worlds.remove(p.getWorld().getUID());
			}
			sender.sendMessage(ChatColor.GRAY + "[Server -> ME]:  Do death chests in world: " + p.getWorld().getUID().toString() + ": " + doWorld);
			
			return true;
		}
		return false;
	}
	
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event) {
		if(event.getEntityType().equals(EntityType.PLAYER)) {
			if(((Player) event.getEntity()).getHealth() <= event.getFinalDamage()) {
				String cause = modifyCause(event.getCause());
				//EventSave es = new EventSave(((Player) event.getEntity()).getInventory(), cause);
				deathCauses.put(((Player) event.getEntity()).getUniqueId(), cause);
				//placeChest(event, cause);
			}
		}
	}
	
	private String modifyCause(DamageCause damageCause) {
		String cause = "";
		if(damageCause.equals(DamageCause.ENTITY_ATTACK) || damageCause.equals(DamageCause.ENTITY_SWEEP_ATTACK)) {
			cause = "assault";
		}
		else if(damageCause.equals(DamageCause.BLOCK_EXPLOSION) || damageCause.equals(DamageCause.ENTITY_EXPLOSION)) {
			cause = "explosion";
		}
		else if(damageCause.equals(DamageCause.FIRE_TICK) || damageCause.equals(DamageCause.FIRE)) {
			cause = "fire";
		}
		else if(damageCause.equals(DamageCause.FLY_INTO_WALL)) {
			cause = "physics";
		}
		else if(damageCause.equals(DamageCause.CUSTOM)) {
			cause = "gods";
		}
		else if(damageCause.equals(DamageCause.SUFFOCATION)) {
			cause = "suffocation";
		}
		else {
			cause = damageCause.toString();
			cause = cause.replace('_', ' ');
			cause = cause.toLowerCase();
		}
		return cause;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		if(deathCauses.containsKey(event.getEntity().getUniqueId())) {
			//EntityDamageEvent e = deathCauses.get(((Player) event.getEntity()).getUniqueId());
			if(worlds.contains(event.getEntity().getLocation().getWorld().getUID())){
				String cause = deathCauses.get(((Player) event.getEntity()).getUniqueId());
				placeChest(event, cause);
				deathCauses.remove(((Player) event.getEntity()).getUniqueId());
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
				
				
				//fill chest (armor -> hotbar -> offhand -> inv)
				for(ItemStack i : inv.getArmorContents()) {
					if(i != null) {
						chestInv.addItem(i);
					}
				}
				
				{
					ItemStack i = inv.getItemInOffHand();
					if(i != null) {
						chestInv.addItem(i);
					}
				}
				
				for(int i = 0; i < 9; i++) {
					if(inv.getItem(i) != null) {
						chestInv.addItem(inv.getItem(i));
					}
				}
				
				ArrayList<ItemStack> droppedItems = new ArrayList<>();
				for(int i = 35; i > 8; i--) {
					if(inv.getItem(i) != null) {
						droppedItems.addAll(chestInv.addItem(inv.getItem(i)).values());
					}
				}
				for(ItemStack i : droppedItems) {
					loc.getWorld().dropItem(loc, i);
				}
				
				
				//place sign
				loc.setY(loc.getY()+1);
				
				for(ItemStack i : loc.getBlock().getDrops()) {
					loc.getWorld().dropItem(loc, i);
				}
				loc.getBlock().setType(Material.OAK_SIGN);
				
				Sign s = (Sign) loc.getBlock().getState();
				s.setLine(0, p.getDisplayName());
				s.setLine(1, "Died to " + cause);
				s.setLine(2, "Died on:");
				String l3 = String.format("%s of %d", timePlugin.getServerTime().getMonthByName(), timePlugin.getServerTime().getYear());
				s.setLine(3, l3);
				s.update();
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
