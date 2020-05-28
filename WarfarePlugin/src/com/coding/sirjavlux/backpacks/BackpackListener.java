package com.coding.sirjavlux.backpacks;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class BackpackListener implements Listener {

	@EventHandler
	public void ivClickEvent(InventoryClickEvent e) {
		Inventory iv = e.getClickedInventory();
		if (iv != null) {
			int slot = e.getSlot();
			Player p = (Player) e.getWhoClicked();
			if (slot == 40) {
				ItemStack clicked = e.getCurrentItem();
				ItemStack cursor = e.getCursor();
				//if clicked is backpack
				if (BackpackManager.isBackpack(clicked)) {
					ItemStack item = BackpackManager.unloadBackPackItemsFromPlayer(p, clicked).clone();
					e.setCurrentItem(item);
				}
				//if cursor is backpack
				if (BackpackManager.isBackpack(cursor)) {
					BackpackManager.loadBackPackItemsToPlayer(p, cursor);
				}	
			} else if (slot > 8 && slot < 27) {
				int size = 0;
				if (BackpackManager.isBackpack(p.getInventory().getItemInOffHand())) {
					Backpack pack = BackpackManager.getBackpackFromItem(p.getInventory().getItemInOffHand());
					size = pack.getPackSpace();
				} else updateInventory(p);
				if (slot < 9 + 18 - size) {
					e.setCancelled(true);
					p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
				} else if (BackpackManager.isBackpack(e.getCursor())) {
					e.setCancelled(true);
					p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
				}
			}
		}
	}
	
	@EventHandler 
	public void swapHandItemsEvent(PlayerSwapHandItemsEvent e) {
		ItemStack off = e.getOffHandItem();
		ItemStack main = e.getMainHandItem();
		Player p = e.getPlayer();
		//if clicked is backpack
		if (BackpackManager.isBackpack(main)) {
			ItemStack item = BackpackManager.unloadBackPackItemsFromPlayer(p, main).clone();
			e.setMainHandItem(item);
		}
		//if cursor is backpack
		if (BackpackManager.isBackpack(off)) {
			BackpackManager.loadBackPackItemsToPlayer(p, off);
		}	
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		updateInventory(p);
	}
	
	public void updateInventory(Player p) {
		PlayerInventory iv = p.getInventory();
		ItemStack offHand = iv.getItemInOffHand();
		if (BackpackManager.isBackpack(offHand)) {
			Backpack pack = BackpackManager.getBackpackFromItem(offHand);
			int size = pack.getPackSpace();
			for (int i = 0; i < 18 - size; i++) {
				int slot = 9 + i;
				iv.setItem(slot, BackpackManager.getSlotBlockItem());
			}
		} else {
			for (int i = 0; i < 18; i++) {
				int slot = 9 + i;
				iv.setItem(slot, BackpackManager.getSlotBlockItem());
			}
		}
	}
}
