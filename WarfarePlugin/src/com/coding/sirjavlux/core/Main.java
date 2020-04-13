package com.coding.sirjavlux.core;

import org.bukkit.plugin.java.JavaPlugin;

import com.coding.sirjavlux.utils.Color;

public class Main extends JavaPlugin {
	
	@Override
	public void onEnable() {
		
		System.out.println(Color.GREEN + "Warfare successfully enabled!" + Color.RESET);
	}
	
	@Override
	public void onDisable() {
		
		System.out.println(Color.RED + "Warfare disabled!" + Color.RESET);
	}
}
