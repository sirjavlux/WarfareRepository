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
	
	public Ammo(String name, String cal, Material mat, double damage, double armorPen, String[] lore, String displayName) {
		this.name = name;
		this.cal = cal;
		this.mat = mat;
		this.damage = damage;
		this.armorPen = armorPen;
		this.lore = lore;
		this.displayName = displayName;
	}
	
	public String getName() { return name; }
	public String getCaliber() { return cal; }
	public Material getMaterial() { return mat; }
	public double getDamage() { return damage; }
	public double getArmorPenetration() { return armorPen; }
	public String[] getLore() { return lore; }
	public String getDisplayName() { return displayName; }
}
