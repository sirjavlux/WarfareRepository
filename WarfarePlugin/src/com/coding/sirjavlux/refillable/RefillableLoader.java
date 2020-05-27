package com.coding.sirjavlux.refillable;

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

public class RefillableLoader extends RefillableManager{

	public static void loadFiles() {
		Plugin plugin = Main.getPlugin(Main.class);
		
		//melee
		File refillableFile = new File(plugin.getDataFolder() + "/refillable");
		//if not existing create and preload weapons
		if (!refillableFile.exists()) {
			System.out.println("Loading refillable presets...");
			refillableFile.mkdir();
			
			FileManager.writeFileFromResources(plugin, plugin.getDataFolder() + "/refillable/test.yml", "Refillable/test.yml");
		}
		loadMeleeData();
	}
	
	public static void loadMeleeData() {
		Plugin plugin = Main.getPlugin(Main.class);
		File refillableFile = new File(plugin.getDataFolder() + "/refillable");

		//loop trough files
		List<String> goodRefillable = new ArrayList<>();
		List<String> badRefillable = new ArrayList<>();
		for (File file : refillableFile.listFiles()) {
			FileConfiguration conf = YamlConfiguration.loadConfiguration(file);
			badRefillable.add(file.getName());
			
			//name
			String name = conf.contains("name") ? conf.getString("name") : null; 
			if (name == null) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The refillable name in file " + file.getName() + " wasn't found!");
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Add this to config 'name: <refillable-name>'");
				continue;
			}
			//material
			String matS = conf.contains("material") ? conf.getString("material") : null;
			if (matS == null) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The refillable " + name + " didn't have any material selected!");
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Add this to config 'material: <material>'");
				continue;
			}
			Material mat = null;
			try {
				mat = Material.valueOf(matS.toUpperCase());
			} catch (Exception e) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The selected material " + matS + " in the refillable " + name + " didn't exist!");
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
			//fill amount
			int fillAmount = conf.contains("fill") ? conf.getInt("fill") : 200;
			//model data
			int modelData = conf.contains("model-data") ? conf.getInt("model-data") : 0;
			//use sound
			String useSound = conf.contains("use-sound") ? conf.getString("use-sound") : "";
			//finish sound
			String finishSound = conf.contains("finish-sound") ? conf.getString("finish-sound") : "";
			//walk speed
			double walkSpeed = conf.contains("walk-speed") ? conf.getDouble("walk-speed") : 0.1;
			//use time 
			int useTime = conf.contains("use-time") ? conf.getInt("use-time") : 20;
			//fill time 
			int fillTime = conf.contains("fill-time") ? conf.getInt("fill-time") : 20;
			//refill type
			if (!conf.contains("type")) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The refillable " + name + " didn't have any type selected!");
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Add this to config 'type: <type>'");
				continue;
			}
			RefillableType type = null;
			String typeS = conf.getString("type");
			try {
				type = RefillableType.valueOf(typeS);
			} catch (Exception e) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The selected type " + typeS + " in the refillable " + name + " didn't exist!");
				continue;
			}
			//use amount
			double useAmount = conf.contains("use-amount") ? conf.getDouble("use-amount") : 10;
			//add melee to map
			Refillable refillable = new Refillable(name, displayName, mat, lore, fillAmount, modelData, useSound, finishSound, walkSpeed, useTime, fillTime, type, useAmount);
			RefillableManager.refillables.put(name, refillable);
			
			badRefillable.remove(file.getName());
			goodRefillable.add(file.getName());
		}
		
		//print loaded magazines and finished message
		printGoodBadFiles(goodRefillable, badRefillable, "Melees");
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
