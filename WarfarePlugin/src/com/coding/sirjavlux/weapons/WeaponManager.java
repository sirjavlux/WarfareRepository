package com.coding.sirjavlux.weapons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
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
	
	private static HashMap<UUID, WeaponItem> weaponItems = new HashMap<>();
	private static HashMap<UUID, MagazineItem> magazineItems = new HashMap<>();
	
	/*/////////////////////////
	 * WEAPON MANAGEMENT
	 */////////////////////////
	
	public static WeaponItem getWeaponItem(UUID uuid) {
		return weaponItems.containsKey(uuid) ? weaponItems.get(uuid) : null;
	}
	
	public static Ammo getStoredAmmo(String name) {
		return ammoStored.get(name);
	}
	
	public static boolean isAmmunition(String name) {
		return ammoStored.containsKey(name);
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
		return magazineStored.containsKey(name);
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
	
	public static boolean isWeapon(String name) {
		return weaponStored.containsKey(name);
	}
	
	public static boolean isWeapon(ItemStack item) {
		boolean weapon = false;
		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
		if (NMSItem.hasTag()) {
			NBTTagCompound tagComp = NMSItem.getTag();
			if (tagComp.hasKey("uuid")) {
				if (weaponItems.containsKey(UUID.fromString(tagComp.getString("uuid")))) {
					weapon = true;
				}
			}
		}
		return weapon;
	}
	
	public static UUID generateRandomSafeUUID() {
		UUID uuid = UUID.randomUUID();
		boolean safe = false;
		while (!safe) {
			if (!weaponItems.containsKey(uuid) && !magazineItems.containsKey(uuid)) {
				safe = true;
			} else {
				uuid = UUID.randomUUID();
			}
		}
		return uuid;
	}
	
	public static void givePlayerWeapon(Player p, Weapon weapon) {
		ItemStack wItem = new ItemStack(weapon.getMat());
		//add nms tags
		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(wItem);
		NBTTagCompound tagComp = NMSItem.hasTag() ? NMSItem.getTag() : new NBTTagCompound();
		UUID wUUID = generateRandomSafeUUID();
		tagComp.setString("uuid", wUUID.toString());
		//set tags
		NMSItem.setTag(tagComp);
		wItem = CraftItemStack.asBukkitCopy(NMSItem);
		//add weapon to stored weapons
		WeaponItem weaponItem = new WeaponItem(weapon, wUUID);
		weaponItems.put(wUUID, weaponItem);
		//update display data of item
		weaponItem.hardUpdate(wItem);
		
		//give item
		inventoryHandler.giveToPlayer(p, wItem, p.getLocation());
	}
	
	public static void givePlayerMagazine(Player p, Magazine mag) {
		ItemStack magItem = new ItemStack(mag.getMaterial());
		
		//add nms tags
		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(magItem);
		NBTTagCompound tagComp = NMSItem.hasTag() ? NMSItem.getTag() : new NBTTagCompound();
		UUID uuid = generateRandomSafeUUID();
		tagComp.setString("uuid", uuid.toString());
		NMSItem.setTag(tagComp);
		magItem = CraftItemStack.asBukkitCopy(NMSItem);
		//add item to map
		MagazineItem magazineItem = new MagazineItem(mag, uuid);
		magazineItems.put(uuid, magazineItem);
		//update display data of item
		magazineItem.update(magItem);
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
		updateAmmoItem(ammoItem, p);
		
		//give item
		while (amount > 0) {
			int amountToAdd = amount > maxStack ? maxStack : amount;
			amount -= amountToAdd;
			ammoItem.setAmount(amountToAdd);
			inventoryHandler.giveToPlayer(p, ammoItem, p.getLocation());
		}
	}
	
	public static void updateAmmoItem(ItemStack item, Player p) {
		if (isAmmunition(item)) {
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
}
