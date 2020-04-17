package com.coding.sirjavlux.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import com.coding.sirjavlux.types.Ammo;
import com.coding.sirjavlux.types.AmmoType;
import com.coding.sirjavlux.types.Magazine;
import com.coding.sirjavlux.types.Weapon;
import com.coding.sirjavlux.types.WeaponType;
import com.coding.sirjavlux.utils.Color;
import com.coding.sirjavlux.utils.FileManager;

public class WeaponLoader extends WeaponManager{

	/*////////////////////////
	 * LOADING WEAPONS
	 *////////////////////////
	
	public static void loadFiles() {
		Plugin plugin = Main.getPlugin(Main.class);
		
		//ammunition
		File ammoFile = new File(plugin.getDataFolder() + "/ammunition");
		//if not existing create and preload weapons
		if (!ammoFile.exists()) {
			System.out.println("Loading ammunition presets...");
			ammoFile.mkdir();
			
			FileManager.writeFileFromResources(plugin, plugin.getDataFolder() + "/ammunition/sniper_ammo.yml", "sniper_ammo.yml");
			FileManager.writeFileFromResources(plugin, plugin.getDataFolder() + "/ammunition/rifle_ammo.yml", "rifle_ammo.yml");
		}
		loadAmmunitionConfigs();
		
		//magazines
		File magsFile = new File(plugin.getDataFolder() + "/magazines");
		//if not existing create and preload magazines
		if (!magsFile.exists()) {
			System.out.println("Loading magazine presets...");
			magsFile.mkdir();
			
		}
		loadMagazineConfigs();
		
		//weapons
		File weaponsFile = new File(plugin.getDataFolder() + "/weapons");
		//if not existing create and preload weapons
		if (!weaponsFile.exists()) {
			System.out.println("Loading weapon presets...");
			weaponsFile.mkdir();
			
			FileManager.writeFileFromResources(plugin, plugin.getDataFolder() + "/weapons/auto_weapon.yml", "auto_weapon.yml");
			FileManager.writeFileFromResources(plugin, plugin.getDataFolder() + "/weapons/sniper.yml", "sniper.yml");
		}
		loadWeaponConfigs();
	}
	
	private static void loadWeaponConfigs() {	
		Plugin plugin = Main.getPlugin(Main.class);
		File weaponsFile = new File(plugin.getDataFolder() + "/weapons");

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
			//caliber
			String caliber = magReq != null ? magReq[0].getCaliber() : (conf.contains("barrel-caliber") ? conf.getString("barrel-caliber") : null);
			if (caliber == null) {
				System.out.println(Color.RED + "The weapon " + name + " didn't have any barrel caliber or magazine requirement set!" + Color.RESET);
				System.out.println(Color.RED + "Add this to config for integrated magazine 'barrel-caliber: <caliber>'" + Color.RESET);
				System.out.println(Color.RED + "Add this to config for use of magazines 'magazine-required: <- mag1>'" + Color.RESET);
				continue;
			}
			//fire rate
			double fireRate = conf.contains("fire-rate") ? conf.getDouble("fire-rate") : 1;
			//preload ammo
			String preLoadAmmoStr = conf.contains("pre-load-ammo") ? conf.getString("pre-load-ammo") : null;
			Ammo preLoadAmmo = null;
			if (loadedByDefault) {
				if (preLoadAmmoStr == null) {
					System.out.println(Color.RED + "The weapon " + name + " didn't have any pre load ammo selected!" + Color.RESET);
					System.out.println(Color.RED + "Add this to config 'pre-load-ammo: <ammo-name>'" + Color.RESET);
					continue;
				} else if (!WeaponManager.isAmmunition(preLoadAmmoStr)) {
					System.out.println(Color.RED + "The pre load ammunition " + preLoadAmmoStr + " in The weapon " + name + " didn't exist!" + Color.RESET);
					continue;
				}
				preLoadAmmo = WeaponManager.getStoredAmmo(preLoadAmmoStr);
			}
			if (preLoadAmmo != null) {
				if (!preLoadAmmo.getCaliber().equalsIgnoreCase(caliber)) {
					System.out.println("Preload ammo caliber " + preLoadAmmo.getCaliber() + " | weapon caliber " + caliber);
					System.out.println(Color.RED + "The pre load ammunition " + preLoadAmmoStr + " in The weapon " + name + " didn't have the same caliber as the weapon " + caliber + "!" + Color.RESET);
					continue;
				}
			}
			//burst amount
			int burstAmount = conf.contains("burst-amount") ? conf.getInt("burst-amount") : 3;
			//burst speed
			double burstSpeed = conf.contains("burst-speed") ? conf.getDouble("burst-speed") : 7;
			
			//create weapon
			Weapon weapon = new Weapon(type, mat, magReq, name, smokeOffset, smokeEnabled, smokeIntensity, damage, lore, displayName, defaultMag, loadedByDefault, reqMag, barrelAmmoCap, caliber, fireRate, preLoadAmmo, burstAmount, burstSpeed);
			weaponStored.put(name, weapon);
			badWeapons.remove(file.getName());
			goodWeapons.add(file.getName());
		} 
		
