package com.coding.sirjavlux.backpacks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import com.coding.sirjavlux.utils.Base64Utils;
import com.coding.sirjavlux.utils.InventoryHandler;

import net.minecraft.server.v1_15_R1.NBTTagCompound;

public class BackpackManager {

	protected static HashMap<String, Backpack> backpacks = new HashMap<>();
	
	/*/////////////////////////
	 * BACKPACK MANAGEMENT
	 */////////////////////////
	
	public static Backpack getStoredBackpack(String name) {
		return backpacks.get(name);
	}
	
	public static boolean isBackpack(String name) {
		return backpacks.containsKey(name);
	}
	
	public static boolean isBackpack(ItemStack item) {
		boolean backpack = false;
		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
		if (NMSItem.hasTag()) {
			NBTTagCompound tagComp = NMSItem.getTag();
			if (tagComp.hasKey("name")) {
				if (backpacks.containsKey(tagComp.getString("name"))) {
					backpack = true;
				}
			}
		}
		return backpack;
	}
	
	public static Backpack getBackpackFromItem(ItemStack item) {
		Backpack backpack = null;
		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
		if (NMSItem.hasTag()) {
			NBTTagCompound tagComp = NMSItem.getTag();
			if (tagComp.hasKey("name")) {
				String name = tagComp.getString("name");
				if (backpacks.containsKey(name)) {
					backpack = backpacks.get(name);
				}
			}
		}
		return backpack;
	}
	
	/*//////////////////////////
	 * GIVE ITEM AND UPDATE
	 *//////////////////////////
	
	public static void giveBackpack(Player p, Backpack backpack, int amount) {
		//give item
		while (amount > 0) {
			ItemStack backpackItem = generateBackpack(backpack);
			InventoryHandler.giveToPlayer(p, backpackItem, p.getLocation());
			amount--;
		}
	}
	
	public static ItemStack generateBackpack(Backpack backpack) {
		ItemStack item = new ItemStack(backpack.getMaterial());
		
		//add nms tags
		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tagComp = NMSItem.hasTag() ? NMSItem.getTag() : new NBTTagCompound();
		tagComp.setString("name", backpack.getName());
		tagComp.setString("uuid", UUID.randomUUID().toString());
		tagComp.setString("items", "");
		NMSItem.setTag(tagComp);
		item = CraftItemStack.asBukkitCopy(NMSItem);
		
		//update display data of item
		updateBackpackItem(item);
		return item;
	}
	
	public static void updateBackpackItem(ItemStack item) {
		Backpack backpack = getBackpackFromItem(item);
		if (backpack != null) {
			item.setType(backpack.getMaterial());
			
			ItemMeta meta = item.getItemMeta();
			
			//displayName
			String displayName = backpack.getDisplayName();
			displayName = ChatColor.translateAlternateColorCodes('&', displayName);
			meta.setDisplayName(displayName);
			
			//lore
			List<String> loreList = new ArrayList<>();
			List<String> lore = backpack.getLore();
			for (int i = 0; i < lore.size(); i++) {
				loreList.add(lore.get(i));
			}
			meta.setLore(loreList);
			
			//set custom texture
			meta.setCustomModelData(backpack.getModelData());
			meta.setUnbreakable(true);
			
			meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
			meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			
			item.setItemMeta(meta);
		}
	}
	
	public static String getSavedBase64Items(ItemStack item) {
		String base64 = "";		
		if (isBackpack(item)) {
			net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
			NBTTagCompound tagComp = NMSItem.getTag();
			base64 = tagComp.getString("items");
		}		
		return base64;
	}
	
	public static void loadBackPackItemsToPlayer(Player p, ItemStack item) {
		if (isBackpack(item)) {
			net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
			NBTTagCompound tagComp = NMSItem.getTag();
			UUID uuid = UUID.fromString(tagComp.getString("uuid"));
			if (!packs.containsKey(uuid)) {
				loadBackpackData(p, item);
			}
			BackpackInstance instance = packs.get(uuid);
			ItemStack[] items = instance.getItems();
			int size = instance.getBackpack().getPackSpace();
			PlayerInventory iv = p.getInventory();
			for (int i = 0; i < size; i++) {
				int slot = 8 + 18 - i;
				int instSlot = items.length - i - 1;
				ItemStack itemT = instSlot >= 0 ? items[instSlot] : new ItemStack(Material.AIR);
				iv.setItem(slot, itemT);
			}
		}
	}
	
