package com.coding.sirjavlux.consumables;

import java.util.List;

import org.bukkit.Material;

public class Consumable {

	private Material mat;
	private int modelData, uses, useTime;
	private List<String> lore;
	private String displayName, name;
	private double heal, walkSpeed;
	private boolean splint, bandage, concussion;
	private String useSound, finishSound;
	
	public Consumable(Material mat, int modelData, List<String> lore, String displayName, String name, double heal, boolean splint, boolean bandage, boolean concussion, int uses, int useTime, double walkSpeed, String useSound, String finishSound) {
		this.mat = mat;
		this.modelData = modelData;
		this.lore = lore;
		this.displayName = displayName;
		this.name = name;
		this.heal = heal;
		this.splint = splint;
		this.bandage = bandage;
		this.concussion = concussion;
		this.uses = uses;
		this.useTime = useTime;
		this.walkSpeed = walkSpeed;
		this.useSound = useSound;
		this.finishSound = finishSound;
	}
	
	public Material getMaterial() { return mat; }
	public int getModelData() { return modelData; }
	public List<String> getLore() { return lore; }
	public String getDisplayName() { return displayName; }
	public String getName() { return name; }
	public double getHeal() { return heal; }
	public boolean getSplint() { return splint; }
	public boolean getBandage() { return bandage; }
	public boolean getConcussion() { return concussion; }
	public int getMaxUses() { return uses; }
	public int getUseTime() { return useTime; }
	public double getWalkSpeed() { return walkSpeed; }
	public String getUseSound() { return useSound; }
	public String getFinishSound() { return finishSound; }
}