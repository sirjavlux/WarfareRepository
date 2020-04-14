package com.coding.sirjavlux.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.coding.sirjavlux.types.Magazine;
import com.coding.sirjavlux.types.Weapon;
import com.coding.sirjavlux.types.WeaponType;
import com.coding.sirjavlux.utils.Color;
import com.coding.sirjavlux.utils.FileManager;

import net.minecraft.server.v1_15_R1.NBTTagCompound;

public class WeaponManager extends Main {

	public static HashMap<String, Weapon> weaponStored = new HashMap<>();
	private static HashMap<String, Magazine> magazineStored = new HashMap<>();
	
	protected static void loadWeaponConfigs() {
		Plugin plugin = Main.getPlugin(Main.class);
		File weaponsFile = new File(plugin.getDataFolder() + "/weapons");
		
		//if not existing create and preload weapons
		if (!weaponsFile.exists()) {
			System.out.println("Loading weapon presets...");
			weaponsFile.mkdir();
			
			FileManager.writeFileFromResources(plugin, plugin.getDataFolder() + "/weapons/auto_weapon.yml", "auto_weapon.yml");
			FileManager.writeFileFromResources(plugin, plugin.getDataFolder() + "/weapons/sniper.yml", "sniper.yml");
		}
		
		//loop trough files
		List<String> goodWeapons = new ArrayList<>();
		List<String> badWeapons = new ArrayList<>();
		for (File file : weaponsFile.listFiles()) {
			FileConfiguration conf = YamlConfiguration.loadConfiguration(file);
			badWeapons.add(file.getName());
			
			//name
			String name = conf.contains("name") ? conf.getString("name") : null; 
			if (name == null) {
				System.out.println(Color.RED + "The weapon name in file " + file.getName() + " wasn't found!" + Color.RESET);
				System.out.println(Color.RED + "Add this to config 'name: <weapon-name>'" + Color.RESET);
				continue;
			}
			//weapon type
			WeaponType type = conf.contains("type") ? WeaponType.valueOf(conf.getString("type")) : null; 
			if (type == null) {
				System.out.println(Color.RED + "The weapon " + name + " didn't have any weapon type selected!" + Color.RESET);
				System.out.println(Color.RED + "Add this to config 'type: <Auto/SemiAuto/BoltAction>'" + Color.RESET);
				continue;
			}
			//mag required
			List<String> magReqList = conf.contains("magazine-required") ? conf.getStringList("magazine-required") : null;
			String[] magReqStr = null;
			Magazine[] magReq = null;
			if (magReqList != null) {
				magReqStr = new String[magReqList.size()];
				magReqStr = magReqList.toArray(magReqStr);
				List<Magazine> magList = new ArrayList<Magazine>();
				for (String str : magReqStr) {
					if (!isMagazine(str)) {
						System.out.println(Color.RED + "The selected required magazine " + magReqStr + " in the weapon " + name + " didn't exist!" + Color.RESET);
						continue;
					}
					magList.add(getStoredMagazine(str));
				}
				magReq = new Magazine[magReqStr.length];
				magReq = magList.toArray(magReq);
			}
			//material
			String matS = conf.contains("material") ? conf.getString("material") : null;
			if (matS == null) {
				System.out.println(Color.RED + "The weapon " + name + " didn't have any material selected!" + Color.RESET);
				System.out.println(Color.RED + "Add this to config 'material: <material>'" + Color.RESET);
				continue;
			} else if (Material.valueOf(matS.toUpperCase()) == null) {
				System.out.println(Color.RED + "The selected material " + matS + " in the weapon " + name + " didn't exist!" + Color.RESET);
				continue;
			}
			Material mat = Material.valueOf(matS.toUpperCase());
			//smoke offset
			List<Double> smokeOffsetList = conf.contains("smoke-offset") ? conf.getDoubleList("smoke-offset") : null;
			double[] smokeOffset = new double[] { 0d, 0d, 0d };
			if (smokeOffsetList != null) {
				if (smokeOffsetList.size() != 3) {
					System.out.println(Color.RED + "Invalid smoke offset in the weapon " + name + ", there can only be 3 numbers x, y, z!" + Color.RESET);
					continue;
				}
				smokeOffset[0] = smokeOffsetList.get(0);
				smokeOffset[1] = smokeOffsetList.get(1);
				smokeOffset[2] = smokeOffsetList.get(2);
			}
			//smoke enabled
			boolean smokeEnabled = conf.contains("smoke-enabled") ? conf.getBoolean("smoke-enabled") : (smokeOffsetList != null ? true : false);
			//smoke intensity
			double smokeIntensity = conf.contains("smoke-intensity") ? conf.getDouble("smoke-intensity") : 0;
			//damage
			double damage = conf.contains("damage") ? conf.getDouble("damage") : 0;
			//lore
			List<String> loreList = conf.contains("lore") ? conf.getStringList("lore") : new ArrayList<String>();
			if (loreList.isEmpty()) {
				loreList.add("&cLore not set!");
				loreList.add("&cAdd 'lore: - something'");
				loreList.add("&cin the weapon file");
			}
			String[] lore = new String[loreList.size()];
			lore = loreList.toArray(lore);
			//display name
			String displayName = conf.contains("display-name") ? conf.getString("display-name") : "&cDisplay name not set!";
			//default mag
			String defaultMagName = conf.contains("default-magazine") ? conf.getString("default-magazine") : null;
			Magazine defaultMag = null;
			if (defaultMagName != null) {
				if (!isMagazine(defaultMagName)) {
					System.out.println(Color.RED + "The selected default magazine " + defaultMagName + " in the weapon " + name + " didn't exist!" + Color.RESET);
					continue;
				}
				defaultMag = getStoredMagazine(defaultMagName);
			}
			//loaded by default
			boolean loadedByDefault = conf.contains("loaded-by-default") ? conf.getBoolean("loaded-by-default") : true;
			//requires mag
			boolean reqMag = conf.contains("requires-magazine") ? conf.getBoolean("requires-magazine") : (magReq != null ? true : false);
			//barrelAmmo
			int barrelAmmoCap = conf.contains("barrel-ammo-cap") ? conf.getInt("barrel-ammo-cap") : 1;
			if (reqMag && barrelAmmoCap > 1) barrelAmmoCap = 1;
			
			//create weapon
			Weapon weapon = new Weapon(type, mat, magReq, name, smokeOffset, smokeEnabled, smokeIntensity, damage, lore, displayName, defaultMag, loadedByDefault, reqMag, barrelAmmoCap);
			weaponStored.put(name, weapon);
			badWeapons.remove(file.getName());
			goodWeapons.add(file.getName());
		} 
		
		//print loaded weapons and finished message
		System.out.println("Weapon loaded!");
		System.out.print("Weapons: ");
		int count = 0;
		for (String str : goodWeapons) {
			System.out.print((count > 0 ? ", " : "") + Color.GREEN + str + Color.RESET);
			count++;
		}
		for (String str : badWeapons) {
			System.out.print((count > 0 ? ", " : "") + Color.RED + str + Color.RESET);
			count++;
		}
		System.out.print("\n");
	}
	
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
