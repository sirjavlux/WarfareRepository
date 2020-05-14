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

import net.minecraft.server.v1_15_R1.PacketPlayOutEntityStatus;

public class SmokeEffect implements Effect {

	private double damage, radius, height;
	private int duration, fireTicks, cooldown, startCooldown;
	private List<EffectParticle> particles;
	private Location loc;
	private List<DamageCuboid> damageLocations;
	private List<LivingEntity> entities;
	private UUID uuid;
	
	public SmokeEffect(Location loc, int duration, double damage, double radius, double height, int fireTicks) {
		this.damage = damage;
		this.radius = radius;
		this.height = height;
		this.fireTicks = fireTicks;
		this.duration = duration;
		this.loc = loc;
		particles = new ArrayList<>();
		damageLocations = new ArrayList<>();
		entities = new ArrayList<>();
		uuid = UUID.randomUUID();
		startCooldown = 4;
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
				int startIntensity = 20;
				Location center = loc.clone();
				int delay = 60;
				for (int i = 0; i < (duration < delay ? delay : duration) / delay; i++) {
					for (double h = 0; h < height; h += (height / (double) startIntensity) * ((radius * 2) / height)) {
						for (double r = 0; r < radius * (h / height < 0.3 ? h / (height / (1 + 2 * (1 - h / height))) : (1 - (h / height) / (1 + 2 * (1 - h / height)))); r += (radius / (double) startIntensity)) {
							int intensity = (int) ((double) startIntensity * (r / radius));
						    double increment = (2 * Math.PI) / (double) intensity;
						    for (int i2 = 0; i2 < intensity; i2++) {
								Location tempCenter = center.clone();
						        double angle = (i2 * increment) * (1 - Math.random() / 3);
						        double x = (r * (1 - Math.random() / 2)) * Math.cos(angle);
						        double z = (r * (1 - Math.random() / 2)) * Math.sin(angle);
						        tempCenter.add(x, h, z);
						        if (tempCenter.getBlock().getType().equals(Material.AIR) || tempCenter.getBlock().isPassable()) {
									particles.add(new EffectParticle(tempCenter.subtract(0, 0.5, 0), Particle.CAMPFIRE_SIGNAL_SMOKE, i * delay, UUID.randomUUID()));	
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
					double finalHealth = entity.getHealth() - damage < 0 ? 0 : entity.getHealth() - damage;
					entity.setHealth(finalHealth);
					entity.setFireTicks(fireTicks);
					//fire status packet
					net.minecraft.server.v1_15_R1.Entity ce = ((CraftEntity) entity).getHandle();
					PacketPlayOutEntityStatus statusPacket = new PacketPlayOutEntityStatus(ce, (byte) 2);
					for (Player p : Bukkit.getOnlinePlayers()) {
						if (entity.getLocation().distance(p.getLocation()) < 30) ((CraftPlayer) p).getHandle().playerConnection.sendPacket(statusPacket);
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