		//print loaded weapons and finished message
		printGoodBadFiles(goodWeapons, badWeapons, "Weapons");
	}
	
	/*////////////////////////
	 * LOADING MAGAZINES
	 *////////////////////////
	
	private static void loadMagazineConfigs() {
		Plugin plugin = Main.getPlugin(Main.class);
		File magsFile = new File(plugin.getDataFolder() + "/magazines");

		//loop trough files
		List<String> goodMags = new ArrayList<>();
		List<String> badMags = new ArrayList<>();
		for (File file : magsFile.listFiles()) {
			FileConfiguration conf = YamlConfiguration.loadConfiguration(file);
			badMags.add(file.getName());
			
			//name
			String name = conf.contains("name") ? conf.getString("name") : null; 
			if (name == null) {
				System.out.println(Color.RED + "The magazine name in file " + file.getName() + " wasn't found!" + Color.RESET);
				System.out.println(Color.RED + "Add this to config 'name: <magazine-name>'" + Color.RESET);
				continue;
			}
			//material
			String matS = conf.contains("material") ? conf.getString("material") : null;
			if (matS == null) {
				System.out.println(Color.RED + "The magazine " + name + " didn't have any material selected!" + Color.RESET);
				System.out.println(Color.RED + "Add this to config 'material: <material>'" + Color.RESET);
				continue;
			} else if (Material.valueOf(matS.toUpperCase()) == null) {
				System.out.println(Color.RED + "The selected material " + matS + " in the magazine " + name + " didn't exist!" + Color.RESET);
				continue;
			}
			Material mat = Material.valueOf(matS.toUpperCase());
			//caliber
			String caliber = conf.contains("caliber") ? conf.getString("caliber") : null;
			if (caliber == null) {
				System.out.println(Color.RED + "The caliber in the magazine " + name + " wasn't found!" + Color.RESET);
				System.out.println(Color.RED + "Add this to config 'caliber: <caliber>'" + Color.RESET);
				continue;
			}
			//ammo capasity
			int ammoCap = conf.contains("ammo-capasity") ? conf.getInt("ammo-capasity") : 0;
			if (!conf.contains("ammo-capasity")) {
				System.out.println(Color.RED + "The ammunition capasity in the magazine " + name + " wasn't found!" + Color.RESET);
				System.out.println(Color.RED + "Add this to config 'ammo-capasity: <amount>'" + Color.RESET);
				continue;
			}
			//display name
			String displayName = conf.contains("display-name") ? conf.getString("display-name") : "&cDisplay name not set!";
			//lore
			List<String> loreList = conf.contains("lore") ? conf.getStringList("lore") : new ArrayList<String>();
			if (loreList.isEmpty()) {
				loreList.add("&cLore not set!");
				loreList.add("&cAdd 'lore: - something'");
				loreList.add("&cin the ammunition file");
			}
			String[] lore = new String[loreList.size()];
			lore = loreList.toArray(lore);
			
			//add mag to map
			Magazine mag = new Magazine(caliber, ammoCap, name, mat, displayName, lore);
			WeaponManager.magazineStored.put(name, mag);
			
			badMags.remove(file.getName());
			goodMags.add(file.getName());
		}
		
		//print loaded magazines and finished message
		printGoodBadFiles(goodMags, badMags, "Magazines");
	}
	
	/*////////////////////////
	 * LOADING AMMUNITION
	 *////////////////////////
	
	private static void loadAmmunitionConfigs() {
		Plugin plugin = Main.getPlugin(Main.class);
		File ammoFile = new File(plugin.getDataFolder() + "/ammunition");

		//loop trough files
		List<String> goodAmmo = new ArrayList<>();
		List<String> badAmmo = new ArrayList<>();
		for (File file : ammoFile.listFiles()) {
			FileConfiguration conf = YamlConfiguration.loadConfiguration(file);
			badAmmo.add(file.getName());
			
			//name
			String name = conf.contains("name") ? conf.getString("name") : null; 
			if (name == null) {
				System.out.println(Color.RED + "The ammunition name in file " + file.getName() + " wasn't found!" + Color.RESET);
				System.out.println(Color.RED + "Add this to config 'name: <ammunition-name>'" + Color.RESET);
				continue;
			}
			//material
			String matS = conf.contains("material") ? conf.getString("material") : null;
			if (matS == null) {
				System.out.println(Color.RED + "The ammunition " + name + " didn't have any material selected!" + Color.RESET);
				System.out.println(Color.RED + "Add this to config 'material: <material>'" + Color.RESET);
				continue;
			} else if (Material.valueOf(matS.toUpperCase()) == null) {
				System.out.println(Color.RED + "The selected material " + matS + " in the ammunition " + name + " didn't exist!" + Color.RESET);
				continue;
			}
			Material mat = Material.valueOf(matS.toUpperCase());
			//caliber
			String caliber = conf.contains("caliber") ? conf.getString("caliber") : null;
			if (caliber == null) {
				System.out.println(Color.RED + "The caliber in the ammunition " + name + " wasn't found!" + Color.RESET);
				System.out.println(Color.RED + "Add this to config 'caliber: <caliber>'" + Color.RESET);
				continue;
			}
			//damage
			double damage = conf.contains("damage") ? conf.getDouble("damage") : 0;
			//armor pen
			double armorPen = conf.contains("armor-penetration") ? conf.getDouble("armor-penetration") : 0;
			//lore
			List<String> loreList = conf.contains("lore") ? conf.getStringList("lore") : new ArrayList<String>();
			if (loreList.isEmpty()) {
				loreList.add("&cLore not set!");
				loreList.add("&cAdd 'lore: - something'");
				loreList.add("&cin the ammunition file");
			}
			String[] lore = new String[loreList.size()];
			lore = loreList.toArray(lore);
			//display name
			String displayName = conf.contains("display-name") ? conf.getString("display-name") : "&cDisplay name not set!";
			//bullet speed
			double speed = conf.contains("speed") ? conf.getDouble("speed") : 3;
			//stack size
			int maxStackSize = conf.contains("max-stack-size") ? conf.getInt("max-stack-size") : 64;
			//shoot material
			String sMatS = conf.contains("material") ? conf.getString("material") : null;
			Material shootMat = mat;
			if (Material.valueOf(sMatS.toUpperCase()) != null) {
				shootMat = Material.valueOf(sMatS.toUpperCase());
			}
			//ammo type
			AmmoType type = conf.contains("type") ? AmmoType.valueOf(conf.getString("type")) : null; 
			if (type == null) {
				System.out.println(Color.RED + "The weapon " + name + " didn't have any weapon type selected!" + Color.RESET);
				System.out.println(Color.RED + "Add this to config 'type: <Auto/SemiAuto/BoltAction>'" + Color.RESET);
				continue;
			}
			//split bullet amount
			int splitBulletAmount = conf.contains("split-bullet-amount") ? conf.getInt("split-bullet-amount") : 1;
			//explosion range
			double explosionRange = conf.contains("explosion-range") ? conf.getDouble("explosion-range") : 0;
			//explosion damage
			double explosionDamage = conf.contains("explosion-damage") ? conf.getDouble("explosion-damage") : 0;
			//explosion damage drop
			double explosionDamageDrop = conf.contains("explosion-damage-drop") ? conf.getDouble("explosion-damage-drop") : 0;
			
			//add ammunition to map
			Ammo ammo = new Ammo(name, caliber, mat, damage, armorPen, lore, displayName, speed, maxStackSize, shootMat, type, splitBulletAmount, explosionRange, explosionDamage, explosionDamageDrop);
			WeaponManager.ammoStored.put(name, ammo);
			
			badAmmo.remove(file.getName());
			goodAmmo.add(file.getName());
		}
		
		//print loaded ammunition and finished message
		printGoodBadFiles(goodAmmo, badAmmo, "Ammunition");
	}
	
	private static void printGoodBadFiles(List<String> good, List<String> bad, String loadedName) {
		System.out.println(loadedName + " loaded!");
		System.out.print(loadedName + ": ");
		int count = 0;
		for (String str : good) {
			System.out.print((count > 0 ? ", " : "") + Color.GREEN + str + Color.RESET);
			count++;
		}
		for (String str : bad) {
			System.out.print((count > 0 ? ", " : "") + Color.RED + str + Color.RESET);
			count++;
		}
		System.out.print("\n");
	}
}
