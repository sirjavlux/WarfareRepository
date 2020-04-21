package com.coding.sirjavlux.listeners;

import java.util.UUID;

import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.coding.sirjavlux.weapons.WeaponItem;
import com.coding.sirjavlux.weapons.WeaponManager;

import net.minecraft.server.v1_15_R1.NBTTagCompound;

public class ItemListener implements Listener {

	@EventHandler
	public void onItemDespawnEvent(ItemDespawnEvent e) {
		 
	}
	
	@EventHandler
	public void playerDropItem(PlayerDropItemEvent e) {
		Item drop = e.getItemDrop();
		ItemStack item = drop.getItemStack();
		if (WeaponManager.isWeapon(item)) {
			net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
			NBTTagCompound tagComp = NMSItem.getTag();
			UUID uuid = UUID.fromString(tagComp.getString("uuid"));
			WeaponItem weaponItem = WeaponManager.getWeaponItem(uuid);
			if (weaponItem != null) {
				weaponItem.hardUpdate(item);
				drop.setItemStack(WeaponManager.saveWeaponData(item));	
			}
		}
	}
	
	@EventHandler
	public void inventoryClickEvent(InventoryCreativeEvent e) {
		ItemStack cursor = e.getCursor();
		Inventory iv = e.getClickedInventory();
		int slot = e.getSlot();
		ItemStack[] contents = iv.getContents();
		
		if (iv != null) {
			//check middle click copy item
			if (cursor != null) {
				if (WeaponManager.isWeapon(cursor) || WeaponManager.isMagazine(cursor)) {
					ItemStack finalItem = cursor.clone();
					net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(finalItem);
					NBTTagCompound tagComp = NMSItem.getTag();
					UUID uuid = UUID.fromString(tagComp.getString("uuid"));
					for (int i = 0; i < contents.length; i++) {
						ItemStack cItem = contents[i];
						if (i != slot) {
							if (WeaponManager.isWeapon(cItem) || WeaponManager.isMagazine(cItem)) {
								net.minecraft.server.v1_15_R1.ItemStack NMSItemC = CraftItemStack.asNMSCopy(cItem);
								NBTTagCompound tagCompC = NMSItemC.getTag();
								UUID cUUID = UUID.fromString(tagCompC.getString("uuid"));
								if (cUUID.equals(uuid)) {
									System.out.println("contains ");
									uuid = WeaponManager.generateRandomSafeUUID();
									tagComp.setString("uuid", uuid.toString());
									NMSItem.setTag(tagComp);
									finalItem = CraftItemStack.asBukkitCopy(NMSItem);
									
									//load and set weapon
									if (WeaponManager.isWeapon(cursor)) {
										WeaponManager.loadWeaponData(finalItem);
									} else if (WeaponManager.isMagazine(cursor)) {
										WeaponManager.loadMagazineData(finalItem);
									}
									
									e.setCurrentItem(finalItem);
									e.setCancelled(true);
									return;
								}
							}
						}
					}	
				}
			}
		}
	}
}
