package com.coding.sirjavlux.core;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.coding.sirjavlux.commands.CommandManager;
import com.coding.sirjavlux.health.HealthEffects;
import com.coding.sirjavlux.listeners.AsyncBulletHandler;
import com.coding.sirjavlux.listeners.InventoryListener;
import com.coding.sirjavlux.listeners.ItemListener;
import com.coding.sirjavlux.listeners.PlayerListener;
import com.coding.sirjavlux.weapons.WeaponLoader;
import com.coding.sirjavlux.weapons.WeaponReloadHandler;

public class Main extends JavaPlugin {
	
	private static Main instance;
	private static AsyncBulletHandler bulletHandler;
	private static HealthEffects healthEffects;
	private static WeaponReloadHandler weaponReloadHandler;
	
	@Override
	public void onEnable() {
		//create config if first launch
		this.saveDefaultConfig();
		
		//create healtheffects and bullet handler instance
		healthEffects = new HealthEffects();
		bulletHandler = new AsyncBulletHandler();
		weaponReloadHandler = new WeaponReloadHandler();
		
		//load listeners
		Bukkit.getPluginManager().registerEvents(bulletHandler, this);
		Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
		Bukkit.getPluginManager().registerEvents(new ItemListener(), this);
		Bukkit.getPluginManager().registerEvents(healthEffects, this);
		Bukkit.getPluginManager().registerEvents(weaponReloadHandler, this);
		
		//commands
		this.getCommand("wf").setExecutor(new CommandManager());
		
		//load weapon files
		WeaponLoader.loadFiles();
		
		//load config
		ConfigManager.loadConfig(this.getConfig());
		
		instance = this;
		
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Warfare successfully enabled!");
	}
	
	@Override
	public void onDisable() {
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Warfare disabled!");
	}
	
	public static Main getInstance() {
		return instance;
	}
	
	public static AsyncBulletHandler getBulletHandler() {
		return bulletHandler;
	}
	
	public static HealthEffects getHealthEffects() {
		return healthEffects;
	}
	
	public static WeaponReloadHandler getWeaponReloadHandler() {
		return weaponReloadHandler;
	}
}
