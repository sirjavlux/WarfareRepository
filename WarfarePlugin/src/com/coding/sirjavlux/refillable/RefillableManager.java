package com.coding.sirjavlux.refillable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.coding.sirjavlux.consumables.WaterBarManager;
import com.coding.sirjavlux.core.Main;
import com.coding.sirjavlux.utils.InventoryHandler;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_15_R1.NBTTagCompound;

public class RefillableManager {

	protected static HashMap<String, Refillable> refillables = new HashMap<>();
	
	/*/////////////////////////
	 * REFILLABLE MANAGEMENT
	 */////////////////////////
	
	public static Refillable getStoredRefillable(String name) {
		return refillables.get(name);
	}
	
	public static boolean isRefillable(String name) {
		return refillables.containsKey(name);
	}
	
	public static boolean isRefillable(ItemStack item) {
		boolean refillable = false;
		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
		if (NMSItem.hasTag()) {
			NBTTagCompound tagComp = NMSItem.getTag();
			if (tagComp.hasKey("name")) {
				if (refillables.containsKey(tagComp.getString("name"))) {
					refillable = true;
				}
			}
		}
		return refillable;
	}
	
	public static Refillable getRefillableFromItem(ItemStack item) {
		Refillable melee = null;
		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
		if (NMSItem.hasTag()) {
			NBTTagCompound tagComp = NMSItem.getTag();
			if (tagComp.hasKey("name")) {
				String name = tagComp.getString("name");
				if (refillables.containsKey(name)) {
					melee = refillables.get(name);
				}
			}
		}
		return melee;
	}
	
	public static Refillable getRefillable(String name) {
		return refillables.get(name);
	}
	
	/*//////////////////////////
	 * GIVE ITEM AND UPDATE
	 *//////////////////////////
	
	public static void giveRefillable(Player p, Refillable refillable, int amount) {
		//give item
		while (amount > 0) {
			ItemStack meleeItem = generateRefillable(refillable);
			InventoryHandler.giveToPlayer(p, meleeItem, p.getLocation());
			amount--;
		}
	}
	
	public static ItemStack generateRefillable(Refillable refillable) {
		ItemStack item = new ItemStack(refillable.getMaterial());
		
		//add nms tags
		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tagComp = NMSItem.hasTag() ? NMSItem.getTag() : new NBTTagCompound();
		tagComp.setString("name", refillable.getName());
		tagComp.setString("uuid", UUID.randomUUID().toString());
		tagComp.setDouble("max-fill", refillable.getFilledAmount());
		tagComp.setDouble("fill", refillable.getFilledAmount());
		NMSItem.setTag(tagComp);
		item = CraftItemStack.asBukkitCopy(NMSItem);
		
		//update display data of item
		updateRefillableItem(item);
		return item;
	}
	
	//placeholders: %durability%
	public static void updateRefillableItem(ItemStack item) {
		Refillable refillable = getRefillableFromItem(item);
		if (refillable != null) {
			item.setType(refillable.getMaterial());
			
			ItemMeta meta = item.getItemMeta();
			
			//displayName
			String displayName = refillable.getDisplayName();
			displayName = ChatColor.translateAlternateColorCodes('&', displayName);
			meta.setDisplayName(displayName);
			
			//lore
			List<String> loreList = new ArrayList<>();
			List<String> lore = refillable.getLore();
			for (int i = 0; i < lore.size(); i++) {
				loreList.add(ChatColor.translateAlternateColorCodes('&', lore.get(i).replaceAll("%fill%", getFillBar(item))));
			}
			meta.setLore(loreList);
			
			//set custom texture
			meta.setCustomModelData(refillable.getModelData());
			meta.setUnbreakable(true);
			
			meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
			meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			
			item.setItemMeta(meta);
		}
	}
	
