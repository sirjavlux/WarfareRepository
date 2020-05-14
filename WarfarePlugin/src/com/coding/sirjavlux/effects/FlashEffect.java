package com.coding.sirjavlux.effects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.coding.sirjavlux.core.Main;
import com.coding.sirjavlux.effectUtils.EffectParticle;

public class FlashEffect implements Effect {

	private double damage, radius, height;
	private int duration, fireTicks, intensity;
	private List<EffectParticle> particles;
	private Location loc;
	private List<DamageCuboid> damageLocations;
	private List<LivingEntity> entities;
	private UUID uuid;
	private Vector projectileDir;
	
	public FlashEffect(Location loc, int duration, double damage, double radius, double height, int fireTicks, int intensity, Vector projectileDir) {
		this.damage = damage;
		this.radius = radius;
		this.height = height;
		this.fireTicks = fireTicks;
		this.duration = duration;
		this.loc = loc;
		this.intensity = intensity;
		this.projectileDir = projectileDir;
		particles = new ArrayList<>();
		damageLocations = new ArrayList<>();
		entities = new ArrayList<>();
		uuid = UUID.randomUUID();
		calculateCenterLocation();
	}
	
	public void calculateCenterLocation() {
		//fix loc
		boolean isSolid = true;
		Location temp = loc.clone();
		int count = 0;
		while (isSolid) {
			temp.subtract(projectileDir.clone().normalize().multiply(1d / 2d));
			if (temp.getBlock().isPassable()) {
				loc = temp;
				isSolid = false;
			} else if (count > 5) isSolid = false;
			count++;
		}
	}
	
	private Effect getEffect() {
		return this;
	}
	
	@Override
	public void playEffect() {
		Location center = loc.clone();
		for (int i = 0; i < 5; i++) center.getWorld().spawnParticle(Particle.FLASH, center.clone(), 0, 0, 0, 0, 0);
		Main.getInstance().getParticleSpawner().addEffect(getEffect());
	}

	@Override
	public void damageEntities() {
		for (LivingEntity entity : entities) {
			entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration, intensity, true));
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
