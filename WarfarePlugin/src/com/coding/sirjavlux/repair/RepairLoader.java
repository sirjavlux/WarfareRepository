package com.coding.sirjavlux.repair;

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

public class RepairLoader extends RepairManager{

	public static void loadFiles() {
		Plugin plugin = Main.getPlugin(Main.class);
		
		//melee
		File repairFile = new File(plugin.getDataFolder() + "/repair");
		//if not existing create and preload weapons
		if (!repairFile.exists()) {
			System.out.println("Loading repair presets...");
			repairFile.mkdir();
			
			FileManager.writeFileFromResources(plugin, plugin.getDataFolder() + "/repair/test.yml", "Repair/test.yml");
		}
		loadRepairData();
	}
	
	public static void loadRepairData() {
		Plugin plugin = Main.getPlugin(Main.class);
		File magsFile = new File(plugin.getDataFolder() + "/repair");

		//loop trough files
		List<String> goodMags = new ArrayList<>();
		List<String> badMags = new ArrayList<>();
		for (File file : magsFile.listFiles()) {
			FileConfiguration conf = YamlConfiguration.loadConfiguration(file);
			badMags.add(file.getName());
			
			//name
			String name = conf.contains("name") ? conf.getString("name") : null; 
			if (name == null) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The repair name in file " + file.getName() + " wasn't found!");
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Add this to config 'name: <repair-name>'");
				continue;
			}
			//material
			String matS = conf.contains("material") ? conf.getString("material") : null;
			if (matS == null) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The repair " + name + " didn't have any material selected!");
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Add this to config 'material: <material>'");
				continue;
			}
			Material mat = null;
			try {
				mat = Material.valueOf(matS.toUpperCase());
			} catch (Exception e) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The selected material " + matS + " in the repair " + name + " didn't exist!");
				continue;
			}
			//lore
			List<String> lore = conf.contains("lore") ? conf.getStringList("lore") : new ArrayList<String>();
			if (lore.isEmpty()) {
				lore.add("&cLore not set!");
				lore.add("&cAdd 'lore: - something'");
				lore.add("&cin the repair file");
			}
			//display name
			String displayName = conf.contains("display-name") ? conf.getString("display-name") : "&cDisplay name not set!";
			//durability
			int durability = conf.contains("durability") ? conf.getInt("durability") : 200;
			//model data
			int modelData = conf.contains("model-data") ? conf.getInt("model-data") : 0;
			//repair per use
			int repairPerUse = conf.contains("repair-per-use") ? conf.getInt("repair-per-use") : 10;
			//repair delay
			int useDelay = conf.contains("repair-delay") ? conf.getInt("repair-delay") : 5;
			//repair type
			String typeS = conf.contains("type") ? conf.getString("type") : null;
			if (typeS == null) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The repair " + name + " didn't have any type selected!");
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Add this to config 'type: <type>'");
				continue;
			}
			RepairType type = null;
			try {
				type = RepairType.valueOf(typeS.toUpperCase());
			} catch (Exception e) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The selected repair type " + typeS + " in the repair " + name + " didn't exist!");
				continue;
			}
			
			//add melee to map
			Repair repair = new Repair(name, displayName, mat, lore, durability, modelData, repairPerUse, useDelay, type);
			RepairManager.repairs.put(name, repair);
			
			badMags.remove(file.getName());
			goodMags.add(file.getName());
		}
		
		//print loaded magazines and finished message
		printGoodBadFiles(goodMags, badMags, "Repairs");
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
