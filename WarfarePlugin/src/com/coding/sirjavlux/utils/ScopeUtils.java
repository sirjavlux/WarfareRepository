package com.coding.sirjavlux.utils;

import org.bukkit.entity.Player;

public class ScopeUtils {

	public static void scope(Player p, float amount) {
		p.setWalkSpeed((float) (amount * -1));
	}
	
	public static void unscope(Player p) {
		p.setWalkSpeed(0.2f);
	}
	
	public static boolean isScoped(Player p) {
		return p.getWalkSpeed() != 0.2f ? true : false;
	}
}
