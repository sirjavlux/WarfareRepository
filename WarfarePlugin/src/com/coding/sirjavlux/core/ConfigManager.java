package com.coding.sirjavlux.core;

import java.util.Map;

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
	
	public static void loadConfig(FileConfiguration conf) {
		
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
	}
	
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