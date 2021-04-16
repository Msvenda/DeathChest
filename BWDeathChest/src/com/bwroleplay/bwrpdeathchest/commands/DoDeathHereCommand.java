package com.bwroleplay.bwrpdeathchest.commands;

import com.bwroleplay.bwrpdeathchest.util.DeathChestDataLayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DoDeathHereCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 1) {
            return false;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players may use this command");
        }
        Player p = (Player) sender;
        boolean doWorld = true;
        if (args.length == 1) {
            doWorld = Boolean.parseBoolean(args[0]);
        }
        if (doWorld && !DeathChestDataLayer.getDataLayer().getWorlds().contains(p.getWorld().getUID())) {
            DeathChestDataLayer.getDataLayer().getWorlds().add(p.getWorld().getUID());
        } else {
            DeathChestDataLayer.getDataLayer().getWorlds().remove(p.getWorld().getUID());
        }
        sender.sendMessage(ChatColor.GRAY + "[Server -> ME]:  Do death chests in world: " + p.getWorld().getUID().toString() + ": " + doWorld);

        return true;
    }
}
