package com.coding.sirjavlux.utils;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import com.coding.sirjavlux.backpacks.Backpack;
import com.coding.sirjavlux.backpacks.BackpackManager;
import com.coding.sirjavlux.weapons.WeaponManager;

import net.minecraft.server.v1_15_R1.NBTTagCompound;

public class InventoryHandler {
	
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
		//save backpack items
		saveBackpackItems(p);
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
	
	public static void saveBackpackItems(Player p) {
		PlayerInventory iv = p.getInventory();
		ItemStack backpack = iv.getItemInOffHand();
		if (BackpackManager.isBackpack(backpack)) {
			Backpack pack = BackpackManager.getBackpackFromItem(backpack);
			int size = pack.getPackSpace();
			net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(backpack);
			NBTTagCompound tagComp = NMSItem.getTag();
			UUID uuid = UUID.fromString(tagComp.getString("uuid"));
			for (int i = 0; i < size; i++) {
				int bSlot = 8 + 18 - i;
				ItemStack updateItem = iv.getItem(bSlot);
				BackpackManager.updateItemInInstance(p, uuid, updateItem == null ? new ItemStack(Material.AIR) : updateItem.clone(), size - i - 1);
			}
		}
	}
}