	private static String getFillBar(ItemStack item) {
		StringBuilder builder = new StringBuilder();
		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tagComp = NMSItem.getTag();
		double fill = tagComp.getDouble("fill");
		double maxFill = tagComp.getDouble("max-fill");
		int totalBarCount = 15;
		for (int i = 0; i < totalBarCount; i++) {
			ChatColor color = (double) i / (double) totalBarCount < (double) fill / (double) maxFill ? ChatColor.GREEN : ChatColor.GRAY;
			builder.append(color + "" + ChatColor.BOLD + "|");
		}
		
		return builder.toString();
	}
	
	public static ItemStack setFill(ItemStack input, double amount) {
		ItemStack item = input.clone();
		if (isRefillable(item)) {
			net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
			NBTTagCompound tagComp = NMSItem.getTag();
			amount = amount < 0 ? 0 : amount;
			double maxFill = tagComp.getInt("max-fill");
			double newFill = amount > maxFill ? maxFill : amount;
			tagComp.setDouble("fill", newFill);
			NMSItem.setTag(tagComp);
			item = CraftItemStack.asBukkitCopy(NMSItem);
			updateRefillableItem(item);
		}
		return item;
	}
	
	public static double getMaxFill(ItemStack item) {
		double maxFill = 0;
		if (isRefillable(item)) {
			net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
			NBTTagCompound tagComp = NMSItem.getTag();
			maxFill = tagComp.getDouble("max-fill");
		}
		return maxFill;
	}
	
	public static double getFill(ItemStack item) {
		double fill = 0;
		if (isRefillable(item)) {
			net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
			NBTTagCompound tagComp = NMSItem.getTag();
			fill = tagComp.getDouble("fill");
		}
		return fill;
	}
	
	/*///////////////////////////
	 * CONSUMABLE RUNNABLE
	 *///////////////////////////
	
	public static HashMap<UUID, RefillableInstance> refillablesInUse = new HashMap<>();
	
