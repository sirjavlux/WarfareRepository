package com.coding.sirjavlux.projectiles;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MoveListener implements Listener {

	private static HashMap<UUID, Double> speeds = new HashMap<>();
	
	@EventHandler
	public void moveEvent(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		double speed = e.getFrom().distance(e.getTo());
		speeds.replace(uuid, speed);
	}
	
	@EventHandler
	public void leaveEvent(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		if (speeds.containsKey(uuid)) speeds.remove(uuid);
	}
	
	@EventHandler
	public void joinEvent(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		if (!speeds.containsKey(uuid)) speeds.put(uuid, 0d);
	}
	
	public static double getPlayerSpeed(UUID uuid) {
		return speeds.containsKey(uuid) ? speeds.get(uuid) : 0;
	}
}
