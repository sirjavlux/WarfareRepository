package com.coding.sirjavlux.consumables;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import com.coding.sirjavlux.core.Main;
import com.coding.sirjavlux.utils.FileManager;

public class ConsumableLoader extends ConsumableManager {

	/*////////////////////////
	 * LOADING CONSUMABLES
	 *////////////////////////
	
	public static void loadFiles() {
		Plugin plugin = Main.getPlugin(Main.class);
		
		//ammunition
		File ammoFile = new File(plugin.getDataFolder() + "/consumables");
		//if not existing create and preload weapons
		if (!ammoFile.exists()) {
			System.out.println("Loading consumable presets...");
			ammoFile.mkdir();
			
			FileManager.writeFileFromResources(plugin, plugin.getDataFolder() + "/consumables/grizly-medkit.yml", "Consumable/grizly-medkit.yml");
		}
		loadConsumables();
	}
	
	private static void loadConsumables() {
		Plugin plugin = Main.getPlugin(Main.class);
		File magsFile = new File(plugin.getDataFolder() + "/consumables");

		//loop trough files
		List<String> goodMags = new ArrayList<>();
		List<String> badMags = new ArrayList<>();
		for (File file : magsFile.listFiles()) {
			FileConfiguration conf = YamlConfiguration.loadConfiguration(file);
			badMags.add(file.getName());
			
			//name
			String name = conf.contains("name") ? conf.getString("name") : null; 
			if (name == null) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The consumable name in file " + file.getName() + " wasn't found!");
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Add this to config 'name: <consumable-name>'");
				continue;
			}
			//material
			String matS = conf.contains("material") ? conf.getString("material") : null;
			if (matS == null) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The consumable " + name + " didn't have any material selected!");
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Add this to config 'material: <material>'");
				continue;
			}
			Material mat = null;
			try {
				mat = Material.valueOf(matS.toUpperCase());
			} catch (Exception e) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The selected material " + matS + " in the consumable " + name + " didn't exist!");
				continue;
			}
			//lore
			List<String> lore = conf.contains("lore") ? conf.getStringList("lore") : new ArrayList<String>();
			if (lore.isEmpty()) {
				lore.add("&cLore not set!");
				lore.add("&cAdd 'lore: - something'");
				lore.add("&cin the consumable file");
			}
			//display name
			String displayName = conf.contains("display-name") ? conf.getString("display-name") : "&cDisplay name not set!";
			//custom model data
			int modelData = conf.contains("custom-model-data") ? conf.getInt("custom-model-data") : 0;
			//heal
			double heal = conf.contains("heal") ? conf.getDouble("heal") : 0;
			//splint
			boolean splint = conf.contains("splint") ? conf.getBoolean("splint") : false;
			//bandage
			boolean bandage = conf.contains("bandage") ? conf.getBoolean("bandage") : false;
			//concussion
			boolean concussion = conf.contains("concussion") ? conf.getBoolean("concussion") : false;
			//uses
			int uses = conf.contains("uses") ? conf.getInt("uses") : 1;
			//use time
			int useTime = conf.contains("use-time") ? conf.getInt("use-time") : 0;
			//walk speed
			double walkSpeed = conf.contains("walk-speed") ? conf.getDouble("walk-speed") : 0.2;
			//use sound
			Sound useSound = Sound.BLOCK_BEACON_AMBIENT;
			try { useSound = Sound.valueOf("use-sound"); } catch (Exception e) { }
			//finish sound
			Sound finishSound = Sound.BLOCK_ANVIL_USE;
			try { finishSound = Sound.valueOf("finish-sound"); } catch (Exception e) { }
			
			Consumable consumable = new Consumable(mat, modelData, lore, displayName, name, heal, splint, bandage, concussion, uses, useTime, walkSpeed, useSound, finishSound);
			ConsumableManager.consumables.put(name, consumable);
			
			badMags.remove(file.getName());
			goodMags.add(file.getName());
		}
		
		//print loaded magazines and finished message
		printGoodBadFiles(goodMags, badMags, "Consumables");
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
