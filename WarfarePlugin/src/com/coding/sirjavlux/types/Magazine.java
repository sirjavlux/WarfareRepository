package com.coding.sirjavlux.types;

public class Magazine {
	
	private int ammoCap;
	private String caliber;
	private String name;
	
	public Magazine(String caliber, int ammoCap, String name) {
		this.ammoCap = ammoCap;
		this.caliber = caliber;
		this.name = name;
	}
	
	public int getAmmoCapasity() { return ammoCap; }
	public String getCaliber() { return caliber; }
	public String getName() { return name; }
}
