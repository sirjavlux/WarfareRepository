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
	
	public static boolean isAmmunition(ItemStack item) {
		boolean ammo = false;
		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
		if (NMSItem.hasTag()) {
			NBTTagCompound tagComp = NMSItem.getTag();
			if (tagComp.hasKey("name")) {
				if (ammoStored.containsKey(tagComp.getString("name"))) {
					ammo = true;
				}
			}
		}
		return ammo;
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
	
	public static boolean isWeapon(String name) {
		if (weaponStored.containsKey(name)) return true; else return false;
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
		tagComp.setString("mag", weapon.isLoadedByDefault() && weapon.requiresMagazine() ? weapon.getDefaultMagazine().getName() : "none");
		tagComp.setInt("magAmmo", weapon.isLoadedByDefault() && weapon.requiresMagazine() ? weapon.getDefaultMagazine().getAmmoCapasity() : 0);
		tagComp.setBoolean("reqMag", weapon.requiresMagazine());
		tagComp.setInt("barrelAmmoCap", weapon.getBarrelAmmoCap());
		tagComp.setInt("barrelAmmo", weapon.isLoadedByDefault() ? (weapon.getBarrelAmmoCap() > 1 ? weapon.getBarrelAmmoCap() : 1) : 0);
		tagComp.setString("magRounds", magazineRounds == null ? "" : magazineRounds.toString());
		tagComp.setString("barrelRounds", barrelRounds.toString());
		tagComp.setString("uuid", UUID.randomUUID().toString());
		
		//attributes
		/*
		NBTTagList modifiers = new NBTTagList();
		NBTTagCompound speed = new NBTTagCompound();
		speed.setString("AttributeName", "generic.attackSpeed");
		speed.setString("Name", "generic.attackSpeed");
        speed.setDouble("Amount", weapon.getFireRate());
        speed.setInt("Operation", 0);
        speed.setInt("UUIDLeast", 894654);
        speed.setInt("UUIDMost", 2872);
        speed.setString("Slot", "mainhand");
		
        modifiers.add(speed);
        tagComp.set("AttributeModifiers", modifiers);
        */
		//set tags
		NMSItem.setTag(tagComp);
		wItem = CraftItemStack.asBukkitCopy(NMSItem);
		
		//update display data of item
		updateWeaponItem(wItem, p, 0);
		
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
		
		//update display data of item
		updateMagAmmoItem(magItem, p);
		
		//give item
		inventoryHandler.giveToPlayer(p, magItem, p.getLocation());
	}
	
	public static void givePlayerMagazine(Player p, Magazine mag) {
		ItemStack magItem = new ItemStack(mag.getMaterial());
		
		//add nms tags
		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(magItem);
		NBTTagCompound tagComp = NMSItem.hasTag() ? NMSItem.getTag() : new NBTTagCompound();
		tagComp.setString("name", mag.getName());
		tagComp.setString("rounds", "");
		tagComp.setString("uuid", UUID.randomUUID().toString());
		NMSItem.setTag(tagComp);
		magItem = CraftItemStack.asBukkitCopy(NMSItem);
		
		//update display data of item
		updateMagAmmoItem(magItem, p);
		
		//give item
		inventoryHandler.giveToPlayer(p, magItem, p.getLocation());
	}
	
	public static void giveAmmo(Player p, Ammo ammo, int amount) {
		ItemStack ammoItem = new ItemStack(ammo.getMaterial());
		int maxStack = ammo.getMaxStackSize();
		
		//add nms tags
		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(ammoItem);
		NBTTagCompound tagComp = NMSItem.hasTag() ? NMSItem.getTag() : new NBTTagCompound();
		tagComp.setString("name", ammo.getName());
		tagComp.setString("uuid", UUID.randomUUID().toString());
		NMSItem.setTag(tagComp);
		ammoItem = CraftItemStack.asBukkitCopy(NMSItem);
		
		//update display data of item
		updateMagAmmoItem(ammoItem, p);
		
		//give item
		while (amount > 0) {
			int amountToAdd = amount > maxStack ? maxStack : amount;
			amount -= amountToAdd;
			ammoItem.setAmount(amountToAdd);
			inventoryHandler.giveToPlayer(p, ammoItem, p.getLocation());
		}
	}
	
	public static void updateWeaponItem(ItemStack item, Player p, int slot) {
		//weapon item
		if (isWeapon(item)) {
			ItemMeta meta = item.getItemMeta();
			
			//get nbt tag and item
			net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
			NBTTagCompound tagComp = NMSItem.getTag();
			Weapon weapon = getStoredWeapon(tagComp.getString("name"));
			int magAmmo = tagComp.getBoolean("reqMag") ? tagComp.getInt("magAmmo") : tagComp.getInt("barrelAmmo");
			int maxAmmo = tagComp.getBoolean("magReq") ? 0 : tagComp.getInt("barrelAmmoCap");
			String magName = tagComp.getString("mag");
			if (isMagazine(magName)) {
				Magazine mag = getStoredMagazine(magName);
				maxAmmo = mag.getAmmoCapasity();
			}
			
			//displayName
			String displayName = weapon.getDisplayName();
			displayName = ChatColor.translateAlternateColorCodes('&', displayName.replaceAll("%ammo%", String.valueOf(magAmmo))
					.replaceAll("%max-ammo%", String.valueOf(maxAmmo)));
			meta.setDisplayName(displayName);
			
			//lore
			String[] lore = weapon.getLore();
			List<String> loreList = new ArrayList<>();
			for (int i = 0; i < lore.length; i++) {
				loreList.add(ChatColor.translateAlternateColorCodes('&', lore[i].replaceAll("%ammo%", String.valueOf(magAmmo))
						.replaceAll("%max-ammo%", String.valueOf(maxAmmo))));
			}
			meta.setLore(loreList);
			
			item.setItemMeta(meta);
			
			//update item
			p.getInventory().setItem(slot, item);
		} 
	}
	
	public static void updateMagAmmoItem(ItemStack item, Player p) {
		//magazine item
		if (isMagazine(item)) {
			ItemMeta meta = item.getItemMeta();
			
			//get nbt tag and item
			net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
			NBTTagCompound tagComp = NMSItem.getTag();
			Magazine mag = getStoredMagazine(tagComp.getString("name"));
			int ammo = tagComp.getString("rounds").split(",").length;
			int maxAmmo =  mag.getAmmoCapasity();
			
			//displayName
			String displayName = mag.getDisplayName();
			displayName = ChatColor.translateAlternateColorCodes('&', displayName.replaceAll("%ammo%", String.valueOf(ammo))
					.replaceAll("%max-ammo%", String.valueOf(maxAmmo)));
			meta.setDisplayName(displayName);
			
			//lore
			String[] lore = mag.getLore();
			List<String> loreList = new ArrayList<>();
			for (int i = 0; i < lore.length; i++) {
				loreList.add(ChatColor.translateAlternateColorCodes('&', lore[i].replaceAll("%ammo%", String.valueOf(ammo))
						.replaceAll("%max-ammo%", String.valueOf(maxAmmo))));
			}
			meta.setLore(loreList);
			
			item.setItemMeta(meta);
		}
		//ammo
		else if (isAmmunition(item)) {
			ItemMeta meta = item.getItemMeta();
			
			//get nbt tag and item
			net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
			NBTTagCompound tagComp = NMSItem.getTag();
			Ammo ammo = getStoredAmmo(tagComp.getString("name"));
			
			//displayName
			String displayName = ammo.getDisplayName();
			displayName = ChatColor.translateAlternateColorCodes('&', displayName);
			meta.setDisplayName(displayName);
			
			//lore
			String[] lore = ammo.getLore();
			List<String> loreList = new ArrayList<>();
			for (int i = 0; i < lore.length; i++) {
				loreList.add(ChatColor.translateAlternateColorCodes('&', lore[i]));
			}
			meta.setLore(loreList);
			
			item.setItemMeta(meta);
		}
	}
	
	public static void reduceAmmo(int amount, ItemStack item, Player p) {
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
			updateWeaponItem(item, p, p.getInventory().getHeldItemSlot());
		} 
		//if reducing magazine ammo
		else if (isMagazine(item)) {
			
		}
	}
	
	public static void unloadMagazine(ItemStack item, Player p, int slot) {
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
			updateWeaponItem(item, p, slot);
		}
	}
	
	private static boolean magazineMatching(Magazine tryMag, Magazine[] mags) {
		boolean works = false;
		for (Magazine mag : mags) {
			if (mag.getName().equals(tryMag.getName())) works = true;
		}
		return works;
	}
	
	public static void loadMagazine(ItemStack mag, ItemStack item, Player p, int slot) {
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
		updateWeaponItem(item, p, slot);
	}
}
