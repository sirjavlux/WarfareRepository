package com.coding.sirjavlux.core;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class WeaponUseListener implements Listener {

	@EventHandler
	public void playerInteractEvent(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		Action action = e.getAction();
		ItemStack item = e.getItem();
		//check if weapon
		if (WeaponManager.isWeapon(item)) {
			
			///////////////////
			//RELOAD
			///////////////////
			
			if ((action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) && p.isSneaking()) {
				
			}
			
			///////////////////
			//SHOOT
			///////////////////
			
			else if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) {
				
			}
			
			///////////////////
			//SCOPE
			///////////////////
			
			else if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
				
			}
		}
	}
}
