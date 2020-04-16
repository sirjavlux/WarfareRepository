package com.coding.sirjavlux.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class inventoryHandler {
	public static void giveToPlayer(Player p, ItemStack item, Location bLoc) {
		Inventory pIv = p.getInventory();
	    //give items
	    if (hasEmptySlot(p, item)) {
	    	int amountOfItemInIv = getAmountOfItemInIv(p, item);
	    	int itemAmount = item.getAmount();
	    	pIv.addItem(item);
    		int amountOfItemInIvAfter = getAmountOfItemInIv(p, item);
    		int dropAmount = itemAmount - (amountOfItemInIvAfter - amountOfItemInIv);
    		if (dropAmount > 0) {
    			ItemStack itemClone = item.clone();
    			itemClone.setAmount(dropAmount);
    			p.getWorld().dropItemNaturally(bLoc, itemClone);
    		}	
	    } else {
	    	p.getWorld().dropItemNaturally(bLoc, item);
	    }
	}

	private static boolean hasEmptySlot(Player p, ItemStack mat) {
		
		boolean hasEmpty = false;
		for (ItemStack item : p.getInventory().getContents()) {
			if (item == null) {
				hasEmpty = true;
				break;
			} else if (item.getItemMeta().equals(mat.getItemMeta())){
				if (item.getAmount() < item.getMaxStackSize()) {
					hasEmpty = true;
					break;
				}
			}
		}
		return hasEmpty;
	}
	
	private static int getAmountOfItemInIv(Player p, ItemStack inputItem) {
		int amount = 0;
		for (ItemStack item : p.getInventory().getContents()) {
			if (item != null) {
				ItemMeta mat = inputItem.getItemMeta();
				if (mat.equals(item.getItemMeta())) {
					amount += item.getAmount();
				}
			}
		}
		return amount;
	}
}
