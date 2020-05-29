package com.coding.sirjavlux.core;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.coding.sirjavlux.armors.ArmorLoader;
import com.coding.sirjavlux.backpacks.BackpackListener;
import com.coding.sirjavlux.backpacks.BackpackLoader;
import com.coding.sirjavlux.commands.CommandManager;
import com.coding.sirjavlux.consumables.ConsumableListener;
import com.coding.sirjavlux.consumables.ConsumableLoader;
import com.coding.sirjavlux.consumables.ConsumableManager;
import com.coding.sirjavlux.consumables.WaterBarManager;
import com.coding.sirjavlux.effectUtils.ParticleSpawner;
import com.coding.sirjavlux.grenades.GrenadeListener;
import com.coding.sirjavlux.grenades.GrenadeLoader;
import com.coding.sirjavlux.grenades.GrenadeManager;
import com.coding.sirjavlux.health.HealthEffects;
import com.coding.sirjavlux.listeners.AsyncBulletHandler;
import com.coding.sirjavlux.listeners.EquipListener;
import com.coding.sirjavlux.listeners.InventoryListener;
import com.coding.sirjavlux.listeners.ItemListener;
import com.coding.sirjavlux.listeners.PlayerListener;
import com.coding.sirjavlux.melee.MeleeListener;
import com.coding.sirjavlux.melee.MeleeLoader;
import com.coding.sirjavlux.projectiles.MoveListener;
import com.coding.sirjavlux.refillable.RefillableListener;
import com.coding.sirjavlux.refillable.RefillableLoader;
import com.coding.sirjavlux.refillable.RefillableManager;
import com.coding.sirjavlux.repair.RepairListener;
import com.coding.sirjavlux.repair.RepairLoader;
import com.coding.sirjavlux.weapons.WeaponLoader;
import com.coding.sirjavlux.weapons.WeaponReloadHandler;

public class Main extends JavaPlugin {
	
	private static Main instance;
	private static AsyncBulletHandler bulletHandler;
	private static HealthEffects healthEffects;
	private static WeaponReloadHandler weaponReloadHandler;
	private ParticleSpawner particleSpawner;
	
	@Override
	public void onEnable() {
		//create config if first launch
		this.saveDefaultConfig();
		
		//load config
		ConfigManager.loadConfig(this.getConfig());
		
		//create healtheffects and bullet handler instance
		healthEffects = new HealthEffects();
		bulletHandler = new AsyncBulletHandler();
		weaponReloadHandler = new WeaponReloadHandler();
		particleSpawner = new ParticleSpawner();
		ConsumableManager.startConsumableRunnable();
		GrenadeManager.startGrenadeHandler();
		RefillableManager.startRefillableRunnable();
		
		//load listeners
		Bukkit.getPluginManager().registerEvents(bulletHandler, this);
		Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
		Bukkit.getPluginManager().registerEvents(new ItemListener(), this);
		Bukkit.getPluginManager().registerEvents(new ConsumableListener(), this);
		Bukkit.getPluginManager().registerEvents(new GrenadeListener(), this);
		Bukkit.getPluginManager().registerEvents(new MeleeListener(), this);
		Bukkit.getPluginManager().registerEvents(new RepairListener(), this);
		Bukkit.getPluginManager().registerEvents(new WaterBarManager(), this);
		Bukkit.getPluginManager().registerEvents(new MoveListener(), this);
		Bukkit.getPluginManager().registerEvents(new RefillableListener(), this);
		Bukkit.getPluginManager().registerEvents(new BackpackListener(), this);
		Bukkit.getPluginManager().registerEvents(new EquipListener(), this);
		Bukkit.getPluginManager().registerEvents(healthEffects, this);
		Bukkit.getPluginManager().registerEvents(weaponReloadHandler, this);
		
		//commands
		this.getCommand("wf").setExecutor(new CommandManager());
		
		//load files
		WeaponLoader.loadFiles();
		ConsumableLoader.loadFiles();
		GrenadeLoader.loadFiles();
		ArmorLoader.loadFiles();
		MeleeLoader.loadFiles();
		RepairLoader.loadFiles();
		RefillableLoader.loadFiles();
		BackpackLoader.loadFiles();
		
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
	
	public ParticleSpawner getParticleSpawner() {
		return particleSpawner;
	}
}
