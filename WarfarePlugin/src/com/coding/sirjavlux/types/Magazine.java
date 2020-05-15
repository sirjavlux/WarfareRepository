package com.coding.sirjavlux.types;

import org.bukkit.Material;

public class Magazine {
	
	private int ammoCap;
	private String caliber;
	private String name;
	private Material mat;
	private String displayName;
	private String[] lore;
	private int customModel;
	private String addAmmoSound, removeAmmoSound;
	
	public Magazine(String caliber, int ammoCap, String name, Material mat, String displayName, String[] lore, int customModel, String addAmmoSound, String removeAmmoSound) {
		this.ammoCap = ammoCap;
		this.caliber = caliber;
		this.name = name;
		this.mat = mat;
		this.displayName = displayName;
		this.lore = lore;
		this.customModel = customModel;
		this.addAmmoSound = addAmmoSound;
		this.removeAmmoSound = removeAmmoSound;
	}
	
	public int getAmmoCapasity() { return ammoCap; }
	public String getCaliber() { return caliber; }
	public String getName() { return name; }
	public Material getMaterial() { return mat; }
	public String getDisplayName() { return displayName; }
	public String[] getLore() { return lore; }
	public int getCustomModelData() { return customModel; }
	public String getAddAmmoSound() { return addAmmoSound; }
	public String getRemoveAmmoSound() { return removeAmmoSound; }
}
