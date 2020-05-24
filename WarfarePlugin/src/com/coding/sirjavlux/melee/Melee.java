package com.coding.sirjavlux.melee;

import java.util.List;

import org.bukkit.Material;

public class Melee {

	Material mat;
	String name, displayName;
	List<String> lore;
	int durability, modelData, armorDamage;
	double damage;
	
	public Melee(String name, String displayName, Material mat, List<String> lore, int durability, int modelData, double damage, int armorDamage) {
		this.displayName = displayName;
		this.name = name;
		this.mat = mat;
		this.lore = lore;
		this.durability = durability;
		this.modelData = modelData;
		this.damage = damage;
		this.armorDamage = armorDamage;
	}
	
	public Material getMaterial() { return mat; }
	public String getName() { return name; }
	public String getDisplayName() { return displayName; }
	public List<String> getLore() { return lore; }
	public int getDurability() { return durability; }
	public int getModelData() { return modelData; }
	public double getDamage() { return damage; }
	public int getArmorDamage() { return armorDamage; }
}
