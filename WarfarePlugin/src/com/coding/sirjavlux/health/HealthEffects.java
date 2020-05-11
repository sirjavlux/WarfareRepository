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
				HashMap<UUID, Integer> newConcussionTime = new HashMap<>();
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
						PotionEffect effect = new PotionEffect(type, 24, ConfigManager.getBrokenLegStrenght(), true, false, false);
						if (!containsValidEffect) p.addPotionEffect(effect);
						newConcussionTime.put(entry.getKey(), time - 1);
					}
				}
				//replace updated concussions
				for (Entry<UUID, Integer> entry : newConcussionTime.entrySet()) {
					UUID uuid = entry.getKey();
					int time = entry.getValue();
					if (concussionTime.containsKey(uuid)) {
						if (concussionTime.get(uuid) > time) {
							concussionTime.replace(uuid, time);
						}
					}
				}
				
				//broken leg effect
				HashMap<UUID, Integer> newBrokenLegTime = new HashMap<>();
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
						newBrokenLegTime.put(entry.getKey(), time - 1);
					}
				}
				//replace updated broken bones
				for (Entry<UUID, Integer> entry : newBrokenLegTime.entrySet()) {
					UUID uuid = entry.getKey();
					int time = entry.getValue();
					if (brokenLegTime.containsKey(uuid)) {
						if (brokenLegTime.get(uuid) > time) {
							brokenLegTime.replace(uuid, time);
						}
					}
				}
				
				//bleeding
				HashMap<UUID, List<Bleeding>> newBleeding = new HashMap<>();
				for (Entry<UUID, List<Bleeding>> entry: bleedingMap.entrySet()) {
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
						bleedingList.add(bleeding);
						if (finalHealth == 0) break;
					}
					if (finalHealth > 0) newBleeding.put(uuid, bleedingList);
				}
				//replace updated bleeding
				for (Entry<UUID, List<Bleeding>> entry : newBleeding.entrySet()) {
					UUID uuid = entry.getKey();
					List<Bleeding> bleedingList = entry.getValue();
					if (bleedingMap.containsKey(uuid)) {
						List<Bleeding> oldBleeding = new ArrayList<>(bleedingMap.get(uuid));
						List<Bleeding> finalBleeding = new ArrayList<>();
						for (Bleeding bleeding : bleedingList) {
							UUID bUUID = bleeding.getUniqueID();
							int count = 0;
							for (Bleeding old : oldBleeding) {
								if (bUUID.equals(old.getUniqueID())) oldBleeding.set(count, bleeding);
								if (old.getTime() > 0) finalBleeding.add(oldBleeding.get(count));
								count++;
							}
						}
						bleedingMap.replace(uuid, finalBleeding);
					}
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
		if (entity instanceof Player && bleedingMap.containsKey(uuid)) {
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
	
	public static void removeBrokenLeg(Player p) {
		UUID uuid = p.getUniqueId();
		if (brokenLegTime.containsKey(uuid)) {
			brokenLegTime.replace(uuid, 0);
		}
	}
	
	public static void removeConcussion(Player p) {
		UUID uuid = p.getUniqueId();
		if (concussionTime.containsKey(uuid)) {
			concussionTime.replace(uuid, 0);
		}
	}
	
	public static void removeBleeding(LivingEntity entity) {
		UUID uuid = entity.getUniqueId();
		if (bleedingMap.containsKey(uuid)) {
			bleedingMap.replace(uuid, new ArrayList<>());
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
			if (brokenLegTime.containsKey(uuid)) brokenLegTime.replace(uuid, 0);
			if (concussionTime.containsKey(uuid)) concussionTime.replace(uuid, 0);
		} else {
			if (bleedingMap.containsKey(uuid)) bleedingMap.remove(uuid);
		}
	}
	
	private static class Bleeding {
		private int time;
		private double damage;
		private int cooldown;
		private UUID uuid;
		
		public Bleeding(int time, double damage) {
			this.time = time;
			this.damage = damage;
			this.cooldown = ConfigManager.getTimeBetweanBleeding();
			this.uuid = UUID.randomUUID();
		}
		
		public int getTime() { return time; }
		public double getDamage() { return damage; }
		public void setTime(int time) { this.time = time; }
		public int getCooldown() { return cooldown; }
		public void setCooldown(int cooldown) { this.cooldown = cooldown; }
		public UUID getUniqueID() { return uuid; }
	}
}