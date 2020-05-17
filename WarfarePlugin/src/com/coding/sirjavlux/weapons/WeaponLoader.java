package com.coding.sirjavlux.weapons;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import com.coding.sirjavlux.core.Main;
import com.coding.sirjavlux.types.Ammo;
import com.coding.sirjavlux.types.AmmoType;
import com.coding.sirjavlux.types.Magazine;
import com.coding.sirjavlux.types.Mechanic;
import com.coding.sirjavlux.types.Weapon;
import com.coding.sirjavlux.types.WeaponType;
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
			
			FileManager.writeFileFromResources(plugin, plugin.getDataFolder() + "/ammunition/9x19luger.yml", "Ammo/9x19luger.yml");
			FileManager.writeFileFromResources(plugin, plugin.getDataFolder() + "/ammunition/9x19pso.yml", "Ammo/9x19pso.yml");
			FileManager.writeFileFromResources(plugin, plugin.getDataFolder() + "/ammunition/9x19pst.yml", "Ammo/9x19pst.yml");
			FileManager.writeFileFromResources(plugin, plugin.getDataFolder() + "/ammunition/9x19tracer.yml", "Ammo/9x19tracer.yml");
		}
		loadAmmunitionConfigs();
		
		//magazines
		File magsFile = new File(plugin.getDataFolder() + "/magazines");
		//if not existing create and preload magazines
		if (!magsFile.exists()) {
			System.out.println("Loading magazine presets...");
			magsFile.mkdir();
			
			FileManager.writeFileFromResources(plugin, plugin.getDataFolder() + "/magazines/mp5mag15.yml", "Mags/mp5mag15.yml");
			FileManager.writeFileFromResources(plugin, plugin.getDataFolder() + "/magazines/mp5mag30.yml", "Mags/mp5mag30.yml");
		}
		loadMagazineConfigs();
		
		//weapons
		File weaponsFile = new File(plugin.getDataFolder() + "/weapons");
		//if not existing create and preload weapons
		if (!weaponsFile.exists()) {
			System.out.println("Loading weapon presets...");
			weaponsFile.mkdir();
			
			FileManager.writeFileFromResources(plugin, plugin.getDataFolder() + "/weapons/mp5.yml", "Weapons/mp5.yml");
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
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The weapon name in file " + file.getName() + " wasn't found!");
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Add this to config 'name: <weapon-name>'");
				continue;
			}
			//weapon type
			WeaponType type = conf.contains("type") ? WeaponType.valueOf(conf.getString("type")) : null; 
			if (type == null) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The weapon " + name + " didn't have any weapon type selected!");
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Add this to config 'type: <Auto/SemiAuto/BoltAction>'");
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
						Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The selected required magazine " + str + " in the weapon " + name + " didn't exist!");
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
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The weapon " + name + " didn't have any material selected!");
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Add this to config 'material: <material>'");
				continue;
			}
			Material mat = null;
			try {
				mat = Material.valueOf(matS.toUpperCase());
			} catch (Exception e) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The selected weapon " + matS + " in the ammunition " + name + " didn't exist!");
				continue;
			}
			//smoke offset
			List<Double> smokeOffsetList = conf.contains("smoke-offset") ? conf.getDoubleList("smoke-offset") : null;
			double[] smokeOffset = new double[] { 0d, 0d, 0d };
			if (smokeOffsetList != null) {
				if (smokeOffsetList.size() != 3) {
					Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Invalid smoke offset in the weapon " + name + ", there can only be 3 numbers x, y, z!");
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
					Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The selected default magazine " + defaultMagName + " in the weapon " + name + " didn't exist!");
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
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The weapon " + name + " didn't have any barrel caliber or magazine requirement set!");
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Add this to config for integrated magazine 'barrel-caliber: <caliber>'");
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Add this to config for use of magazines 'magazine-required: <- mag1>'");
				continue;
			}
			//fire rate
			double fireRate = conf.contains("fire-rate") ? conf.getDouble("fire-rate") : 1;
			//preload ammo
			String preLoadAmmoStr = conf.contains("pre-load-ammo") ? conf.getString("pre-load-ammo") : null;
			Ammo preLoadAmmo = null;
			if (loadedByDefault) {
				if (preLoadAmmoStr == null) {
					Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The weapon " + name + " didn't have any pre load ammo selected!");
					Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Add this to config 'pre-load-ammo: <ammo-name>'");
					continue;
				} else if (!WeaponManager.isAmmunition(preLoadAmmoStr)) {
					Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The pre load ammunition " + preLoadAmmoStr + " in The weapon " + name + " didn't exist!");
					continue;
				}
				preLoadAmmo = WeaponManager.getStoredAmmo(preLoadAmmoStr);
			}
			if (preLoadAmmo != null) {
				if (!preLoadAmmo.getCaliber().equalsIgnoreCase(caliber)) {
					Bukkit.getServer().getConsoleSender().sendMessage("Preload ammo caliber " + preLoadAmmo.getCaliber() + " | weapon caliber " + caliber);
					Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The pre load ammunition " + preLoadAmmoStr + " in The weapon " + name + " didn't have the same caliber as the weapon " + caliber + "!");
					continue;
				}
			}
			//burst amount
			int burstAmount = conf.contains("burst-amount") ? conf.getInt("burst-amount") : 3;
			//burst speed
			double burstSpeed = conf.contains("burst-speed") ? conf.getDouble("burst-speed") : 7;
			//recoil reduction
			double recoilRed = conf.contains("recoil-reduction") ? conf.getDouble("recoil-reduction") : 0;
			//knockback reduction
			double knockbackRed = conf.contains("knockback-reduction") ? conf.getDouble("knockback-reduction") : 0;
			//reload speed
			int reloadSpeed = conf.contains("reload-speed") ? conf.getInt("reload-speed") : 80;
			//custom model data
			int customModel = conf.contains("custom-model-data") ? conf.getInt("custom-model-data") : 0;
			//right action
			Mechanic right = null;
			try { right = Mechanic.valueOf(conf.getString("actions.right").toUpperCase()); } catch (Exception e) {}
			//right action
			Mechanic left = null;
			try { left = Mechanic.valueOf(conf.getString("actions.left").toUpperCase()); } catch (Exception e) {}
			//right action
			Mechanic shiftRight = null;
			try { shiftRight = Mechanic.valueOf(conf.getString("actions.shift_right").toUpperCase()); } catch (Exception e) {}
			//right action
			Mechanic shiftLeft = null;
			try { shiftLeft = Mechanic.valueOf(conf.getString("actions.shift_left").toUpperCase()); } catch (Exception e) {}
			//right action
			Mechanic shift = null;
			try { shift = Mechanic.valueOf(conf.getString("actions.shift").toUpperCase()); } catch (Exception e) {}
			//scope amount
			float scope = (float) (conf.contains("scope") ? conf.getDouble("scope") : -0.2);
			//scoped material
			String scopeMatS = conf.contains("scope-material") ? conf.getString("scope-material") : null;
			Material scopeMat = mat;
			try { scopeMat = Material.valueOf(scopeMatS.toUpperCase()); } catch (Exception e) { }
			//scoped model data
			int scopeModelData = conf.contains("scope-custom-model-data") ? conf.getInt("scope-custom-model-data") : 0;
			//scope sound
			String scopeSound = conf.contains("scope-sound") ? conf.getString("scope-sound") : "block.wood.hit";
			//shoot sound
			String shootSound = conf.contains("shoot-sound") ? conf.getString("shoot-sound") : "block.iron.hit";
			//reload sound
			String reloadSound = conf.contains("reload-sound") ? conf.getString("reload-sound") : "block.stone_button.click_on";
			//after shot sound
			String afterShotSound = conf.contains("after-shot-sound") ? conf.getString("after-shot-sound") : null;
			//after reload sound
			String finishedReloadSound = conf.contains("finished-reload-sound") ? conf.getString("finished-reload-sound") : "block.stone_button.click_on";
			
			//create weapon
			Weapon weapon = new Weapon(type, mat, magReq, name, smokeOffset, smokeEnabled, smokeIntensity, damage, lore, displayName, defaultMag, loadedByDefault, reqMag, barrelAmmoCap, caliber, fireRate, preLoadAmmo, burstAmount, burstSpeed, recoilRed, knockbackRed, reloadSpeed, customModel, right, shiftRight, left, shiftLeft, shift, scope, scopeMat, scopeModelData, scopeSound, shootSound, reloadSound, afterShotSound, finishedReloadSound);
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
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The magazine name in file " + file.getName() + " wasn't found!");
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Add this to config 'name: <magazine-name>'");
				continue;
			}
			//material
			String matS = conf.contains("material") ? conf.getString("material") : null;
			if (matS == null) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The magazine " + name + " didn't have any material selected!");
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Add this to config 'material: <material>'");
				continue;
			}
			Material mat = null;
			try {
				mat = Material.valueOf(matS.toUpperCase());
			} catch (Exception e) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The selected material " + matS + " in the magazine " + name + " didn't exist!");
				continue;
			}
			//caliber
			String caliber = conf.contains("caliber") ? conf.getString("caliber") : null;
			if (caliber == null) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The caliber in the magazine " + name + " wasn't found!");
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Add this to config 'caliber: <caliber>'");
				continue;
			}
			//ammo capasity
			int ammoCap = conf.contains("ammo-capasity") ? conf.getInt("ammo-capasity") : 0;
			if (!conf.contains("ammo-capasity")) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The ammunition capasity in the magazine " + name + " wasn't found!");
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Add this to config 'ammo-capasity: <amount>'");
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
			//custom model data
			int customModel = conf.contains("custom-model-data") ? conf.getInt("custom-model-data") : 0;
			//add ammo sound
			String addAmmo = conf.contains("add-ammo-sound") ? conf.getString("add-ammo-sound") : "block.stone_button.click_on";
			//remove ammo sound
			String removeAmmo = conf.contains("remove-ammo-sound") ? conf.getString("remove-ammo-sound") : "block.stone_button.click_on";
			
			//add mag to map
			Magazine mag = new Magazine(caliber, ammoCap, name, mat, displayName, lore, customModel, addAmmo, removeAmmo);
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
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The ammunition name in file " + file.getName() + " wasn't found!");
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Add this to config 'name: <ammunition-name>'");
				continue;
			}
			//material
			String matS = conf.contains("material") ? conf.getString("material") : null;
			if (matS == null) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The ammunition " + name + " didn't have any material selected!");
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Add this to config 'material: <material>'");
				continue;
			}
			Material mat = null;
			try {
				mat = Material.valueOf(matS.toUpperCase());
			} catch (Exception e) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The selected material " + matS + " in the ammunition " + name + " didn't exist!");
				continue;
			}
			//caliber
			String caliber = conf.contains("caliber") ? conf.getString("caliber") : null;
			if (caliber == null) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The caliber in the ammunition " + name + " wasn't found!");
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Add this to config 'caliber: <caliber>'");
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
			String sMatS = conf.contains("projectile-material") ? conf.getString("projectile-material") : null;
			Material shootMat = mat;
			try {
				mat = Material.valueOf(sMatS.toUpperCase());
			} catch (Exception e) {
				System.out.println(e);
			}
			//ammo type
			AmmoType type = conf.contains("type") ? AmmoType.valueOf(conf.getString("type")) : null; 
			if (type == null) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The weapon " + name + " didn't have any weapon type selected!");
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Add this to config 'type: <Auto/SemiAuto/BoltAction>'");
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
			//knock back
			double knockBack = conf.contains("knockback") ? conf.getDouble("knockback") : 0.5;
			//armor damage 
			double armorDamage = conf.contains("armor-damage") ? conf.getDouble("armor-damage") : 7;
			//recoil
			double recoil = conf.contains("recoil") ? conf.getDouble("recoil") : 0;
			//knockback
			double knockback = conf.contains("knockback") ? conf.getDouble("knockback") : 0;
			//custom model data
			int customModel = conf.contains("custom-model-data") ? conf.getInt("custom-model-data") : 0;
			//explosion fire ticks
			int fireTicks = conf.contains("explosion-fire-ticks") ? conf.getInt("explosion-fire-ticks") : 0;
			//spread
			float spread = (float) (conf.contains("bullet-spread") ? conf.getDouble("bullet-spread") : 0);
			//hitBurnTicks
			int hitBurnTicks = conf.contains("hit-burn-ticks") ? conf.getInt("hit-burn-ticks") : 0;
			//trail
			Color trail = null;
			if (conf.contains("trail.r") && conf.contains("trail.g") && conf.contains("trail.b")) {
				int r = conf.getInt("trail.r");
				int g = conf.getInt("trail.g");
				int b = conf.getInt("trail.b");
				trail = Color.fromBGR(b, g, r);
			}
			//bullet hit ground sound
			String hitGround = conf.contains("hit-ground-sound") ? conf.getString("hit-ground-sound") : "";
			//bullet hit flesh sound
			String hitFlesh = conf.contains("hit-flesh-sound") ? conf.getString("hit-flesh-sound") : "block.slime_block.break";
			//bullet hit armor sound
			String hitArmor = conf.contains("hit-armor-sound") ? conf.getString("hit-armor-sound") : "block.anvil.land";
			//explode sound
			String explodeSound = conf.contains("explode-sound") ? conf.getString("explode-sound") : "entity.generic.explode";
			//explode sound
			String trailSound = conf.contains("trail-sound") ? conf.getString("trail-sound") : "";
			
			//add ammunition to map
			Ammo ammo = new Ammo(name, caliber, mat, damage, armorPen, lore, displayName, speed, maxStackSize, shootMat, type, splitBulletAmount, explosionRange, explosionDamage, explosionDamageDrop, knockBack, armorDamage, recoil, knockback, customModel, fireTicks, spread, hitBurnTicks, trail, hitGround, hitFlesh, hitArmor, explodeSound, trailSound);
			WeaponManager.ammoStored.put(name, ammo);
			
			badAmmo.remove(file.getName());
			goodAmmo.add(file.getName());
		}
		
		//print loaded ammunition and finished message
		printGoodBadFiles(goodAmmo, badAmmo, "Ammunition");
	}
	
	private static void printGoodBadFiles(List<String> good, List<String> bad, String loadedName) {
		System.out.println(loadedName + " loaded!");
		int count = 0;
		for (String str : good) {
			Bukkit.getServer().getConsoleSender().sendMessage((count > 0 ? ", " : "") + ChatColor.GREEN + str);
			count++;
		}
		for (String str : bad) {
			Bukkit.getServer().getConsoleSender().sendMessage((count > 0 ? ", " : "") + ChatColor.RED + str);
			count++;
		}
	}
}
