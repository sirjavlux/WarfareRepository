package com.coding.sirjavlux.core;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.coding.sirjavlux.projectiles.ProjectileManager;
import com.coding.sirjavlux.utils.Color;

public class Main extends JavaPlugin implements Listener {
	
	private static Main instance;
	
	@Override
	public void onEnable() {
		//create config if first launch
		this.saveDefaultConfig();
		
		//load listeners
		Bukkit.getPluginManager().registerEvents(this, this);
		Bukkit.getPluginManager().registerEvents(new DamageListener(), this);
		
		//load weapon files
		WeaponLoader.loadFiles();
		
		instance = this;
		
		System.out.println(Color.GREEN + "Warfare successfully enabled!" + Color.RESET);
	}
	
	@Override
	public void onDisable() {
		
		System.out.println(Color.RED + "Warfare disabled!" + Color.RESET);
	}
	
	@EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        
        if (e.getMaterial() != Material.SNOWBALL)
            return;
        
        Player p = e.getPlayer();
        e.setCancelled(true);
		ProjectileManager.fireProjectile(p);
	}
	
	public static Main getInstance() {
		return instance;
	}
}
