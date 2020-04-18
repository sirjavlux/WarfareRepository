package com.coding.sirjavlux.listeners;

import java.util.UUID;

import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
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
}
