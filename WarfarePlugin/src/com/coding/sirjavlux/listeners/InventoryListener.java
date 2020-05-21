package com.coding.sirjavlux.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.coding.sirjavlux.weapons.WeaponManager;

public class InventoryListener implements Listener {
	
	@EventHandler
	public void onOpenInventory(InventoryOpenEvent e) {
		Inventory iv = e.getInventory();
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
	public void onPickUpEvent(EntityPickupItemEvent e) {
		if (e.getEntity() instanceof Player) {
			Inventory iv = ((Player) e.getEntity()).getInventory();
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
	}
}
