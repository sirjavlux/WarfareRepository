package com.coding.sirjavlux.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.coding.sirjavlux.types.Ammo;
import com.coding.sirjavlux.types.Magazine;
import com.coding.sirjavlux.types.Weapon;
import com.coding.sirjavlux.utils.inventoryHandler;

import net.minecraft.server.v1_15_R1.NBTTagCompound;

public class WeaponManager {

	protected static HashMap<String, Weapon> weaponStored = new HashMap<>();
	protected static HashMap<String, Magazine> magazineStored = new HashMap<>();
	protected static HashMap<String, Ammo> ammoStored = new HashMap<>();
	
	/*/////////////////////////
	 * WEAPON MANAGEMENT
	 */////////////////////////
	
	public static Ammo getStoredAmmo(String name) {
		return ammoStored.get(name);
	}
	
	public static boolean isAmmunition(String name) {
		if (ammoStored.containsKey(name)) return true; else return false;
	}
	
	public static Magazine getStoredMagazine(String name) {
		return magazineStored.get(name);
	}
	
	public static boolean isMagazine(String name) {
		if (magazineStored.containsKey(name)) return true; else return false;
	}
	
	public static boolean isMagazine(ItemStack item) {
		boolean mag = false;
		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
		if (NMSItem.hasTag()) {
			NBTTagCompound tagComp = NMSItem.getTag();
			if (tagComp.hasKey("name")) {
				if (magazineStored.containsKey(tagComp.getString("name"))) {
					mag = true;
				}
			}
		}
		return mag;
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
		
		//generate ammo slots in barrel and magazine
		Ammo preLoadRounds = weapon.getPreLoadAmmo();
		String roundName = preLoadRounds.getName();
		StringBuilder barrelRounds = new StringBuilder(roundName);
		for (int i = 1; i < weapon.getBarrelAmmoCap(); i++) barrelRounds.append("," + roundName);
		StringBuilder magazineRounds = null;
		if (weapon.requiresMagazine()) {
			magazineRounds = new StringBuilder(roundName);
			for (int i = 1; i < weapon.getDefaultMagazine().getAmmoCapasity(); i++) barrelRounds.append("," + roundName);	
		}
		
		//add nms tags
		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(wItem);
		NBTTagCompound tagComp = NMSItem.hasTag() ? NMSItem.getTag() : new NBTTagCompound();
		tagComp.setString("name", weapon.getName());
		tagComp.setString("mag", weapon.isLoadedByDefault() ? weapon.getDefaultMagazine().getName() : "none");
		tagComp.setInt("magAmmo", weapon.isLoadedByDefault() && weapon.requiresMagazine() ? weapon.getDefaultMagazine().getAmmoCapasity() : 0);
		tagComp.setBoolean("reqMag", weapon.requiresMagazine());
		tagComp.setInt("barrelAmmoCap", weapon.getBarrelAmmoCap());
		tagComp.setInt("barrelAmmo", weapon.isLoadedByDefault() ? (weapon.getBarrelAmmoCap() > 1 ? weapon.getBarrelAmmoCap() : 1) : 0);
		tagComp.setString("magRounds", magazineRounds == null ? "" : magazineRounds.toString());
		tagComp.setString("barrelRounds", barrelRounds.toString());
		tagComp.setString("uuid", UUID.randomUUID().toString());
		NMSItem.setTag(tagComp);
		wItem = CraftItemStack.asBukkitCopy(NMSItem);
		
		//update display data of item
		updateItem(wItem);
		
		//give item
		inventoryHandler.giveToPlayer(p, wItem, p.getLocation());
	}
	
	public static void givePlayerMagazine(Player p, Magazine mag, String magRounds) {
		ItemStack magItem = new ItemStack(mag.getMaterial());
		
		//add nms tags
		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(magItem);
		NBTTagCompound tagComp = NMSItem.hasTag() ? NMSItem.getTag() : new NBTTagCompound();
		tagComp.setString("name", mag.getName());
		tagComp.setString("rounds", magRounds);
		tagComp.setString("uuid", UUID.randomUUID().toString());
		NMSItem.setTag(tagComp);
		magItem = CraftItemStack.asBukkitCopy(NMSItem);
		
		//give item
		inventoryHandler.giveToPlayer(p, magItem, p.getLocation());
	}
	
	public static void updateItem(ItemStack item) {
		//weapon item
		if (isWeapon(item)) {
			ItemMeta meta = item.getItemMeta();
			
			//get nbt tag and item
			net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
			NBTTagCompound tagComp = NMSItem.getTag();
			Weapon weapon = getStoredWeapon(tagComp.getString("name"));
			int magAmmo = tagComp.getInt("magAmmo");
			
			//displayName
			String displayName = weapon.getDisplayName();
			displayName = ChatColor.translateAlternateColorCodes('&', displayName.replaceAll("[ammo]", String.valueOf(magAmmo)));
			meta.setDisplayName(displayName);
			
			//lore
			String[] lore = weapon.getLore();
			List<String> loreList = new ArrayList<>();
			for (int i = 0; i < lore.length; i++) {
				loreList.add(ChatColor.translateAlternateColorCodes('&', lore[i].replaceAll("[ammo]", String.valueOf(magAmmo))));
			}
			meta.setLore(loreList);
			
			item.setItemMeta(meta);
		} 
		//magazine item
		else if (isMagazine(item)) {
			ItemMeta meta = item.getItemMeta();
			
			//get nbt tag and item
			net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
			NBTTagCompound tagComp = NMSItem.getTag();
			Magazine mag = getStoredMagazine(tagComp.getString("name"));
			int ammo = tagComp.getString("rounds").split(",").length;
			
			//displayName
			String displayName = mag.getDisplayName();
			displayName = ChatColor.translateAlternateColorCodes('&', displayName.replaceAll("[ammo]", String.valueOf(ammo)));
			meta.setDisplayName(displayName);
			
			//lore
			String[] lore = mag.getLore();
			List<String> loreList = new ArrayList<>();
			for (int i = 0; i < lore.length; i++) {
				loreList.add(ChatColor.translateAlternateColorCodes('&', lore[i].replaceAll("[ammo]", String.valueOf(ammo))));
			}
			meta.setLore(loreList);
			
			item.setItemMeta(meta);
		}
	}
	
