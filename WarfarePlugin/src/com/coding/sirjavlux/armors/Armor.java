package com.coding.sirjavlux.armors;

import java.util.List;

import org.bukkit.Color;
import org.bukkit.Material;

public class Armor {

	Material mat;
	String name, displayName, hitArmor, headData;
	List<String> lore;
	Color color;
	int durability, modelData;
	double protection;
	
	public Armor(String name, String displayName, Material mat, List<String> lore, String hitArmor, Color color, String headData, int durability, int modelData, double protection) {
		this.displayName = displayName;
		this.name = name;
		this.mat = mat;
		this.lore = lore;
		this.hitArmor = hitArmor;
		this.headData = headData;
		this.durability = durability;
		this.modelData = modelData;
		this.protection = protection;
	}
	
	public Material getMaterial() { return mat; }
	public String getName() { return name; }
	public String getDisplayName() { return displayName; }
	public List<String> getLore() { return lore; }
	public String getHitArmorSound() { return hitArmor; }
	public Color getColor() { return color; }
	public String getHeadData() { return headData; }
	public int getDurability() { return durability; }
	public int getModelData() { return modelData; }
	public double getProtection() { return protection; }
}
