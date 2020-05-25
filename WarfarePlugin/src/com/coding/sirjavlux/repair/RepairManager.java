package com.coding.sirjavlux.repair;

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

import com.coding.sirjavlux.utils.InventoryHandler;

import net.minecraft.server.v1_15_R1.NBTTagCompound;

public class RepairManager {

	protected static HashMap<String, Repair> repairs = new HashMap<>();
	
	/*/////////////////////////
	 * ARMOR MANAGEMENT
	 */////////////////////////
	
	public static Repair getStoredRepair(String name) {
		return repairs.get(name);
	}
	
	public static boolean isRepair(String name) {
		return repairs.containsKey(name);
	}
	
	public static boolean isRepair(ItemStack item) {
		boolean melee = false;
		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
		if (NMSItem.hasTag()) {
			NBTTagCompound tagComp = NMSItem.getTag();
			if (tagComp.hasKey("name")) {
				if (repairs.containsKey(tagComp.getString("name"))) {
					melee = true;
				}
			}
		}
		return melee;
	}
	
	public static Repair getRepairFromItem(ItemStack item) {
		Repair melee = null;
		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
		if (NMSItem.hasTag()) {
			NBTTagCompound tagComp = NMSItem.getTag();
			if (tagComp.hasKey("name")) {
				String name = tagComp.getString("name");
				if (repairs.containsKey(name)) {
					melee = repairs.get(name);
				}
			}
		}
		return melee;
	}
	
	public static Repair getRepair(String name) {
		return repairs.get(name);
	}
	
	/*//////////////////////////
	 * GIVE ITEM AND UPDATE
	 *//////////////////////////
	
	public static void giveRepair(Player p, Repair melee, int amount) {
		//give item
		while (amount > 0) {
			ItemStack meleeItem = generateRepair(melee);
			InventoryHandler.giveToPlayer(p, meleeItem, p.getLocation());
			amount--;
		}
	}
	
	public static ItemStack generateRepair(Repair melee) {
		ItemStack item = new ItemStack(melee.getMaterial());
		
		//add nms tags
		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tagComp = NMSItem.hasTag() ? NMSItem.getTag() : new NBTTagCompound();
		tagComp.setString("name", melee.getName());
		tagComp.setString("uuid", UUID.randomUUID().toString());
		tagComp.setInt("max-durability", melee.getDurability());
		tagComp.setInt("durability", melee.getDurability());
		NMSItem.setTag(tagComp);
		item = CraftItemStack.asBukkitCopy(NMSItem);
		
		//update display data of item
		updateRepairItem(item);
		return item;
	}
	
	//placeholders: %durability%
	public static void updateRepairItem(ItemStack item) {
		Repair melee = getRepairFromItem(item);
		if (melee != null) {
			item.setType(melee.getMaterial());
			
			ItemMeta meta = item.getItemMeta();
			
			//displayName
			String displayName = melee.getDisplayName();
			displayName = ChatColor.translateAlternateColorCodes('&', displayName);
			meta.setDisplayName(displayName);
			
			//lore
			List<String> loreList = new ArrayList<>();
			List<String> lore = melee.getLore();
			for (int i = 0; i < lore.size(); i++) {
				loreList.add(ChatColor.translateAlternateColorCodes('&', lore.get(i).replaceAll("%durability%", getDurabilityBar(item))));
			}
			meta.setLore(loreList);
			
			//set custom texture
			meta.setCustomModelData(melee.getModelData());
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
			ChatColor color = (double) i / (double) totalBarCount > (double) durability / (double) maxDurability ? ChatColor.GRAY : ChatColor.GREEN;
			builder.append(color + "" + ChatColor.BOLD + "|");
		}
		
		return builder.toString();
	}
	
	public static ItemStack setDurability(ItemStack input, int amount) {
		ItemStack item = input.clone();
		if (isRepair(item)) {
			net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
			NBTTagCompound tagComp = NMSItem.getTag();
			amount = amount < 0 ? 0 : amount;
			int maxDurability = tagComp.getInt("max-durability");
			int newDurability = amount > maxDurability ? maxDurability : amount;
			tagComp.setInt("durability", newDurability);
			NMSItem.setTag(tagComp);
			item = CraftItemStack.asBukkitCopy(NMSItem);
			updateRepairItem(item);
		}
		return item;
	}
	
	public static int getMaxDurability(ItemStack item) {
		int maxDurability = 0;
		if (isRepair(item)) {
			net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
			NBTTagCompound tagComp = NMSItem.getTag();
			maxDurability = tagComp.getInt("max-durability");
		}
		return maxDurability;
	}
	
	public static int getDurability(ItemStack item) {
		int durability = 0;
		if (isRepair(item)) {
			net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
			NBTTagCompound tagComp = NMSItem.getTag();
			durability = tagComp.getInt("durability");
		}
		return durability;
	}
}
