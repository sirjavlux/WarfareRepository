package com.coding.sirjavlux.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.coding.sirjavlux.weapons.WeaponManager;

public class PlayerJoinQuitListener implements Listener {

	@EventHandler
	public void playerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		Inventory iv = p.getInventory();
		for (ItemStack item : iv.getContents()) {
			//if weapon
			if (WeaponManager.isWeapon(item)) {
				//load data and set to weapon item obj
				WeaponManager.loadWeaponData(item);
			} 
			//if magazine
			else if (WeaponManager.isMagazine(item)) {
				//load data and set to magazine item obj
				WeaponManager.loadMagazineData(item);
			}
		}
	}
	
	@EventHandler 
	public void playerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		Inventory iv = p.getInventory();
		int count = 0;
		for (ItemStack item : iv.getContents()) {
			//if weapon
			if (WeaponManager.isWeapon(item)) {
				//load data and set to weapon item obj
				ItemStack genItem = WeaponManager.saveWeaponData(item);
				iv.setItem(count, genItem);
			} 
			//if magazine
			else if (WeaponManager.isMagazine(item)) {
				//load data and set to magazine item obj
				ItemStack genItem = WeaponManager.saveMagazineData(item);
				iv.setItem(count, genItem);
			}
			count++;
		}
	}
}
