package com.coding.sirjavlux.core;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.coding.sirjavlux.commands.CommandManager;
import com.coding.sirjavlux.utils.Color;

public class Main extends JavaPlugin {
	
	private static Main instance;
	
	@Override
	public void onEnable() {
		//create config if first launch
		this.saveDefaultConfig();
		
		//load listeners
		Bukkit.getPluginManager().registerEvents(new ProjectileListener(), this);
		Bukkit.getPluginManager().registerEvents(new WeaponUseListener(), this);
		
		//commands
		this.getCommand("wf").setExecutor(new CommandManager());
		
		//load weapon files
		WeaponLoader.loadFiles();
		
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
}