	public static void unloadBackPackItemsFromPlayer(Player p) {		
		PlayerInventory iv = p.getInventory();
		for (int i = 0; i < 18; i++) {
			int slot = 9 + i;
			iv.setItem(slot, getSlotBlockItem());
		}
	}
	
	public static void loadBackpackData(Player p, ItemStack item) {
		if (isBackpack(item)) {
			String base64 = getSavedBase64Items(item);
			Backpack pack = getBackpackFromItem(item);
			net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
			NBTTagCompound tagComp = NMSItem.getTag();
			UUID uuid = UUID.fromString(tagComp.getString("uuid"));
			int size = pack.getPackSpace();
			List<ItemStack> oldItems = new ArrayList<>();
			ItemStack[] items = new ItemStack[size];
			if (!base64.isEmpty()) {
				String[] base64s = base64.split(",");
				for (String base : base64s) { try { oldItems.add(Base64Utils.fromBase64(base)); } catch (IOException e) { oldItems.add(new ItemStack(Material.AIR)); } }
			}	
			for (int i = 0; i < size; i++) {
				items[i] = oldItems.size() > i ? oldItems.get(i).clone() : new ItemStack(Material.AIR);
			}
			if (!packs.containsKey(uuid)) packs.put(uuid, new BackpackInstance(items, uuid, pack));
		}
	}
	
	public static ItemStack saveBackpackData(Player p, ItemStack item) {
		if (isBackpack(item)) {
			net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
			NBTTagCompound tagComp = NMSItem.getTag();
			UUID uuid = UUID.fromString(tagComp.getString("uuid"));
			if (packs.containsKey(uuid)) {
				BackpackInstance instance = packs.get(uuid);
				StringBuilder base64Builder = new StringBuilder();
				Backpack pack = getBackpackFromItem(item);
				int size = pack.getPackSpace();
				ItemStack[] items = instance.getItems();
				for (int i = 0; i < size; i++) {
					ItemStack bItem = i >= 0 ? items[i] : new ItemStack(Material.AIR);
					String base64 = "null";
					try { base64 = Base64Utils.toBase64(bItem); } catch (IOException e) { }
					base64Builder.append(base64Builder.length() > 0 ? "," + base64 : base64);
				}
				tagComp.setString("items", base64Builder.toString());
				NMSItem.setTag(tagComp);
				ItemStack newItem = CraftItemStack.asBukkitCopy(NMSItem);
				return newItem;	
			}
		}	
		return item;
	}
	
	public static ItemStack getSlotBlockItem() {
		ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Backpack slot");
		item.setItemMeta(meta);
		return item;
	}
	
	public static void updateItemInInstance(Player p, UUID uuid, ItemStack item, int slot) {
		if (!packs.containsKey(uuid)) {
			loadBackpackData(p, p.getInventory().getItemInOffHand());
		}
		BackpackInstance instance = packs.get(uuid);
		ItemStack[] items = instance.getItems();
		if (items.length > slot) {
			items[slot] = item.clone();
		}
		instance.setItems(items);
	}
	
	private static HashMap<UUID, BackpackInstance> packs = new HashMap<>();
	
	public static class BackpackInstance {
		
		private ItemStack[] items;
		private UUID uuid;
		private Backpack pack;
		
		public BackpackInstance(ItemStack[] items, UUID uuid, Backpack pack) {
			this.items = items;
			this.uuid = uuid;
			this.pack = pack;
		}
		
		public ItemStack[] getItems() { return items; }
		public UUID getUniqueId() { return uuid; }
		public Backpack getBackpack() { return pack; }
		
		public void setItems(ItemStack[] items) { this.items = items; }
	}
}
