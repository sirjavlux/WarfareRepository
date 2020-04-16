package com.coding.sirjavlux.types;

import org.bukkit.Material;

public class Ammo {
	
	private String name;
	private String cal;
	private Material mat;
	private double damage;
	private double armorPen;
	private String[] lore;
	private String displayName;
	private double speed;
	private int maxStackSize;
	private Material shootMat;
	private AmmoType type;
	private int splitBulletAmount;
	private double explosionRange;
	private double explosionDamage;
	private double explosionDamageDrop;
	
	public Ammo(String name, String cal, Material mat, double damage, double armorPen, String[] lore, String displayName, double speed, int maxStackSize, Material shootMat, AmmoType type, int splitBulletAmount, double explosionRange, double explotionDamage, double explotionDamageDrop) {
		this.name = name;
		this.cal = cal;
		this.mat = mat;
		this.damage = damage;
		this.armorPen = armorPen;
		this.lore = lore;
		this.displayName = displayName;
		this.speed = speed;
		this.maxStackSize = maxStackSize;
		this.shootMat = shootMat;
		this.type = type;
		this.splitBulletAmount = splitBulletAmount;
		this.explosionRange = explosionRange;
		this.explosionDamage = explotionDamage;
		this.explosionDamageDrop = explotionDamageDrop;
	}
	
	public String getName() { return name; }
	public String getCaliber() { return cal; }
	public Material getMaterial() { return mat; }
	public double getDamage() { return damage; }
	public double getArmorPenetration() { return armorPen; }
	public String[] getLore() { return lore; }
	public String getDisplayName() { return displayName; }
	public double getSpeed() { return speed; }
	public int getMaxStackSize() { return maxStackSize; }
	public Material getShootMaterial() { return shootMat; }
	public AmmoType getAmmoType() { return type; }
	public int getSplitBulletAmount() { return splitBulletAmount; }
	public double getExplotionRange() { return explosionRange; }
	public double getExplotionDamage() { return explosionDamage; }
	public double getExplotionDrop() { return explosionDamageDrop; }
	
}
