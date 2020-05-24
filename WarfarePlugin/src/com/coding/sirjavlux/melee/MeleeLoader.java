package com.coding.sirjavlux.melee;

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

public class MeleeLoader extends MeleeManager{

	public static void loadFiles() {
		Plugin plugin = Main.getPlugin(Main.class);
		
		//melee
		File meleeFile = new File(plugin.getDataFolder() + "/melee");
		//if not existing create and preload weapons
		if (!meleeFile.exists()) {
			System.out.println("Loading melee presets...");
			meleeFile.mkdir();
			
			FileManager.writeFileFromResources(plugin, plugin.getDataFolder() + "/melee/test.yml", "Melee/test.yml");
		}
		loadMeleeData();
	}
	
	public static void loadMeleeData() {
		Plugin plugin = Main.getPlugin(Main.class);
		File magsFile = new File(plugin.getDataFolder() + "/melee");

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
			//display name
			String displayName = conf.contains("display-name") ? conf.getString("display-name") : "&cDisplay name not set!";
			//durability
			int durability = conf.contains("durability") ? conf.getInt("durability") : 200;
			//model data
			int modelData = conf.contains("model-data") ? conf.getInt("model-data") : 0;
			//damage
			double damage = conf.contains("damage") ? conf.getDouble("damage") : 0.2;
			//armor damage
			int armorDamage = conf.contains("armor-damage") ? conf.getInt("armor-damage") : 0;
			
			//add melee to map
			Melee melee = new Melee(name, displayName, mat, lore, durability, modelData, damage, armorDamage);
			MeleeManager.weapons.put(name, melee);
			
			badMags.remove(file.getName());
			goodMags.add(file.getName());
		}
		
		//print loaded magazines and finished message
		printGoodBadFiles(goodMags, badMags, "Melees");
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
