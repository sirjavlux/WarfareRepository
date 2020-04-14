package com.coding.sirjavlux.types;

import org.bukkit.Material;

public class Ammo {
	
	private String cal;
	private Material mat;
	
	public Ammo(String cal, Material mat) {
		this.cal = cal;
		this.mat = mat;
	}
	
	public String getCaliber() { return cal; }
	public Material getMaterial() { return mat; }
}
