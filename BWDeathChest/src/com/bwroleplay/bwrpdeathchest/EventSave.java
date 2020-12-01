package com.bwroleplay.bwrpdeathchest;

import org.bukkit.inventory.PlayerInventory;

public class EventSave {
	public String cause;
	
	public EventSave(PlayerInventory pInv, String c) {
		cause = c;
	}
}
