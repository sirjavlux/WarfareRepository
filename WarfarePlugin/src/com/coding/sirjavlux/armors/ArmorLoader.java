package com.coding.sirjavlux.armors;

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
import com.coding.sirjavlux.utils.FileManager;

public class ArmorLoader extends ArmorManager{

	public static void loadFiles() {
		Plugin plugin = Main.getPlugin(Main.class);
		
		//ammunition
		File ammoFile = new File(plugin.getDataFolder() + "/armor");
		//if not existing create and preload weapons
		if (!ammoFile.exists()) {
			System.out.println("Loading armor presets...");
			ammoFile.mkdir();
			
			FileManager.writeFileFromResources(plugin, plugin.getDataFolder() + "/armor/6b2.yml", "Armor/6b2.yml");
		}
		loadArmorData();
	}
	
	public static void loadArmorData() {
		Plugin plugin = Main.getPlugin(Main.class);
		File magsFile = new File(plugin.getDataFolder() + "/armor");

		//loop trough files
		List<String> goodMags = new ArrayList<>();
		List<String> badMags = new ArrayList<>();
		for (File file : magsFile.listFiles()) {
			FileConfiguration conf = YamlConfiguration.loadConfiguration(file);
			badMags.add(file.getName());
			
			//name
			String name = conf.contains("name") ? conf.getString("name") : null; 
			if (name == null) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The armor name in file " + file.getName() + " wasn't found!");
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Add this to config 'name: <armor-name>'");
				continue;
			}
			//material
			String matS = conf.contains("material") ? conf.getString("material") : null;
			if (matS == null) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The armor " + name + " didn't have any material selected!");
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Add this to config 'material: <material>'");
				continue;
			}
			Material mat = null;
			try {
				mat = Material.valueOf(matS.toUpperCase());
			} catch (Exception e) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The selected material " + matS + " in the armor " + name + " didn't exist!");
				continue;
			}
			//lore
			List<String> lore = conf.contains("lore") ? conf.getStringList("lore") : new ArrayList<String>();
			if (lore.isEmpty()) {
				lore.add("&cLore not set!");
				lore.add("&cAdd 'lore: - something'");
				lore.add("&cin the armor file");
			}
			//color
			Color color = null;
			if (conf.contains("color.r") && conf.contains("color.g") && conf.contains("color.b")) {
				int r = conf.getInt("color.r");
				int g = conf.getInt("color.g");
				int b = conf.getInt("color.b");
				color = Color.fromBGR(b, g, r);
			}
			//display name
			String displayName = conf.contains("display-name") ? conf.getString("display-name") : "&cDisplay name not set!";
			//bullet hit armor sound
			String hitArmor = conf.contains("hit-armor-sound") ? conf.getString("hit-armor-sound") : "block.anvil.land";
			//head data
			String headData = conf.contains("head-data") ? conf.getString("head-data") : "";
			//durability
			int durability = conf.contains("durability") ? conf.getInt("durability") : 200;
			//model data
			int modelData = conf.contains("model-data") ? conf.getInt("model-data") : 0;
			//protection
			double protection = conf.contains("protection") ? conf.getInt("protection") : 0.2;
			
			//add armor to map
			Armor armor = new Armor(name, displayName, mat, lore, hitArmor, color, headData, durability, modelData, protection);
			ArmorManager.armors.put(name, armor);
			
			badMags.remove(file.getName());
			goodMags.add(file.getName());
		}
		
		//print loaded magazines and finished message
		printGoodBadFiles(goodMags, badMags, "Armors");
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
