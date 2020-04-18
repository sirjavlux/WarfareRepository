package com.coding.sirjavlux.core;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.coding.sirjavlux.commands.CommandManager;
import com.coding.sirjavlux.listeners.AsyncBulletHandler;
import com.coding.sirjavlux.listeners.InventoryListener;
import com.coding.sirjavlux.listeners.ItemListener;
import com.coding.sirjavlux.listeners.PlayerJoinQuitListener;
import com.coding.sirjavlux.listeners.ProjectileListener;
import com.coding.sirjavlux.utils.Color;
import com.coding.sirjavlux.weapons.WeaponLoader;

public class Main extends JavaPlugin {
	
	private static Main instance;
	private static AsyncBulletHandler bulletHandler;
	
	@Override
	public void onEnable() {
		//create config if first launch
		this.saveDefaultConfig();
		
		//load listeners
		Bukkit.getPluginManager().registerEvents(new ProjectileListener(), this);
		Bukkit.getPluginManager().registerEvents(new AsyncBulletHandler(), this);
		Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerJoinQuitListener(), this);
		Bukkit.getPluginManager().registerEvents(new ItemListener(), this);
		
		//commands
		this.getCommand("wf").setExecutor(new CommandManager());
		
		//load weapon files
		WeaponLoader.loadFiles();
		
		//load config
		ConfigManager.loadConfig(this.getConfig());
		
		instance = this;
		
		System.out.println(Color.GREEN + "Warfare successfully enabled!" + Color.RESET);
	}
	
	@Override
	public void onDisable() {
		System.out.println(Color.RED + "Warfare disabled!" + Color.RESET);
	}
	
	public static Main getInstance() {
		return instance;
	}
	
	public static AsyncBulletHandler getBulletHandler() {
		return bulletHandler;
	}
}
