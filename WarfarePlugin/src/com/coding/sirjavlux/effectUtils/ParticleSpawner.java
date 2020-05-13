package com.coding.sirjavlux.effectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.coding.sirjavlux.core.Main;
import com.coding.sirjavlux.effects.Effect;

public class ParticleSpawner {
	
	public ParticleSpawner() {
		startEffectSpawner();
		startFlameProjectileEffectSpawner();
	}
	
	HashMap<UUID, Effect> effects = new HashMap<>();
	private void startEffectSpawner() {
		new BukkitRunnable() {
			@Override
			public void run() {
				//play effects
				HashMap<UUID, Effect> particleMap = new HashMap<>();
				for (Entry<UUID, Effect> entry : effects.entrySet()) {
					UUID particleUUID = entry.getKey();
					Effect effect = entry.getValue();
					List<EffectParticle> particles = effect.getParticles();
					List<EffectParticle> newParticles = new ArrayList<>();
					for (EffectParticle particle : particles) {
						if (particle.getDelay() < 1) spawnParticle(particle);
						else {
							particle.setDelay(particle.getDelay() - 1);
							newParticles.add(particle);
						}
					}
					effect.setParticles(newParticles);
					//check for nearby players and update and potentially damage them
					List<LivingEntity> entityList = new ArrayList<>();
					//if specific location based
					if (!effect.getDamageLocations().isEmpty()) {
						for (Location loc : effect.getDamageLocations()) {
							for (Entity entity : loc.getWorld().getNearbyEntities(loc.getBlock().getLocation().add(0.5, 0, 0.5), 0.7, 0.5, 0.7)) {
								if (entity instanceof LivingEntity) entityList.add((LivingEntity) entity);
							}
						}
					} 
					//if radius based
					else {
						for (Entity entity : effect.getLocation().getWorld().getNearbyEntities(effect.getLocation(), effect.getRadius(), effect.getHeight(), effect.getRadius())) {
							if (entity instanceof LivingEntity) entityList.add((LivingEntity) entity);
						}
					}
					effect.setEntities(entityList);
					effect.damageEntities();
					particleMap.put(particleUUID, effect);
				}
				for (Entry<UUID, Effect> entry : particleMap.entrySet()) {
					UUID particleUUID = entry.getKey();
					Effect effect = entry.getValue();
					List<EffectParticle> particles = effect.getParticles();
					if (!particles.isEmpty()) effects.replace(particleUUID, effect);
					else effects.remove(particleUUID);	
				}
			}
		}.runTaskTimer(Main.getPlugin(Main.class), 1, 1);
	}
	
	public void addEffect(Effect effect) {
		effects.put(UUID.randomUUID(), effect);
	}
	
	private HashMap<UUID, UUID> projectiles = new HashMap<>();
	
	public void addFlameProjectileEffect(com.coding.sirjavlux.projectiles.Projectile projectile) {
		projectiles.put(projectile.getUniqueID(), ((Player) ((Projectile) projectile.getBukkitEntity()).getShooter()).getUniqueId());
	}
	
	private void startFlameProjectileEffectSpawner() {
		new BukkitRunnable() {
			@Override
			public void run() {
				List<UUID> removeProjectiles = new ArrayList<>();
				for (Entry<UUID, UUID> entry : new HashMap<>(projectiles).entrySet()) {
					UUID projectilesUUID = entry.getKey();
					UUID playerUUID = entry.getValue();
					if (Bukkit.getEntity(projectilesUUID) != null) {
						Entity projectile = Bukkit.getEntity(projectilesUUID);
						Location loc = projectile.getLocation();
						if (Bukkit.getOfflinePlayer(playerUUID).isOnline()) if (Bukkit.getPlayer(playerUUID).getEyeLocation().distance(loc) < 0.4) continue;
						loc.getWorld().spawnParticle(Particle.FLAME, loc, 0, 0, 0, 0);
						continue;
					}
					removeProjectiles.add(projectilesUUID);
				}
				for (UUID projectile : removeProjectiles) {
					projectiles.remove(projectile);
				}
			}
		}.runTaskTimerAsynchronously(Main.getPlugin(Main.class), 1, 1);
	}
	
	private void spawnParticle(EffectParticle particle) {
		//if redstone particle
		if (particle.getParticle() == Particle.REDSTONE) {
			particle.getLocation().getWorld().spawnParticle(Particle.REDSTONE, particle.getLocation(), particle.getCount(), particle.getDustOptions());
		}
		//spell mob particle
		else if (particle.getParticle() == Particle.SPELL_MOB) {
			particle.getLocation().getWorld().spawnParticle(Particle.SPELL_MOB, particle.getLocation(), particle.getCount(), particle.getRGB()[0], particle.getRGB()[1], particle.getRGB()[2], 1);
		}
		//if note particle
		else if (particle.getParticle() == Particle.NOTE) {
			particle.getLocation().getWorld().spawnParticle(Particle.NOTE, particle.getLocation(), particle.getCount(), particle.getNote(), 0, 0, 1);
		}
		//if crack particle
		else if (particle.getParticle().name().contains("CRACK")) {
			particle.getLocation().getWorld().spawnParticle(Particle.ITEM_CRACK, particle.getLocation(), particle.getCount(), new ItemStack(particle.getMaterial()));
		}
		//if dust particle
		else if (particle.getParticle().name().contains("DUST")) {
			particle.getLocation().getWorld().spawnParticle(Particle.FALLING_DUST, particle.getLocation(), particle.getCount(), particle.getMaterial().createBlockData());
		}
		//regular particle
		else {
			particle.getLocation().getWorld().spawnParticle(particle.getParticle(), particle.getLocation(), particle.getCount(), particle.getOffset()[0], particle.getOffset()[1], particle.getOffset()[2], particle.getExtra());
		}	
	}
}
