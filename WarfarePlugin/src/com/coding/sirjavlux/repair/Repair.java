package com.coding.sirjavlux.repair;

import java.util.List;

import org.bukkit.Material;

public class Repair {

	Material mat;
	String name, displayName;
	List<String> lore;
	int durability, modelData, repairPerUse, useDelay;
	RepairType type;
	
	public Repair(String name, String displayName, Material mat, List<String> lore, int durability, int modelData, int repairPerUse, int useDelay, RepairType type) {
		this.displayName = displayName;
		this.name = name;
		this.mat = mat;
		this.lore = lore;
		this.durability = durability;
		this.modelData = modelData;
		this.repairPerUse = repairPerUse;
		this.useDelay = useDelay;
		this.type = type;
	}
	
	public Material getMaterial() { return mat; }
	public String getName() { return name; }
	public String getDisplayName() { return displayName; }
	public List<String> getLore() { return lore; }
	public int getDurability() { return durability; }
	public int getModelData() { return modelData; }
	public int getRepairPerUse() { return repairPerUse; }
	public int getUseDelay() { return useDelay; }
	public RepairType getRepairType() { return type; }
}
