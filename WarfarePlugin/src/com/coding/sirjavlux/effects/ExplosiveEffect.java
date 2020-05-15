package com.coding.sirjavlux.effects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.coding.sirjavlux.core.Main;
import com.coding.sirjavlux.effectUtils.EffectParticle;
import com.coding.sirjavlux.events.EntityDamagedByEffectEvent;
import com.coding.sirjavlux.utils.FormulaUtils;

import net.minecraft.server.v1_15_R1.PacketPlayOutEntityStatus;

public class ExplosiveEffect implements Effect {

	private double damage, radius, height, damageRed;
	private int duration, fireTicks;
	private List<EffectParticle> particles;
	private Location loc;
	private List<DamageCuboid> damageLocations;
	private List<LivingEntity> entities;
	private LivingEntity entity;
	private UUID uuid;
	private boolean hasDamaged;
	
	public ExplosiveEffect(Location loc, double damage, double radius, double height, int fireTicks, double damageRed, LivingEntity entity) {
		this.damage = damage;
		this.radius = radius;
		this.height = height;
		this.fireTicks = fireTicks;
		this.duration = 0;
		this.loc = loc;
		this.damageRed = damageRed;
		this.entity = entity;
		this.hasDamaged = false;
		particles = new ArrayList<>();
		damageLocations = new ArrayList<>();
		entities = new ArrayList<>();
		uuid = UUID.randomUUID();
	}
	
	private Effect getEffect() {
		return this;
	}
	
	@Override
	public void playEffect() {
		new BukkitRunnable() {
			@Override
			public void run() {
				int intensity = (int) (9d * radius);
				
				HashMap<Vector, List<EffectParticle>> particleMap = new HashMap<>();
				//create start particles
				for (int i = 0; i < intensity; i++) {
					EffectParticle particle = new EffectParticle(loc.clone().add(0, 0.3, 0), Particle.REDSTONE, 0, uuid);
					particle.setDustOptions(new Particle.DustOptions(Color.WHITE, 1));
					Vector dir = (loc.clone().add(0, 1, 0).toVector()).subtract(FormulaUtils.getRandomLocation(loc.clone().add(0, 1, 0), 2).toVector()).normalize();
					List<EffectParticle> newParticleList = new ArrayList<>();
					newParticleList.add(particle);
					particleMap.put(dir, newParticleList);
				}
				int delay = 0;
				int count = 0;
				int explosionSize = (int) (((radius * (double) intensity) / 2.4));
				int rgb = 255;
				//create additional follow up particles
				for (int i = 0; i < explosionSize; i++) {
					if (count % 6 == 0) {
						delay++;
					}
					for (Entry<Vector, List<EffectParticle>> entry : particleMap.entrySet()) {
						Vector dir = entry.getKey().clone();
						List<EffectParticle> list = entry.getValue();
						EffectParticle lastParticle = list.get(list.size() - 1);
						
						Location partLoc = lastParticle.getLocation().clone();
						partLoc = partLoc.toVector().add(new Vector(dir.getX() / 5, dir.getY() / 10, dir.getZ() / 5)).toLocation(loc.getWorld());
						EffectParticle newParticle = new EffectParticle(partLoc, Particle.REDSTONE, 0, uuid);
						newParticle.setDustOptions(new Particle.DustOptions(Color.fromBGR(rgb, rgb, rgb), (float) (0.4 + (delay / 5.8))));
						newParticle.setDelay(delay);
						
						list.add(newParticle);
						particleMap.replace(dir, list);
					}
					count++;
					rgb -= rgb * (0.03 * delay);
				}
				//get all particles
				List<EffectParticle> particles = new ArrayList<>();
				for (Entry<Vector, List<EffectParticle>> entry : particleMap.entrySet()) particles.addAll(entry.getValue());
				//create directional explosion particles
				for (int i = 0; i < intensity; i++) {
					for (int i2 = 0; i2 < 2; i2++) {
						Vector dir = (loc.clone().add(0, 1, 0).toVector()).subtract(FormulaUtils.getRandomLocation(loc.clone().add(0, 1, 0), 2).toVector()).normalize().multiply(0.1);
						loc.getWorld().spawnParticle(Math.random() > 0.3 ? Particle.SMOKE_NORMAL : Particle.SMOKE_LARGE, loc, 0, dir.getX(), dir.getY(), dir.getZ());
					}
				}
				setParticles(particles);
				
				Main.getInstance().getParticleSpawner().addEffect(getEffect());
			}
		}.runTaskAsynchronously(Main.getInstance());
	}

	@Override
	public void damageEntities() {
		if (!hasDamaged) {
			hasDamaged = true;
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
