package com.coding.sirjavlux.grenades;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.coding.sirjavlux.utils.inventoryHandler;

import net.minecraft.server.v1_15_R1.NBTTagCompound;

public class GrenadeManager {

	protected static HashMap<String, Grenade> grenades = new HashMap<>();
	
	public static boolean isGrenade(String name) {
		return grenades.containsKey(name);
	}
	
	public static boolean isGrenade(ItemStack item) {
		boolean grenade = false;
		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
		if (NMSItem.hasTag()) {
			NBTTagCompound tagComp = NMSItem.getTag();
			if (tagComp.hasKey("name")) {
				if (grenades.containsKey(tagComp.getString("name"))) {
					grenade = true;
				}
			}
		}
		return grenade;
	}
	
	public static Grenade getStoredGrenade(String name) {
		return grenades.get(name);
	}
	
	public static Grenade getGrenadeFromItem(ItemStack item) {
		Grenade grenade = null;
		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
		if (NMSItem.hasTag()) {
			NBTTagCompound tagComp = NMSItem.getTag();
			if (tagComp.hasKey("name")) {
				String name = tagComp.getString("name");
				if (grenades.containsKey(name)) {
					grenade = grenades.get(name);
				}
			}
		}
		return grenade;
	}
	
	/*//////////////////////////
	 * GIVE ITEM AND UPDATE
	 *//////////////////////////
	
	public static void giveGrenade(Player p, Grenade grenade, int amount) {
		int maxStack = grenade.getMaxStackSize();
		//give item
		while (amount > 0) {
			ItemStack grenadeItem = generateGrenade(grenade, 1);
			int amountToAdd = amount > maxStack ? maxStack : amount;
			amount -= amountToAdd;
			grenadeItem.setAmount(amountToAdd);
			inventoryHandler.giveToPlayer(p, grenadeItem, p.getLocation());
		}
	}
	
	public static ItemStack generateGrenade(Grenade grenade, int amount) {
		ItemStack item = new ItemStack(grenade.getMaterial());
		
		//add nms tags
		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tagComp = NMSItem.hasTag() ? NMSItem.getTag() : new NBTTagCompound();
		tagComp.setString("name", grenade.getName());
		tagComp.setString("uuid", UUID.randomUUID().toString());
		NMSItem.setTag(tagComp);
		item = CraftItemStack.asBukkitCopy(NMSItem);
		
		//update display data of item
		updateGrenadeItem(item);
		item.setAmount(amount > item.getMaxStackSize() ? item.getMaxStackSize() : amount);
		return item;
	}
	
	public static void updateGrenadeItem(ItemStack item) {
		Grenade grenade = getGrenadeFromItem(item);
		if (grenade != null) {
			ItemMeta meta = item.getItemMeta();
			
			//displayName
			String displayName = grenade.getDisplayName();
			displayName = ChatColor.translateAlternateColorCodes('&', displayName);
			meta.setDisplayName(displayName);
			
			//lore
			List<String> loreList = new ArrayList<>();
			List<String> lore = grenade.getLore();
			for (int i = 0; i < lore.size(); i++) {
				loreList.add(ChatColor.translateAlternateColorCodes('&', lore.get(i)));
			}
			meta.setLore(loreList);
			
			//set custom texture
			meta.setCustomModelData(grenade.getModelData());
			meta.setUnbreakable(true);
			
			item.setItemMeta(meta);
			item.setType(grenade.getMaterial());
		}
	}
}
