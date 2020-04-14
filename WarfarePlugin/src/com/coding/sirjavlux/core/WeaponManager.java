package com.coding.sirjavlux.core;

import java.io.File;
import java.util.HashMap;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.coding.sirjavlux.types.Magazine;
import com.coding.sirjavlux.types.Weapon;
import com.coding.sirjavlux.utils.FileManager;

import net.minecraft.server.v1_15_R1.NBTTagCompound;

public class WeaponManager extends Main {

	private static HashMap<String, Weapon> weaponStored = new HashMap<>();
	private static HashMap<String, Magazine> magazineStored = new HashMap<>();
	
	protected static void loadWeaponConfigs() {
		Plugin plugin = Main.getPlugin(Main.class);
		File weaponsFile = new File(plugin.getDataFolder() + "/weapons");
		
		//if not existing create and preload weapons
		if (!weaponsFile.exists()) {
			System.out.println("Loading weapon presets.");
			weaponsFile.mkdir();
			
			FileManager.writeFileFromResources(plugin, plugin.getDataFolder() + "/weapons/auto_weapon.yml", "auto_weapon.yml");
		}
		
		//loop trough files
		for (File file : weaponsFile.listFiles()) {
			FileConfiguration conf = YamlConfiguration.loadConfiguration(file);
			
			String name = conf.contains("name") ? conf.getString("name") : null; if (name == null) continue;
			
		}
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
