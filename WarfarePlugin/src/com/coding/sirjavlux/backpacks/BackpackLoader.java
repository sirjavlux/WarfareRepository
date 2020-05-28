package com.coding.sirjavlux.backpacks;

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

public class BackpackLoader extends BackpackManager{

	public static void loadFiles() {
		Plugin plugin = Main.getPlugin(Main.class);
		
		//melee
		File backpackFile = new File(plugin.getDataFolder() + "/backpacks");
		//if not existing create and preload weapons
		if (!backpackFile.exists()) {
			System.out.println("Loading backpack presets...");
			backpackFile.mkdir();
			
			FileManager.writeFileFromResources(plugin, plugin.getDataFolder() + "/backpacks/test.yml", "Backpacks/test.yml");
		}
		loadBackpackData();
	}
	
	private static void loadBackpackData() {
		Plugin plugin = Main.getPlugin(Main.class);
		File backpacksFile = new File(plugin.getDataFolder() + "/backpacks");

		//loop trough files
		List<String> goodBackpacks = new ArrayList<>();
		List<String> badBackpacks = new ArrayList<>();
		for (File file : backpacksFile.listFiles()) {
			FileConfiguration conf = YamlConfiguration.loadConfiguration(file);
			badBackpacks.add(file.getName());
			
			//name
			String name = conf.contains("name") ? conf.getString("name") : null; 
			if (name == null) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The backpack name in file " + file.getName() + " wasn't found!");
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Add this to config 'name: <backpack-name>'");
				continue;
			}
			//material
			String matS = conf.contains("material") ? conf.getString("material") : null;
			if (matS == null) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The backpack " + name + " didn't have any material selected!");
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Add this to config 'material: <material>'");
				continue;
			}
			Material mat = null;
			try {
				mat = Material.valueOf(matS.toUpperCase());
			} catch (Exception e) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The selected material " + matS + " in the backpack " + name + " didn't exist!");
				continue;
			}
			//lore
			List<String> lore = conf.contains("lore") ? conf.getStringList("lore") : new ArrayList<String>();
			if (lore.isEmpty()) {
				lore.add("&cLore not set!");
				lore.add("&cAdd 'lore: - something'");
				lore.add("&cin the backpack file");
			}
			//display name
			String displayName = conf.contains("display-name") ? conf.getString("display-name") : "&cDisplay name not set!";
			//model data
			int modelData = conf.contains("model-data") ? conf.getInt("model-data") : 0;
			//backpack spaces
			int spaces = conf.contains("spaces") ? conf.getInt("spaces") : 0;
			
			//add melee to map
			Backpack backpack = new Backpack(name, displayName, mat, lore, modelData, spaces);
			BackpackManager.backpacks.put(name, backpack);
			
			badBackpacks.remove(file.getName());
			goodBackpacks.add(file.getName());
		}
		
		//print loaded backpacks and finished message
		printGoodBadFiles(goodBackpacks, badBackpacks, "Backpacks");
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
