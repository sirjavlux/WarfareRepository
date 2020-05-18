package com.coding.sirjavlux.armors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import com.coding.sirjavlux.utils.SkullUtils;
import com.coding.sirjavlux.utils.inventoryHandler;

import net.minecraft.server.v1_15_R1.NBTTagCompound;

public class ArmorManager {

	protected static HashMap<String, Armor> armors = new HashMap<>();
	
	/*/////////////////////////
	 * ARMOR MANAGEMENT
	 */////////////////////////
	
	public static Armor getStoredArmor(String name) {
		return armors.get(name);
	}
	
	public static boolean isArmor(String name) {
		return armors.containsKey(name);
	}
	
	public static boolean isArmor(ItemStack item) {
		boolean mag = false;
		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
		if (NMSItem.hasTag()) {
			NBTTagCompound tagComp = NMSItem.getTag();
			if (tagComp.hasKey("name")) {
				if (armors.containsKey(tagComp.getString("name"))) {
					mag = true;
				}
			}
		}
		return mag;
	}
	
	public static Armor getArmorFromItem(ItemStack item) {
		Armor armor = null;
		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
		if (NMSItem.hasTag()) {
			NBTTagCompound tagComp = NMSItem.getTag();
			if (tagComp.hasKey("name")) {
				String name = tagComp.getString("name");
				if (armors.containsKey(name)) {
					armor = armors.get(name);
				}
			}
		}
		return armor;
	}
	
	public static Armor getArmor(String name) {
		return armors.get(name);
	}
	
	/*//////////////////////////
	 * GIVE ITEM AND UPDATE
	 *//////////////////////////
	
	public static void giveArmor(Player p, Armor armor, int amount) {
		//give item
		while (amount > 0) {
			ItemStack armorItem = generateArmor(armor);
			inventoryHandler.giveToPlayer(p, armorItem, p.getLocation());
			amount--;
		}
	}
	
	public static ItemStack generateArmor(Armor armor) {
		ItemStack item = new ItemStack(armor.getMaterial());
		
		//add nms tags
		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tagComp = NMSItem.hasTag() ? NMSItem.getTag() : new NBTTagCompound();
		tagComp.setString("name", armor.getName());
		tagComp.setString("uuid", UUID.randomUUID().toString());
		tagComp.setInt("max-durability", armor.getDurability());
		tagComp.setInt("durability", armor.getDurability());
		NMSItem.setTag(tagComp);
		item = CraftItemStack.asBukkitCopy(NMSItem);
		
		//update display data of item
		updateArmorItem(item);
		return item;
	}
	
	//placeholders: %uses%
	public static void updateArmorItem(ItemStack item) {
		Armor armor = getArmorFromItem(item);
		if (armor != null) {
			item.setType(armor.getMaterial());
			
			//update head data if player head item
			item = SkullUtils.getCustomSkull(item, armor.getHeadData());
			
			//update color if leather armor
			ItemMeta meta = item.getItemMeta();
			if (meta instanceof LeatherArmorMeta && armor.getColor() != null) {
				LeatherArmorMeta letherMeta = (LeatherArmorMeta) meta;
				letherMeta.setColor(armor.getColor());
				meta = letherMeta;
			}
			
			//displayName
			String displayName = armor.getDisplayName();
			displayName = ChatColor.translateAlternateColorCodes('&', displayName);
			meta.setDisplayName(displayName);
			
			//lore
			List<String> loreList = new ArrayList<>();
			List<String> lore = armor.getLore();
			for (int i = 0; i < lore.size(); i++) {
				loreList.add(ChatColor.translateAlternateColorCodes('&', lore.get(i).replaceAll("%durability%", getDurabilityBar(item))));
			}
			meta.setLore(loreList);
			
			//set custom texture
			meta.setCustomModelData(armor.getModelData());
			meta.setUnbreakable(true);
			
			meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
			meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			
			item.setItemMeta(meta);
		}
	}
	
	private static String getDurabilityBar(ItemStack item) {
		StringBuilder builder = new StringBuilder();
		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tagComp = NMSItem.getTag();
		int durability = tagComp.getInt("durability");
		int maxDurability = tagComp.getInt("max-durability");
		int totalBarCount = 15;
		for (int i = 0; i < totalBarCount; i++) {
			ChatColor color = i > durability / maxDurability ? ChatColor.GREEN : ChatColor.GRAY;
			builder.append(color + "" + ChatColor.BOLD + ":");
		}
		
		return builder.toString();
	}
	
	public static ItemStack setDurability(ItemStack item, int amount) {
		if (isArmor(item)) {
			net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
			NBTTagCompound tagComp = NMSItem.getTag();
			int durability = tagComp.getInt("durability");
			int maxDurability = tagComp.getInt("max-durability");
			int newDurability = durability + amount > maxDurability ? maxDurability : durability + amount;
			tagComp.setInt("durability", newDurability);
			NMSItem.setTag(tagComp);
			item = CraftItemStack.asBukkitCopy(NMSItem);
			updateArmorItem(item);
		}
		return item;
	}
	
	public static int getMaxDurability(ItemStack item) {
		int maxDurability = 0;
		if (isArmor(item)) {
			net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
			NBTTagCompound tagComp = NMSItem.getTag();
			maxDurability = tagComp.getInt("max-durability");
		}
		return maxDurability;
	}
	
	public static int getDurability(ItemStack item) {
		int durability = 0;
		if (isArmor(item)) {
			net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
			NBTTagCompound tagComp = NMSItem.getTag();
			durability = tagComp.getInt("durability");
		}
		return durability;
	}
}
