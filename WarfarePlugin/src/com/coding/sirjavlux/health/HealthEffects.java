package com.coding.sirjavlux.health;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.coding.sirjavlux.core.ConfigManager;
import com.coding.sirjavlux.core.Main;

public class HealthEffects implements Listener {
	
	private static HashMap<UUID, Integer> concussionTime = new HashMap<>();
	private static HashMap<UUID, Integer> brokenLegTime = new HashMap<>();
	private static HashMap<UUID, List<Bleeding>> bleedingMap = new HashMap<>();
	
	public HealthEffects() {
		startHealthReader();
	}
	
	private void startHealthReader() {
		new BukkitRunnable() {
			@Override
			public void run() {
				//concussion effect
				for (Entry<UUID, Integer> entry: concussionTime.entrySet()) {
					int time = entry.getValue();
					if (time > 0) {
						Player p = Bukkit.getPlayer(entry.getKey());
						if (p == null) continue;
						boolean containsValidEffect = false;
						PotionEffectType type = PotionEffectType.BLINDNESS;
						if (p.hasPotionEffect(type)) {
							if (p.getPotionEffect(type).getDuration() > 1) {
								containsValidEffect = true;
							}
						}
						PotionEffect effect = new PotionEffect(type, 15, ConfigManager.getBrokenLegStrenght(), true, false, false);
						if (!containsValidEffect) p.addPotionEffect(effect);
					}
				}
				//broken leg effect
				for (Entry<UUID, Integer> entry: brokenLegTime.entrySet()) {
					int time = entry.getValue();
					if (time > 0) {
						Player p = Bukkit.getPlayer(entry.getKey());
						if (p == null) continue;
						boolean containsValidEffect = false;
						PotionEffectType type = PotionEffectType.SLOW;
						if (p.hasPotionEffect(type)) {
							if (p.getPotionEffect(type).getDuration() > 1) {
								containsValidEffect = true;
							}
						}
						PotionEffect effect = new PotionEffect(type, 15, ConfigManager.getBrokenLegStrenght(), true, false, false);
						if (!containsValidEffect) p.addPotionEffect(effect);
					}
				}
				//bleeding
				HashMap<UUID, List<Bleeding>> cloneBleedingMap = new HashMap<>(bleedingMap);
				for (Entry<UUID, List<Bleeding>> entry: cloneBleedingMap.entrySet()) {
					List<Bleeding> bleedingList = new ArrayList<>();
					UUID uuid = entry.getKey();
					LivingEntity entity = (LivingEntity) Bukkit.getEntity(uuid);
					if (entity == null) continue;
					double health = entity.getHealth();
					double finalHealth = health;
					for (Bleeding bleeding : entry.getValue()) {
						bleeding.setTime(bleeding.getTime() - 1);
						bleeding.setCooldown(bleeding.getCooldown() - 1);
						if (bleeding.cooldown < 1) {
							double damage = bleeding.getDamage();
							finalHealth = health - damage < 0 ? 0 : health - damage;
							entity.setHealth(finalHealth);
							bleeding.setCooldown(ConfigManager.getTimeBetweanBleeding());
						} 
						if (bleeding.getTime() > 0) bleedingList.add(bleeding);
						if (finalHealth == 0) break;
					}
					if (finalHealth > 0) entry.setValue(bleedingList);
				}
			}
		}.runTaskTimer(Main.getPlugin(Main.class), 1, 1);
	}
	
	public static void breakLeg(Player p) {
		UUID uuid = p.getUniqueId();
		brokenLegTime.replace(uuid, ConfigManager.getBrokenLegTime());
	}
	
	public static void concussion(Player p) {
		UUID uuid = p.getUniqueId();
		concussionTime.replace(uuid, ConfigManager.getConcussionTime());
	}
	
	public static void addBleeding(LivingEntity entity, double damage) {
		UUID uuid = entity.getUniqueId();
		if (entity instanceof Player) {
			List<Bleeding> list = bleedingMap.get(uuid);
			list.add(new Bleeding(ConfigManager.getBleedingTime(), damage));
			bleedingMap.replace(uuid, list);
		} else if (!ConfigManager.getOnlyPlayerBleeding()) {
			if (!bleedingMap.containsKey(uuid)) bleedingMap.put(uuid, new ArrayList<Bleeding>());
			List<Bleeding> list = bleedingMap.get(uuid);
			list.add(new Bleeding(ConfigManager.getBleedingTime(), damage));
			bleedingMap.replace(uuid, list);
		}
	}
	
	@EventHandler
	public void playerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		if (!concussionTime.containsKey(uuid)) concussionTime.put(uuid, 0);
		if (!brokenLegTime.containsKey(uuid)) brokenLegTime.put(uuid, 0);
		if (!bleedingMap.containsKey(uuid)) bleedingMap.put(uuid, new ArrayList<Bleeding>());
	}
	
	@EventHandler
	public void playerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		if (concussionTime.get(uuid) < 1) concussionTime.remove(uuid);
		if (brokenLegTime.get(uuid) < 1) brokenLegTime.remove(uuid);
		if (bleedingMap.get(uuid).size() < 1) bleedingMap.remove(uuid);
	}
	
	@EventHandler
	public void entityKilledEvent(EntityDeathEvent e) {
		LivingEntity entity = e.getEntity();
		UUID uuid = entity.getUniqueId();
		if (entity instanceof Player) {
			if (bleedingMap.containsKey(uuid)) bleedingMap.replace(uuid, new ArrayList<Bleeding>());
			if (brokenLegTime.get(uuid) > 0) brokenLegTime.replace(uuid, 0);
			if (concussionTime.get(uuid) > 0) concussionTime.replace(uuid, 0);
		} else {
			if (bleedingMap.containsKey(uuid)) bleedingMap.remove(uuid);
		}
	}
	
	private static class Bleeding {
		private int time;
		private double damage;
		private int cooldown;
		
		public Bleeding(int time, double damage) {
			this.time = time;
			this.damage = damage;
			this.cooldown = ConfigManager.getTimeBetweanBleeding();
		}
		
		public int getTime() { return time; }
		public double getDamage() { return damage; }
		public void setTime(int time) { this.time = time; }
		public int getCooldown() { return cooldown; }
		public void setCooldown(int cooldown) { this.cooldown = cooldown; }
	}
}