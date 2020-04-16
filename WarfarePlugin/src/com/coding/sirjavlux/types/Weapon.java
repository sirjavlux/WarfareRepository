package com.coding.sirjavlux.types;

import org.bukkit.Material;

public class Weapon {
	
	private WeaponType type;
	private Material mat;
	private Magazine[] magReq;
	private String name;
	private double[] smokeOffset;
	private boolean smokeEnabled;
	private double smokeIntensity;
	private double damage;
	private String[] lore;
	private String displayName;
	private Magazine defaultMag;
	private boolean loadedByDefault; 
	private boolean reqMag;
	private int barrelAmmoCap;
	private String caliber;
	private double fireRate;
	private Ammo preloadAmmo;
	private int burstAmount;
	
	public Weapon (WeaponType type, Material mat, Magazine[] magReq, String name, double[] smokeOffset, boolean smokeEnabled, double smokeIntensity, double damage, String[] lore, String displayName, Magazine defaultMag, boolean loadedByDefault, boolean reqMag, int barrelAmmoCap, String caliber, double fireRate, Ammo preloadAmmo, int burstAmount) {
		this.type = type;
		this.mat = mat;
		this.magReq = magReq;
		this.name = name;
		this.smokeOffset = smokeOffset;
		this.smokeEnabled = smokeEnabled;
		this.smokeIntensity = smokeIntensity;
		this.damage = damage;
		this.lore = lore;
		this.displayName = displayName;
		this.defaultMag = defaultMag;
		this.loadedByDefault = loadedByDefault;
		this.reqMag = reqMag;
		this.barrelAmmoCap = barrelAmmoCap;
		this.caliber = caliber;
		this.fireRate = fireRate;
		this.preloadAmmo = preloadAmmo;
		this.burstAmount = burstAmount;
	}
	
	public WeaponType getType() { return type; }
	public Material getMat() { return mat; }
	public double getWeaponDamage() { return damage; }
	public Magazine[] getMagazineRequired() { return magReq; }
	public String getName() { return name; }
	public double[] getSmokeOffset() { return smokeOffset; }
	public boolean isSmokeEnabled() { return smokeEnabled; }
	public double getSmokeIntensity() { return smokeIntensity; }
	public String[] getLore() { return lore; }
	public String getDisplayName() { return displayName; }
	public Magazine getDefaultMagazine() { return defaultMag; }
	public boolean isLoadedByDefault() { return loadedByDefault; }
	public boolean requiresMagazine() { return reqMag; }
	public int getBarrelAmmoCap() { return barrelAmmoCap; }
	public String getCaliber() { return caliber; }
	public double getFireRate() { return fireRate; }
	public Ammo getPreLoadAmmo() { return preloadAmmo; }
	public int getBurstAmount() { return burstAmount; }
}
