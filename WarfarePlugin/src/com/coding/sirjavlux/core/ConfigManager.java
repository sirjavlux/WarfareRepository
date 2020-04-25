package com.coding.sirjavlux.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class ConfigManager {
	
	private static double leatherHelmetProt;
	private static double goldenHelmetProt;
	private static double chainHelmetProt;
	private static double ironHelmetProt;
	private static double diamondHelmetProt;
	
	private static double leatherChestplateProt;
	private static double goldenChestplateProt;
	private static double chainChestplateProt;
	private static double ironChestplateProt;
	private static double diamondChestplateProt;
	
	private static double leatherLeggingsProt;
	private static double goldenLeggingsProt;
	private static double chainLeggingsProt;
	private static double ironLeggingsProt;
	private static double diamondLeggingsProt;
	
	private static double dynamicProtectionEnchantment;
	private static double staticProtectionEnchantment;
	private static double durabilityProtDecreaseRate;
	
	private static List<Material> ignoredBlocks = new ArrayList<>();
	private static boolean ignoreOpenGate;
	
	private static double speedRedEntity;
	private static double speedRedObsticle;
	private static double penRedArmor;
	private static double penRedObsticle;
	private static double penRedEntity;
	private static double damageRedSpeed;
	private static double damageRedPen;
	
	private static boolean damageBreakLegEnabled;
	private static boolean damageConcussionEnabled;
	private static boolean damageBleedingEnabled;
	private static double damageBreakLegHeight;
	private static double damageBreakLegChanceDamage;
	private static double damageConcussionChanceDamage;
	private static double damageConcussionChance;
	private static double damageBleedingChanceDamage;
	private static double damageBleedingPerDamage;
	private static int damageBrokenLegTime;
	private static int damageConcussionTime;
	private static int damageBleedingTime;
	private static int damageTimeBetweanBleeding;
	private static boolean damageOnlyPlayerBleeding;
	private static int damageConcussionStrenght;
	private static int damageBrokenLegStrenght;
	
	private static float recoilYawModifier;
	private static boolean recoilEnabled;
	private static boolean knockbackEnabled;
	
	public static void loadConfig(FileConfiguration conf) {
		//armor protection data
		leatherHelmetProt = conf.contains("armor.protection.helmet.leather") ? conf.getDouble("armor.protection.helmet.leather") : 0.15;
		goldenHelmetProt = conf.contains("armor.protection.helmet.gold") ? conf.getDouble("armor.protection.helmet.gold") : 0.17;
		chainHelmetProt = conf.contains("armor.protection.helmet.chain") ? conf.getDouble("armor.protection.helmet.chain") : 0.2;
		ironHelmetProt = conf.contains("armor.protection.helmet.iron") ? conf.getDouble("armor.protection.helmet.iron") : 0.34;
		diamondHelmetProt = conf.contains("armor.protection.helmet.diamond") ? conf.getDouble("armor.protection.helmet.diamond") : 0.64;
		
		leatherChestplateProt = conf.contains("armor.protection.chestplate.leather") ? conf.getDouble("armor.protection.chestplate.leather") : 0.17;
		goldenChestplateProt = conf.contains("armor.protection.chestplate.gold") ? conf.getDouble("armor.protection.chestplate.gold") : 0.2;
		chainChestplateProt = conf.contains("armor.protection.chestplate.chain") ? conf.getDouble("armor.protection.chestplate.chain") : 0.32;
		ironChestplateProt = conf.contains("armor.protection.chestplate.iron") ? conf.getDouble("armor.protection.chestplate.iron") : 0.41;
		diamondChestplateProt = conf.contains("armor.protection.chestplate.diamond") ? conf.getDouble("armor.protection.chestplate.diamond") : 0.73;
		
		leatherLeggingsProt = conf.contains("armor.protection.leggings.leather") ? conf.getDouble("armor.protection.leggings.leather") : 0.16;
		goldenLeggingsProt = conf.contains("armor.protection.leggings.gold") ? conf.getDouble("armor.protection.leggings.gold") : 0.19;
		chainLeggingsProt = conf.contains("armor.protection.leggings.chain") ? conf.getDouble("armor.protection.leggings.chain") : 0.28;
		ironLeggingsProt = conf.contains("armor.protection.leggings.iron") ? conf.getDouble("armor.protection.leggings.iron") : 0.37;
		diamondLeggingsProt = conf.contains("armor.protection.leggings.diamond") ? conf.getDouble("armor.protection.leggings.diamond") : 0.68;
		
		dynamicProtectionEnchantment = conf.contains("armor.protection.dynamic-protection-enchantment") ? conf.getDouble("armor.protection.dynamic-protection-enchantment") : 0.04;
		staticProtectionEnchantment = conf.contains("armor.protection.static-protection-enchantment") ? conf.getDouble("armor.protection.static-protection-enchantment") : 0.06;
		durabilityProtDecreaseRate = conf.contains("armor.protection.durability-protection-decrease-rate") ? conf.getDouble("armor.protection.durability-protection-decrease-rate") : 0.6;
	
		//ignored blocks by projectile data
		List<String> blocks = conf.contains("projectile.ignored-blocks") ? conf.getStringList("projectile.ignored-blocks") : new ArrayList<>();
		for (String str : blocks) {
			try{
				ignoredBlocks.add(Material.getMaterial(str.toUpperCase()));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		ignoreOpenGate = conf.contains("projectile.ignore-open.fence-gate") ? conf.getBoolean("projectile.ignore-open.fence-gate") : true;
		
		//collateral speed/penetration affecting values
		speedRedEntity = conf.contains("projectile.speed-reduction.entity") ? conf.getDouble("projectile.speed-reduction.entity") : 0.4;
		speedRedObsticle = conf.contains("projectile.speed-reduction.obsticle") ? conf.getDouble("projectile.speed-reduction.obsticle") : 0.1;
		penRedArmor = conf.contains("projectile.penetration-reduction.armor") ? conf.getDouble("projectile.penetration-reduction.armor") : 0.6;
		penRedObsticle = conf.contains("projectile.penetration-reduction.obsticle") ? conf.getDouble("projectile.penetration-reduction.obsticle") : 0.1;
		penRedEntity = conf.contains("projectile.penetration-reduction.entity") ? conf.getDouble("projectile.penetration-reduction.entity") : 0.2;
		damageRedSpeed = conf.contains("projectile.damage-reduction.speed") ? conf.getDouble("projectile.damage-reduction.speed") : 0.4;
		damageRedPen = conf.contains("projectile.damage-reduction.penetration") ? conf.getDouble("projectile.damage-reduction.penetration") : 0.2;
		
		//damage settings
		damageBreakLegEnabled = conf.contains("damage.break-leg.enabled") ? conf.getBoolean("damage.break-leg.enabled") : true;
		damageConcussionEnabled = conf.contains("damage.concussion.enabled") ? conf.getBoolean("damage.concussion.enabled") : true;
		damageBleedingEnabled = conf.contains("damage.bleeding.enabled") ? conf.getBoolean("damage.bleeding.enabled") : true;
		damageBreakLegHeight = conf.contains("damage.break-leg.height") ? conf.getDouble("damage.break-leg.height") : 4;
		damageBreakLegChanceDamage = conf.contains("damage.break-leg.chance-per-legdamage") ? conf.getDouble("damage.break-leg.chance-per-legdamage") : 0.06;
		damageConcussionChanceDamage = conf.contains("damage.concussion.chance-per-headdamage") ? conf.getDouble("damage.concussion.chance-per-headdamage") : 0.08;
		damageConcussionChance = conf.contains("damage.concussion.chance-per-head") ? conf.getDouble("damage.concussion.chance-per-head") : 0.1;
		damageBleedingChanceDamage = conf.contains("damage.bleeding.chance-per-damage") ? conf.getDouble("damage.bleeding.chance-per-damage") : 0.04;
		damageBleedingPerDamage = conf.contains("damage.bleeding.bleeding-per-damage") ? conf.getDouble("damage.bleeding.bleeding-per-damage") : 0.1;
		damageBrokenLegTime = conf.contains("damage.break-leg.time-active-ticks") ? conf.getInt("damage.break-leg.time-active-ticks") : 24000;
		damageConcussionTime = conf.contains("damage.concussion.time-active-ticks") ? conf.getInt("damage.concussion.time-active-ticks") : 12000;
		damageBleedingTime = conf.contains("damage.bleeding.time-active-ticks") ? conf.getInt("damage.bleeding.time-active-ticks") : 6000;
		damageTimeBetweanBleeding = conf.contains("damage.bleeding.time-betwean-bleeding") ? conf.getInt("damage.bleeding.time-betwean-bleeding") : 20;
		damageOnlyPlayerBleeding = conf.contains("damage.bleeding.only-players") ? conf.getBoolean("damage.bleeding.only-players") : false;
		damageConcussionStrenght = conf.contains("damage.concussion.strenght") ? conf.getInt("damage.concussion.strenght") : 0;
		damageBrokenLegStrenght = conf.contains("damage.break-leg.strenght") ? conf.getInt("damage.break-leg.strenght") : 1;
		
		//shooting settings
		recoilYawModifier = (float) (conf.contains("shooting.recoil.yaw-modifier") ? conf.getDouble("shooting.recoil.yaw-modifier") : 0.2);
		recoilEnabled = conf.contains("shooting.recoil.enabled") ? conf.getBoolean("shooting.recoil.enabled") : true;
		knockbackEnabled = conf.contains("shooting.knockback.enabled") ? conf.getBoolean("shooting.knockback.enabled") : true;
	}
	
	public static float getRecoilYawModifier() { return recoilYawModifier; }
	public static boolean recoilEnabled() { return recoilEnabled; }
	public static boolean knockbackEnabled() { return knockbackEnabled; }
	
	public static boolean breakLegEnabled() { return damageBreakLegEnabled; }
	public static boolean concussionEnabled() { return damageConcussionEnabled; }
	public static boolean bleedingEnabled() { return damageBleedingEnabled; }
	public static double getBreakLegHeight() { return damageBreakLegHeight; }
	public static double getBreakLegDamageChance() { return damageBreakLegChanceDamage; }
	public static double getConcussionDamageChance() { return damageConcussionChanceDamage; }
	public static double getConcussionChance() { return damageConcussionChance; }
	public static double getBleadingDamageChance() { return damageBleedingChanceDamage; }
	public static double getBleedingPerDamage() { return damageBleedingPerDamage; }
	public static int getBrokenLegTime() { return damageBrokenLegTime; }
	public static int getConcussionTime() { return damageConcussionTime; }
	public static int getBleedingTime() { return damageBleedingTime; }
	public static int getTimeBetweanBleeding() { return damageTimeBetweanBleeding; }
	public static boolean getOnlyPlayerBleeding() { return damageOnlyPlayerBleeding; }
	public static int getConcussionStrenght() { return damageConcussionStrenght; }
	public static int getBrokenLegStrenght() { return damageBrokenLegStrenght; }
	
	public static List<Material> getIgnoredBlocks() { return ignoredBlocks; }
	public static boolean ignoreOpenGates() { return ignoreOpenGate; }
	
	public static double getSpeedReductionEntity() { return speedRedEntity; }
	public static double getSpeedReductionObsticle() { return speedRedObsticle; }
	public static double getPenetrationReductionArmor() { return penRedArmor; }
	public static double getPenetrationReductionObsticle() { return penRedObsticle; }
	public static double getPenetrationReductionEntity() { return penRedEntity; }
	public static double getDamageReductionSpeed() { return damageRedSpeed; }
	public static double getDamageReductionPenetration() { return damageRedPen; }
	
	public static double getItemArmorProtection(ItemStack item) {
		double armorProt = 0;
		//get default armor protection
		String itemType = item.getType().name().split("_")[0];
		if (item.getType().name().contains("HELM")) {
			switch (itemType) {
			case "LEATHER": armorProt = leatherHelmetProt;
				break;
			case "GOLDEN": armorProt = goldenHelmetProt;
				break;
			case "CHAINMAIL": armorProt = chainHelmetProt;
				break;
			case "IRON": armorProt = ironHelmetProt;
				break;
			case "DIAMOND": armorProt = diamondHelmetProt;
				break;
			}
		} else if (item.getType().name().contains("LEG")) {
			switch (itemType) {
			case "LEATHER": armorProt = leatherLeggingsProt;
				break;
			case "GOLDEN": armorProt = goldenLeggingsProt;
				break;
			case "CHAINMAIL": armorProt = chainLeggingsProt;
				break;
			case "IRON": armorProt = ironLeggingsProt;
				break;
			case "DIAMOND": armorProt = diamondLeggingsProt;
				break;
			}
		} else if (item.getType().name().contains("CHEST")) {
			switch (itemType) {
			case "LEATHER": armorProt = leatherChestplateProt;
				break;
			case "GOLDEN": armorProt = goldenChestplateProt;
				break;
			case "CHAINMAIL": armorProt = chainChestplateProt;
				break;
			case "IRON": armorProt = ironChestplateProt;
				break;
			case "DIAMOND": armorProt = diamondChestplateProt;
				break;
			}
		}
		//calculate with enchantment
		Map<Enchantment, Integer> enchants = item.getEnchantments();
		int enchantmentAmount = enchants.containsKey(Enchantment.PROTECTION_ENVIRONMENTAL) ? enchants.get(Enchantment.PROTECTION_ENVIRONMENTAL) : 0;
		armorProt = armorProt + armorProt * (dynamicProtectionEnchantment * enchantmentAmount) + staticProtectionEnchantment * enchantmentAmount;
		//calculate armor durabilitmy protection decrease
		ItemMeta meta = item.getItemMeta();
		if (meta instanceof Damageable) {
			Damageable dMeta = (Damageable) meta;
			double maxDurability = item.getType().getMaxDurability();
			double durability = maxDurability - dMeta.getDamage();
			armorProt -= ((armorProt * 100) * ((1 - durability / maxDurability) * durabilityProtDecreaseRate)) / 100;
			armorProt = armorProt < 0 ? 0 : armorProt;
		}
		
		return armorProt;
	}
}