package com.coding.sirjavlux.refillable;

import java.util.List;

import org.bukkit.Material;

import com.coding.sirjavlux.refillable.RefillableManager.RefillableType;

public class Refillable {

	private Material mat;
	private String name, displayName;
	private List<String> lore;
	private int filledAmount, modelData, useTime, fillTime;
	private String useSound, finishSound;
	private double walkSpeed, useAmount;
	private RefillableType type;
	
	public Refillable(String name, String displayName, Material mat, List<String> lore, int filledAmount, int modelData, String useSound, String finishSound, double walkSpeed, int useTime, int fillTime, RefillableType type, double useAmount) {
		this.displayName = displayName;
		this.name = name;
		this.mat = mat;
		this.lore = lore;
		this.filledAmount = filledAmount;
		this.modelData = modelData;
		this.useSound = useSound;
		this.finishSound = finishSound;
		this.walkSpeed = walkSpeed;
		this.useTime = useTime;
		this.fillTime = fillTime;
		this.type = type;
		this.useAmount = useAmount;
	}
	
	public Material getMaterial() { return mat; }
	public String getName() { return name; }
	public String getDisplayName() { return displayName; }
	public List<String> getLore() { return lore; }
	public int getFilledAmount() { return filledAmount; }
	public int getModelData() { return modelData; }
	public String getUseSound() { return useSound; }
	public String getFinishSound() { return finishSound; }
	public double getWalkSpeed() { return walkSpeed; }
	public int getUseTime() { return useTime; }
	public int getFillTime() { return fillTime; }
	public RefillableType getRefillableType() { return type; }
	public double getUseAmount() { return useAmount; }
}
