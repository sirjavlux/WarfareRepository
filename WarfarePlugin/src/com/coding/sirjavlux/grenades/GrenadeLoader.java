package com.coding.sirjavlux.grenades;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import com.coding.sirjavlux.core.Main;
import com.coding.sirjavlux.utils.FileManager;

public class GrenadeLoader extends GrenadeManager {

	public static void loadFiles() {
		Plugin plugin = Main.getPlugin(Main.class);
		
		//ammunition
		File ammoFile = new File(plugin.getDataFolder() + "/grenades");
		//if not existing create and preload weapons
		if (!ammoFile.exists()) {
			System.out.println("Loading grenade presets...");
			ammoFile.mkdir();
			
			FileManager.writeFileFromResources(plugin, plugin.getDataFolder() + "/grenades/smoke.yml", "Grenade/smoke.yml");
			FileManager.writeFileFromResources(plugin, plugin.getDataFolder() + "/grenades/flash.yml", "Grenade/flash.yml");
			FileManager.writeFileFromResources(plugin, plugin.getDataFolder() + "/grenades/molotov.yml", "Grenade/molotov.yml");
			FileManager.writeFileFromResources(plugin, plugin.getDataFolder() + "/grenades/explosive.yml", "Grenade/explosive.yml");
		}
		loadGrenades();
	}
	
	private static void loadGrenades() {
		Plugin plugin = Main.getPlugin(Main.class);
		File magsFile = new File(plugin.getDataFolder() + "/grenades");

		//loop trough files
		List<String> goodMags = new ArrayList<>();
		List<String> badMags = new ArrayList<>();
		for (File file : magsFile.listFiles()) {
			FileConfiguration conf = YamlConfiguration.loadConfiguration(file);
			badMags.add(file.getName());
			
			//name
			String name = conf.contains("name") ? conf.getString("name") : null; 
			if (name == null) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The grenade name in file " + file.getName() + " wasn't found!");
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Add this to config 'name: <grenade-name>'");
				continue;
			}
			//material
			String matS = conf.contains("material") ? conf.getString("material") : null;
			if (matS == null) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The grenade " + name + " didn't have any material selected!");
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Add this to config 'material: <material>'");
				continue;
			}
			Material mat = null;
			try {
				mat = Material.valueOf(matS.toUpperCase());
			} catch (Exception e) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The selected material " + matS + " in the grenade " + name + " didn't exist!");
				continue;
			}
			//type
			String typeS = conf.contains("type") ? conf.getString("type") : null;
			if (typeS == null) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The grenade " + name + " didn't have any type selected!");
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Add this to config 'type: <type>'");
				continue;
			}
			GrenadeType type = null;
			try {
				type = GrenadeType.valueOf(typeS);
			} catch (Exception e) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The selected grenade type " + typeS + " in the grenade " + name + " didn't exist!");
				continue;
			}
			//lore
			List<String> lore = conf.contains("lore") ? conf.getStringList("lore") : new ArrayList<String>();
			if (lore.isEmpty()) {
				lore.add("&cLore not set!");
				lore.add("&cAdd 'lore: - something'");
				lore.add("&cin the grenade file");
			}
			//display name
			String displayName = conf.contains("display-name") ? conf.getString("display-name") : "&cDisplay name not set!";
			//custom model data
			int modelData = conf.contains("custom-model-data") ? conf.getInt("custom-model-data") : 0;
			//explosion range
			double explosionRange = conf.contains("explosion-range") ? conf.getDouble("explosion-range") : 0;
			//explosion damage
			double explosionDamage = conf.contains("explosion-damage") ? conf.getDouble("explosion-damage") : 0;
			//explosion damage drop
			double explosionDamageDrop = conf.contains("explosion-damage-drop") ? conf.getDouble("explosion-damage-drop") : 0;
			//explosion fire ticks
			int fireTicks = conf.contains("explosion-fire-ticks") ? conf.getInt("explosion-fire-ticks") : 0;
			//speed
			double speed = conf.contains("speed") ? conf.getDouble("speed") : 0.6;
			//stack size
			int maxStackSize = conf.contains("max-stack-size") ? conf.getInt("max-stack-size") : 64;
			//duration
			int duration = conf.contains("duration") ? conf.getInt("duration") : 60;
			
			Grenade grenade = new Grenade(mat, modelData, lore, displayName, type, name, explosionRange, explosionDamage, explosionDamageDrop, fireTicks, speed, maxStackSize, duration);
			GrenadeManager.grenades.put(name, grenade);
			
			badMags.remove(file.getName());
			goodMags.add(file.getName());
		}
		
		//print loaded magazines and finished message
		printGoodBadFiles(goodMags, badMags, "Grenades");
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