	public static void reduceAmmo(int amount, ItemStack item) {
		//if reducing weapon ammo
		if (isWeapon(item)) {
			//get nbt tag and item
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
				int magAmmo = tagComp.getInt("magAmmo");
				int barrelAmmo = tagComp.getInt("barrelAmmo");
				int barrelAmmoCap = tagComp.getInt("barrelAmmoCap");
				int barrelAmmoReduction = barrelAmmoCap > amount ? amount : barrelAmmoCap;
				String magRounds = tagComp.getString("magRounds");
				String barrelRounds = tagComp.getString("barrelRounds");
				
				//change bullets in weapon and mag
				//remove bullets from barrel
				int count = 0;
				for (int i = 0; i < amount; i++) {
					if (!barrelRounds.isEmpty()) {
						String lastBulletInBarrel = barrelRounds.contains(",") ? barrelRounds.substring(barrelRounds.lastIndexOf(",")) : barrelRounds;
						barrelRounds = barrelRounds.substring(0, barrelRounds.length() - lastBulletInBarrel.length());
						count++;
					}
				}
				//remove bullets from mag and add to barrel
				if (magAmmo > 0) {
					for (int i = 0; i < count; i++) {
						//remove bullets from mag
						if (!magRounds.isEmpty()) {
							String lastBulletInMag = magRounds.contains(",") ? magRounds.substring(magRounds.lastIndexOf(",")) : magRounds;
							magRounds = magRounds.substring(0, magRounds.length() - lastBulletInMag.length());
							//add removed bullet to barrel
							barrelRounds = (lastBulletInMag.indexOf(0) == ',' ? lastBulletInMag.replace(",", "") : lastBulletInMag) + barrelRounds;
						}
					}
				}
				
				//change ammo number
				magAmmo = magAmmo - barrelAmmoReduction;
				if (magAmmo < 0) {
					barrelAmmo -= magAmmo * -1;
					barrelAmmo = barrelAmmo < 0 ? 0 : barrelAmmo;
					magAmmo = 0;
				} else {
					
				}
				
				tagComp.setString("magRounds", magRounds);
				tagComp.setString("barrelRounds", barrelRounds);
				tagComp.setInt("magAmmo", magAmmo);
				tagComp.setInt("barrelAmmo", barrelAmmo);
			}
			
			//set new tag
			NMSItem.setTag(tagComp);
			item = CraftItemStack.asBukkitCopy(NMSItem);
			//update item displays
			updateItem(item);
		} 
		//if reducing magazine ammo
		else if (isMagazine(item)) {
			
		}
	}
	
	public static void unloadMagazine(ItemStack item, Player p) {
		if (isWeapon(item)) {
			//get nbt tag and item
			net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
			NBTTagCompound tagComp = NMSItem.getTag();
			
			String magStr = tagComp.getString("mag");
			if (magStr != "none") {
				//if invalid magazine remove
				if (!isMagazine(magStr)) {
					p.sendMessage(ChatColor.RED + "Unloaded magazine have been removed from the files and is therefore not given back to you.");
				} 
				//if valid magazine
				else {
					givePlayerMagazine(p, getMagazine(magStr), tagComp.getString("magRounds"));
					tagComp.setString("magRounds", "");
					tagComp.setInt("magAmmo", 0);
				}
				tagComp.setString("mag", "none");
			}
			NMSItem.setTag(tagComp);
			item = CraftItemStack.asBukkitCopy(NMSItem);
			updateItem(item);
		}
	}
	
	private static boolean magazineMatching(Magazine tryMag, Magazine[] mags) {
		boolean works = false;
		for (Magazine mag : mags) {
			if (mag.getName().equals(tryMag.getName())) works = true;
		}
		return works;
	}
	
	public static void loadMagazine(ItemStack mag, ItemStack item, Player p) {
		//remove mag from inventory
		mag.setType(Material.AIR);
		
		//get nbt tag and item
		net.minecraft.server.v1_15_R1.ItemStack NMSMag = CraftItemStack.asNMSCopy(mag);
		NBTTagCompound tagCompM = NMSMag.getTag();
		Magazine magazine = getStoredMagazine(tagCompM.getString("name"));
		
		net.minecraft.server.v1_15_R1.ItemStack NMSWeapon = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tagCompW = NMSMag.getTag();
		Weapon weapon = getStoredWeapon(tagCompW.getString("name"));
		
		//check if usable mag
		if (magazineMatching(magazine, weapon.getMagazineRequired())) {
			//set new tags on weapon
			String magRounds = tagCompM.getString("rounds");
			tagCompW.setString("mag", magazine.getName());
			tagCompW.setString("magRounds", magRounds);
			tagCompW.setInt("magAmmo", magRounds.split(",").length);
			
			NMSWeapon.setTag(tagCompW);
			item = CraftItemStack.asBukkitCopy(NMSWeapon);
		}
		updateItem(item);
	}
}
