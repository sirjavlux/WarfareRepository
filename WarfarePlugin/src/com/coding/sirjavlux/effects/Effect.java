package com.coding.sirjavlux.effects;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.coding.sirjavlux.effectUtils.EffectParticle;

public interface Effect {
	public void playEffect();
	public void damageEntities();
	public double getDamage();
	public double getRadius();
	public double getHeight();
	public void setDamage(double damage);
	public void setRadius(double radius);
	public void setHeight(double height);
	public int getPlayerFireTicks();
	public void setPlayerFireTicks(int ticks);
	public List<LivingEntity> getEntities();
	public void setEntities(List<LivingEntity> entities);
	public UUID getUniqueId();
	public int getEffectDuration();
	public void setEffectDuration(int duration);
	public List<EffectParticle> getParticles();
	public void setParticles(List<EffectParticle> particles);
	public Location getLocation();
	public List<Location> getDamageLocations();
}
