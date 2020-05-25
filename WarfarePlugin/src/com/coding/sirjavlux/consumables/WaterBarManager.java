package com.coding.sirjavlux.consumables;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.coding.sirjavlux.core.ConfigManager;
import com.coding.sirjavlux.core.Main;
import com.coding.sirjavlux.health.HealthEffects;

public class WaterBarManager implements Listener {

	public WaterBarManager() {
		startWaterBarReducer();
	}
	
	@EventHandler
	public void playerJoinEvent(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if (!p.hasPlayedBefore()) {
			p.setLevel(100);
			p.setExp(1);
		}
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
		p.setLevel(100);
		p.setExp(1);
	}
	
	public static void addWater(Player p, double amount) {
		int level = (int) amount;
		double exp = amount - level;
		int pLevels = p.getLevel();
		double pExp = p.getExp();
		exp += pExp;
		level += (int) exp;
		exp = exp > 1 ? exp - 1 : exp;
		p.setExp((float) exp);
		p.setLevel(level + pLevels > 100 ? 100 : level + pLevels);
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
					double velLenght = p.getVelocity().lengthSquared();
					red += HealthEffects.isBleeding(uuid) ? red * bleedingRed : 0;
					red += (red * velRed) * velLenght;
					//reduce exp and levels
					int pLevels = p.getLevel();
					double exp = p.getExp();
					int redLevels = (int) Math.round(red);
					double expRed = red - redLevels;
					double finalExp = exp - expRed;
					if (finalExp < 0) {
						redLevels++;
						finalExp = 1 + finalExp;
					}
					//damage player
					if (pLevels < redLevels) {
						p.setLevel(0);
						p.setExp(0);
						p.damage(ConfigManager.getWaterHealthRed());
					}
					//reduce exp and level
					else {
						p.setLevel(pLevels - redLevels);
						p.setExp((float) finalExp);
					}
				}
			}
		}.runTaskTimer(Main.getPlugin(Main.class), runnableSpeed, runnableSpeed);
	}
}
