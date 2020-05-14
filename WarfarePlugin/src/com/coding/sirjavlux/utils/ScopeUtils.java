package com.coding.sirjavlux.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

public class ScopeUtils {

	public static List<UUID> scopedPlayers = new ArrayList<>();
	
	public static void scope(Player p, float amount) {
		p.setWalkSpeed((float) (amount * -1));
		scopedPlayers.add(p.getUniqueId());
	}
	
	public static void unscope(Player p) {
		p.setWalkSpeed(0.2f);
		scopedPlayers.remove(p.getUniqueId());
	}
	
	public static boolean isScoped(Player p) {
		return scopedPlayers.contains(p.getUniqueId());
	}
}
