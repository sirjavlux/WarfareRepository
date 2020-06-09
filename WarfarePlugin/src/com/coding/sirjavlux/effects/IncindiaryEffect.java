package com.coding.sirjavlux.effects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.coding.sirjavlux.core.Main;
import com.coding.sirjavlux.effectUtils.EffectParticle;
import com.coding.sirjavlux.events.EntityDamagedByEffectEvent;
import com.coding.sirjavlux.events.WarfareDeathEvent;
import com.coding.sirjavlux.events.WarfareDeathEvent.WarfareDeathCause;

import net.minecraft.server.v1_15_R1.PacketPlayOutEntityStatus;

public class IncindiaryEffect implements Effect {

	private double damage, radius, height, damageRed;
	private int duration, fireTicks, cooldown, startCooldown, intensity;
	private List<EffectParticle> particles;
	private Location loc;
	private List<DamageCuboid> damageLocations;
	private List<LivingEntity> entities;
	private LivingEntity entity;
	private UUID uuid;
	
	public IncindiaryEffect(Location loc, int duration, double damage, double radius, double height, int fireTicks, int intensity, double damageRed, LivingEntity entity) {
		this.damage = damage;
		this.radius = radius;
		this.height = height;
		this.fireTicks = fireTicks;
		this.duration = duration;
		this.loc = loc;
		this.intensity = intensity;
		this.damageRed = damageRed;
		this.entity = entity;
		particles = new ArrayList<>();
		damageLocations = new ArrayList<>();
		entities = new ArrayList<>();
		uuid = UUID.randomUUID();
		startCooldown = 1;
		cooldown = startCooldown;
	}
	
	private Effect getEffect() {
		return this;
	}
	
	@Override
	public void playEffect() {
		new BukkitRunnable() {
			@Override
			public void run() {
				int startIntensity = intensity;
				Location center = loc.clone();
				int delay = 60;
				for (int i = 0; i < (duration < delay ? delay : duration) / delay; i++) {
					for (double h = 0; h < height; h += (height / (double) startIntensity) * ((radius * 2) / height)) {
						for (double r = 0; r < radius * (height < 1 ? 1 : (h / height < 0.3 ? h / (height / (1 + 2 * (1 - h / height))) : (1 - (h / height) / (1 + 2 * (1 - h / height))))); r += (radius / (double) startIntensity)) {
							int intensity = (int) ((double) startIntensity * (r / radius));
						    double increment = (2 * Math.PI) / (double) intensity;
						    for (int i2 = 0; i2 < intensity; i2++) {
								Location tempCenter = center.clone();
						        double angle = (i2 * increment) * (1 - Math.random() / 3);
						        double x = (r * (1 - Math.random() / 2)) * Math.cos(angle);
						        double z = (r * (1 - Math.random() / 2)) * Math.sin(angle);
						        tempCenter.add(x, h, z);
						        if (tempCenter.getBlock().getType().equals(Material.AIR) || tempCenter.getBlock().isPassable() || tempCenter.getBlock().isLiquid()) {
									particles.add(new EffectParticle(tempCenter, Particle.FLAME, i * delay, UUID.randomUUID()));	
						        }
						    }
						}
					}
				}
				Main.getInstance().getParticleSpawner().addEffect(getEffect());
			}
		}.runTaskAsynchronously(Main.getInstance());
	}

	@Override
	public void damageEntities() {
		cooldown--;
		if (cooldown < 1) {
			for (LivingEntity entity : entities) {
				if (entity instanceof Player) if (!((Player) entity).isOnline()) continue; //continue if player and is not online
				if (entity.getHealth() > 0 && (damage > 0 || fireTicks > 0)) {
					double damage = this.damage;
					double distance = entity.getLocation().distance(loc) * 1.4;
					double modifier = (1d / (1d - damageRed)) / Math.pow(1d / (1d - damageRed), 1 + distance);
					damage = damage * modifier;
					EntityDamagedByEffectEvent event = new EntityDamagedByEffectEvent(entity, this.entity, damage);
					if (!event.isCancelled()) {
						damage = event.getDamage();
						double finalHealth = entity.getHealth() - damage < 0 ? 0 : entity.getHealth() - damage;
						entity.setHealth(finalHealth);
						if (finalHealth <= 0) {
							WarfareDeathEvent wEvent = new WarfareDeathEvent((LivingEntity) entity, (LivingEntity) event.getDamager(), WarfareDeathCause.Effect);
							Bukkit.getPluginManager().callEvent(wEvent);
							if (!wEvent.isCancelled() && !wEvent.getDeathMessage().isEmpty()) for (Player p : Bukkit.getOnlinePlayers()) p.sendMessage(wEvent.getDeathMessage());
						}
						entity.setFireTicks(fireTicks);
						//fire status packet
						net.minecraft.server.v1_15_R1.Entity ce = ((CraftEntity) entity).getHandle();
						PacketPlayOutEntityStatus statusPacket = new PacketPlayOutEntityStatus(ce, (byte) 2);
						for (Player p : Bukkit.getOnlinePlayers()) {
							if (entity.getLocation().distance(p.getLocation()) < 30) ((CraftPlayer) p).getHandle().playerConnection.sendPacket(statusPacket);
						}	
					}
				}
			}
			cooldown = startCooldown;
		}
	}

	@Override
	public double getDamage() {
		return damage;
	}

	@Override
	public double getRadius() {
		return radius;
	}

	@Override
	public double getHeight() {
		return height;
	}

	@Override
	public void setDamage(double damage) {
		this.damage = damage;
	}

	@Override
	public void setRadius(double radius) {
		this.radius = radius;
	}

	@Override
	public void setHeight(double height) {
		this.height = height;
	}

	@Override
	public int getPlayerFireTicks() {
		return this.fireTicks;
	}

	@Override
	public void setPlayerFireTicks(int ticks) {
		this.fireTicks = ticks;
	}

	@Override
	public List<LivingEntity> getEntities() {
		return this.entities;
	}

	@Override
	public void setEntities(List<LivingEntity> entities) {
		this.entities = entities;
	}

	@Override
	public UUID getUniqueId() {
		return uuid;
	}

	@Override
	public int getEffectDuration() {
		return duration;
	}
	
	@Override
	public void setEffectDuration(int duration) {
		this.duration = duration;
	}

	@Override
	public List<EffectParticle> getParticles() {
		return particles;
	}

	@Override
	public void setParticles(List<EffectParticle> particles) {
		this.particles = particles;
	}

	@Override
	public Location getLocation() {
		return this.loc;
	}

	@Override
	public List<DamageCuboid> getDamageLocations() {
		return this.damageLocations;
	}
}
