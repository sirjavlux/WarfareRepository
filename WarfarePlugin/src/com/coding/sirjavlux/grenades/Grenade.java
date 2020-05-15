package com.coding.sirjavlux.grenades;

import java.util.List;

import org.bukkit.Material;

public class Grenade {

	private GrenadeType type;
	private Material mat;
	private int modelData, fireTicks, maxStackSize, duration;
	private List<String> lore;
	private String displayName, name;
	private double explosionRange, explosionDamage, explosionDamageDrop, speed;
	
	public Grenade(Material mat, int modelData, List<String> lore, String displayName, GrenadeType type, String name, double explosionRange, double explosionDamage, double explosionDamageDrop, int fireTicks, double speed, int maxStackSize, int duration) {
		this.mat = mat;
		this.modelData = modelData;
		this.lore = lore;
		this.displayName = displayName;
		this.type = type;
		this.name = name;
		this.explosionRange = explosionRange;
		this.explosionDamage = explosionDamage;
		this.explosionDamageDrop = explosionDamageDrop;
		this.fireTicks = fireTicks;
		this.speed = speed;
		this.maxStackSize = maxStackSize;
		this.duration = duration;
	}
	
	public Material getMaterial() { return mat; }
	public int getModelData() { return modelData; }
	public List<String> getLore() { return lore; }
	public String getDisplayName() { return displayName; }
	public GrenadeType getType() { return type; }
	public String getName() { return name; }
	public double getExplosionRange() { return explosionRange; }
	public double getExplosionDamage() { return explosionDamage; }
	public double getExplosionDamageDrop() { return explosionDamageDrop; }
	public int getFireTicks() { return fireTicks; }
	public double getSpeed() { return speed; }
	public int getMaxStackSize() { return maxStackSize; }
	public int getDuration() { return duration; }
}