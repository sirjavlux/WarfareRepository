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
	
	public Ammo(String name, String cal, Material mat, double damage, double armorPen, String[] lore, String displayName, double speed, int maxStackSize, Material shootMat) {
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
}
