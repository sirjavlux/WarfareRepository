package com.coding.sirjavlux.core;

import java.util.HashMap;

import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.coding.sirjavlux.types.Ammo;
import com.coding.sirjavlux.types.Magazine;
import com.coding.sirjavlux.types.Weapon;

import net.minecraft.server.v1_15_R1.NBTTagCompound;

public class WeaponManager {

	protected static HashMap<String, Weapon> weaponStored = new HashMap<>();
	protected static HashMap<String, Magazine> magazineStored = new HashMap<>();
	protected static HashMap<String, Ammo> ammoStored = new HashMap<>();
	
	/*/////////////////////////
	 * WEAPON MANAGEMENT
	 */////////////////////////
	
	public static Magazine getStoredMagazine(String name) {
		return magazineStored.get(name);
	}
	
	public static boolean isMagazine(String name) {
		if (magazineStored.containsKey(name)) return true; else return false;
	}
	
	public static Weapon getStoredWeapon(String name) {
		return weaponStored.get(name);
	}
	
	public static Magazine getMagazine(String name) {
		return magazineStored.get(name);
	}
	
	public static boolean isWeapon(ItemStack item) {
		boolean weapon = false;
		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
		if (NMSItem.hasTag()) {
			NBTTagCompound tagComp = NMSItem.getTag();
			if (tagComp.hasKey("name")) {
				if (weaponStored.containsKey(tagComp.getString("name"))) {
					weapon = true;
				}
			}
		}
		return weapon;
	}
	
	public static void givePlayerWeapon(Player p, Weapon weapon) {
		ItemStack wItem = new ItemStack(weapon.getMat());
		
		//add nms tags
		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(wItem);
		NBTTagCompound tagComp = NMSItem.hasTag() ? NMSItem.getTag() : new NBTTagCompound();
		tagComp.setString("name", weapon.getName());
		tagComp.setString("mag", weapon.isLoadedByDefault() ? weapon.getDefaultMagazine().getName() : "none");
		tagComp.setInt("magAmmo", weapon.isLoadedByDefault() && weapon.requiresMagazine() ? weapon.getDefaultMagazine().getAmmoCapasity() : 0);
		tagComp.setBoolean("reqMag", weapon.requiresMagazine());
		tagComp.setInt("barrelAmmoCap", weapon.getBarrelAmmoCap());
		tagComp.setInt("barrelAmmo", weapon.isLoadedByDefault() ? (weapon.getBarrelAmmoCap() > 1 ? weapon.getBarrelAmmoCap() : 1) : 0);
		NMSItem.setTag(tagComp);
		wItem = CraftItemStack.asBukkitCopy(NMSItem);
		
		//update display data of item
		updateItem(wItem);
	}
	
	public static void updateItem(ItemStack item) {
		if (isWeapon(item)) {
			//ItemMeta meta = item.getItemMeta();
		}
	}
	
	public static void reduceAmmo(int amount, ItemStack item) {
		if (isWeapon(item)) {
			
			net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
			NBTTagCompound tagComp = NMSItem.getTag();
			Weapon weapon = getStoredWeapon(tagComp.getString("name"));
			
			//if integrated magazine
			if (!weapon.requiresMagazine()) {
				int ammo = tagComp.getInt("barrelAmmo");
				ammo -= ammo - amount < 0 ? ammo : amount;
				tagComp.setInt("barrelAmmo", ammo);
			} 
			//if requires magazine
			else {
				int ammo = tagComp.getInt("magAmmo");
				ammo -= ammo - amount < 0 ? ammo : amount;
				tagComp.setInt("magAmmo", ammo);
			}
			
			//set new tag
			NMSItem.setTag(tagComp);
			item = CraftItemStack.asBukkitCopy(NMSItem);
			//update item displays
			updateItem(item);
		}
	}
}
