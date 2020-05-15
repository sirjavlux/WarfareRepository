package com.coding.sirjavlux.types;

import org.bukkit.Color;
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
	private AmmoType type;
	private int splitBulletAmount;
	private double explosionRange;
	private double explosionDamage;
	private double explosionDamageDrop;
	private double knockBack;
	private double armorDamage;
	private double recoil;
	private double knockback;
	private int customModel; 
	private int explosionFireTicks;
	private float spread;
	private int hitBurnTicks;
	private Color trail;
	private String hitGround, hitFlesh, hitArmor, explodeSound, trailSound;
	
	public Ammo(String name, String cal, Material mat, double damage, double armorPen, String[] lore, String displayName, double speed, int maxStackSize, Material shootMat, AmmoType type, int splitBulletAmount, double explosionRange, double explotionDamage, double explotionDamageDrop, double knockBack, double armorDamage, double recoil, double knockback, int customModel, int explosionFireTicks, float spread, int hitBurnTicks, Color trail, String hitGround, String hitFlesh, String hitArmor, String explodeSound, String trailSound) {
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
		this.type = type;
		this.splitBulletAmount = splitBulletAmount;
		this.explosionRange = explosionRange;
		this.explosionDamage = explotionDamage;
		this.explosionDamageDrop = explotionDamageDrop;
		this.knockBack = knockBack;
		this.armorDamage = armorDamage;
		this.recoil = recoil;
		this.knockback = knockback;
		this.customModel = customModel;
		this.explosionFireTicks = explosionFireTicks;
		this.spread = spread;
		this.hitBurnTicks = hitBurnTicks;
		this.trail = trail;
		this.hitGround = hitGround;
		this.hitFlesh = hitFlesh;
		this.hitArmor = hitArmor;
		this.explodeSound = explodeSound;
		this.trailSound = trailSound;
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
	public AmmoType getAmmoType() { return type; }
	public int getSplitBulletAmount() { return splitBulletAmount; }
	public double getExplotionRange() { return explosionRange; }
	public double getExplotionDamage() { return explosionDamage; }
	public double getExplotionDrop() { return explosionDamageDrop; }
	public double getKnockBack() { return knockBack; }
	public double getArmorDamage() { return armorDamage; }	
	public double getRecoil() { return recoil; }
	public double getKnockback() { return knockback; }
	public int getCustomModelData() { return customModel; }
	public int getExplosionFireTicks() { return explosionFireTicks; }
	public float getBulletSpread() { return spread; }
	public int getHitBurnTicks() { return hitBurnTicks; }
	public Color getTrail() { return trail; }
	public String getHitGroundSound() { return hitGround; }
	public String getHitFleshSound() { return hitFlesh; }
	public String getHitArmorSound() { return hitArmor; }
	public String getExplodeSound() { return explodeSound; }
	public String getTrailSound() { return trailSound; }
}
