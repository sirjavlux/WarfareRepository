package com.coding.sirjavlux.consumables;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.coding.sirjavlux.core.ConfigManager;
import com.coding.sirjavlux.core.Main;
import com.coding.sirjavlux.health.HealthEffects;
import com.coding.sirjavlux.projectiles.MoveListener;

public class WaterBarManager implements Listener {

	private static HashMap<UUID, Double> levels = new HashMap<>();
	
	public WaterBarManager() {
		startWaterBarReducer();
	}
	
	@EventHandler
	public void playerJoinEvent(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		if (!p.hasPlayedBefore()) {
			p.setLevel(100);
			p.setExp(0);
		}
		if (!levels.containsKey(uuid)) levels.put(uuid, p.getLevel() + 0.9d);
	}
	
	@EventHandler 
	public void leaveEvent(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		if (levels.containsKey(uuid)) levels.remove(uuid);
	}
	
	@EventHandler
	public void entityDeathEvent(EntityDeathEvent e) {
		e.setDroppedExp(0);
	}
	
	@EventHandler
	public void blockBreakEvent(BlockBreakEvent e) {
		e.setExpToDrop(0);
	}
	
	@EventHandler
	public void playerRespawnEvent(PlayerRespawnEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		p.setLevel(100);
		p.setExp(0);
		levels.replace(uuid, 100.9d);
	}
	
	public static void addWater(Player p, double amount) {
		UUID uuid = p.getUniqueId();
		double currentExp = levels.get(uuid);
		double finalExp = currentExp + amount > 100.9 ? 100.9 : currentExp + amount;
		levels.replace(uuid, finalExp);
		p.setExp(0);
		p.setLevel((int) levels.get(uuid).doubleValue());
	}
	
	private void startWaterBarReducer() {
		final int runnableSpeed = 80;
		new BukkitRunnable() {
			final double bleedingRed = ConfigManager.getWaterBleedingRed();
			final double waterSecRed = ConfigManager.getWaterRedSec() * (runnableSpeed / 20);
			final double velRed = ConfigManager.getWaterVelRed();
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					UUID uuid = p.getUniqueId();
					double red = waterSecRed;
					double speed = MoveListener.getPlayerSpeed(uuid) * 3;
					red += HealthEffects.isBleeding(uuid) ? red * bleedingRed : 0;
					red += (red * velRed) * speed;
					double currentExp = levels.get(uuid);
					//damage player
					if (red > currentExp) {
						levels.replace(uuid, 0d);
						p.setLevel(0);
						p.setExp(0);
						p.damage(ConfigManager.getWaterHealthRed());
					}
					//reduce exp and level
					else {
						levels.replace(uuid, currentExp - red);
						p.setLevel((int) levels.get(uuid).doubleValue());
						p.setExp(0);
					}
				}
			}
		}.runTaskTimer(Main.getPlugin(Main.class), runnableSpeed, runnableSpeed);
	}
}
