package com.bwroleplay.bwrpdeathchest;

import com.bwroleplay.bwrpdeathchest.commands.DoDeathHereCommand;
import com.bwroleplay.bwrpdeathchest.listeners.OnDamageListener;
import com.bwroleplay.bwrpdeathchest.listeners.OnDeathListener;
import com.bwroleplay.bwrpdeathchest.util.DeathChestDataLayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DeathChest extends JavaPlugin implements Listener{

	private static final String propertiesFile = "plugins/BWDeathChest/config.yml";
	
	@Override
	public void onEnable(){

		getServer().getPluginManager().registerEvents(this, this);

		this.getServer().getPluginManager().registerEvents(new OnDeathListener(), this);
		this.getServer().getPluginManager().registerEvents(new OnDamageListener(), this);
		Bukkit.getPluginCommand("doDeathHere").setExecutor(new DoDeathHereCommand());

		List<UUID> worlds;

		File propFile = new File(propertiesFile);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(propFile);
		worlds = config.getStringList("worlds").stream().map(w -> UUID.fromString(w)).collect(Collectors.toList());

		DeathChestDataLayer deathChestDataLayer = DeathChestDataLayer.getDataLayer();
		deathChestDataLayer.setWorlds(worlds);
	}
	
	@Override
	public void onDisable(){
		//save properties file
		File propFile = new File(propertiesFile);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(propFile);
		config.set("worlds", DeathChestDataLayer.getDataLayer().getWorlds().stream().map(w -> w.toString()).collect(Collectors.toList()));
		try {
			config.save(propFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}
}
