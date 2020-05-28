package com.coding.sirjavlux.backpacks;

import java.util.List;

import org.bukkit.Material;

public class Backpack {

	Material mat;
	String name, displayName;
	List<String> lore;
	int modelData, spaces;
	
	public Backpack(String name, String displayName, Material mat, List<String> lore, int modelData, int spaces) {
		this.displayName = displayName;
		this.name = name;
		this.mat = mat;
		this.lore = lore;
		this.modelData = modelData;
		this.spaces = spaces > 18 ? 18 : spaces;
	}
	
	public Material getMaterial() { return mat; }
	public String getName() { return name; }
	public String getDisplayName() { return displayName; }
	public List<String> getLore() { return lore; }
	public int getModelData() { return modelData; }
	public int getPackSpace() { return spaces; }
}
