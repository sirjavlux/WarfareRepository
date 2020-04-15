package com.coding.sirjavlux.types;

import org.bukkit.Material;

public class Magazine {
	
	private int ammoCap;
	private String caliber;
	private String name;
	private Material mat;
	private String displayName;
	private String[] lore;
	
	public Magazine(String caliber, int ammoCap, String name, Material mat, String displayName, String[] lore) {
		this.ammoCap = ammoCap;
		this.caliber = caliber;
		this.name = name;
		this.mat = mat;
	}
	
	public int getAmmoCapasity() { return ammoCap; }
	public String getCaliber() { return caliber; }
	public String getName() { return name; }
	public Material getMaterial() { return mat; }
	public String getDisplayName() { return displayName; }
	public String[] getLore() { return lore; }
}