	public static void startRefillableRunnable() {
		final int runnableSpeed = 2;
		new BukkitRunnable() {
			@Override
			public void run() {
				List<UUID> removableUUIDs = new ArrayList<>();
				//run trough consumables and use/remove time
				for (Entry<UUID, RefillableInstance> entry : refillablesInUse.entrySet()) {
					UUID uuid = entry.getKey();
					RefillableInstance ref = entry.getValue();
					Refillable refillable = ref.refillable;
					//check if instance is invalid
					if (!Bukkit.getOfflinePlayer(uuid).isOnline()) {
						removableUUIDs.add(uuid);
						continue;
					}
					Player p = Bukkit.getPlayer(uuid);
					boolean con = false;
					if (!isRefillable(p.getInventory().getItemInMainHand())) {
						removableUUIDs.add(uuid);
						con = true;
					}
					if (p.getInventory().getHeldItemSlot() != ref.slot) {
						removableUUIDs.add(uuid);
						con = true;
					}
					if (p.getInventory().getItemInMainHand() == ref.item) {
						removableUUIDs.add(uuid);
						con = true;
					}
					if (con) {
						//fill if fill type
						if (ref.type == RefillableUseType.Fill) {
							ItemStack item = p.getInventory().getItem(ref.slot);
							if (isRefillable(item)) {
								Refillable ref1 = getRefillableFromItem(item);
								Refillable ref2 = getRefillableFromItem(ref.item);
								if (ref1 == ref2 && ref.totalFill > getFill(ref.item)) {
									ItemStack finalItem = setFill(ref.item, ref.totalFill);
									p.getInventory().setItem(ref.slot, finalItem);
								}
							}
						}
						continue;
					}
					//remove time
					if (ref.timeLeft > 0) {
						if (ref.timeLeft % 10 == 0 && ref.timeLeft > 20) p.playSound(p.getLocation(), refillable.getUseSound(), 1, 1);
						p.setWalkSpeed((float) refillable.getWalkSpeed());
						DecimalFormat format = new DecimalFormat("###########0.0");
						double time = (double) (ref.timeLeft / (20d / runnableSpeed)) - runnableSpeed / (20d / runnableSpeed);
						time = time < 0 ? 0 : time;
						p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', ChatColor.GRAY + "" + ChatColor.BOLD + (ref.type == RefillableUseType.Use ? "Using " : "Filling ") + refillable.getDisplayName() + ChatColor.GRAY + ChatColor.BOLD + "... time remaining " + ChatColor.YELLOW + ChatColor.BOLD + format.format(time))));
						ref.timeLeft -= runnableSpeed;
						//fill if fill type
						if (ref.type == RefillableUseType.Fill) {
							switch (refillable.getRefillableType()) {
							case Water:
								double maxFill = ref.refillable.getFilledAmount();
								double maxTime = ref.refillable.getFillTime();
								double diff = (maxTime - (double) ref.timeLeft) / maxTime;
								ref.totalFill = (double) maxFill * diff;
								break;
							}
						}
					} 
					//use refillable
					else {
						switch (ref.type) {
						case Fill:
							ItemStack finalItem = setFill(ref.item, ref.refillable.getFilledAmount());
							p.getInventory().setItem(ref.slot, finalItem);
							break;
						case Use:
							double useAmount = ref.refillable.getUseAmount();
							double fill = getFill(ref.item);
							double finalRemove = fill - useAmount < 0 ? 0 : fill - useAmount;
							finalItem = setFill(ref.item, finalRemove);
							p.getInventory().setItem(ref.slot, finalItem);
							double finalUse = fill - finalRemove;
							
							switch (refillable.getRefillableType()) {
							case Water: WaterBarManager.addWater(p, finalUse);
								break;
							}
							break;
						}
						p.setWalkSpeed(0.2f);
						removableUUIDs.add(uuid);
						p.playSound(p.getLocation(), refillable.getFinishSound(), 1, 1);
						p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', ChatColor.GRAY + "" + ChatColor.BOLD + "Successfully " + (ref.type == RefillableUseType.Use ? "used " : "filled ") + refillable.getDisplayName() + ChatColor.GRAY + ChatColor.BOLD + "!")));
					}
				}
				//remove used consumable instances
				for (UUID uuid : removableUUIDs) {
					refillablesInUse.remove(uuid);
					if (Bukkit.getOfflinePlayer(uuid).isOnline()) {
						Player p = Bukkit.getPlayer(uuid);
						p.setWalkSpeed(0.2f);
					}
				}
			}
		}.runTaskTimer(Main.getPlugin(Main.class), runnableSpeed, runnableSpeed);
	}
	
	public static void useRefillable(ItemStack item, int slot, Player p, RefillableUseType type) {
		if (isRefillable(item)) {
			RefillableInstance con = new RefillableInstance(item, slot, p, type);
			if (!refillablesInUse.containsKey(p.getUniqueId())) {
				refillablesInUse.put(p.getUniqueId(), con);
			}
		}
	}
	
	public static enum RefillableType {
		Water
	}
	
	public static enum RefillableUseType {
		Fill,
		Use
	}
	
	private static class RefillableInstance {
		
		public final ItemStack item;
		public final int slot;
		public int timeLeft;
		public final Refillable refillable;
		public final RefillableUseType type;
		public double totalFill;
		
		public RefillableInstance(ItemStack item, int slot, Player p, RefillableUseType type) {
			this.item = item.clone();
			this.slot = slot;
			this.type = type;
			this.totalFill = 0;
			this.refillable = getRefillableFromItem(item);
			this.timeLeft = type == RefillableUseType.Use ? refillable.getUseTime() : refillable.getFillTime();
			if (type == RefillableUseType.Fill) {
				double maxFill = getMaxFill(item);
				double fill = getFill(item);
				totalFill = fill;
				timeLeft = (int) ((double) timeLeft * (1 - fill / maxFill));
			}
		}
	}
}
