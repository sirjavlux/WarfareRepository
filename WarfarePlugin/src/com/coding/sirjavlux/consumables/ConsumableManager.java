package com.coding.sirjavlux.consumables;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.coding.sirjavlux.core.Main;
import com.coding.sirjavlux.health.HealthEffects;
import com.coding.sirjavlux.utils.inventoryHandler;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_15_R1.NBTTagCompound;

public class ConsumableManager {

	protected static HashMap<String, Consumable> consumables = new HashMap<>();
	
	public static boolean isConsumable(String name) {
		return consumables.containsKey(name);
	}
	
	public static boolean isConsumable(ItemStack item) {
		boolean consumable = false;
		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
		if (NMSItem.hasTag()) {
			NBTTagCompound tagComp = NMSItem.getTag();
			if (tagComp.hasKey("name")) {
				if (consumables.containsKey(tagComp.getString("name"))) {
					consumable = true;
				}
			}
		}
		return consumable;
	}
	
	public static Consumable getStoredConsumable(String name) {
		return consumables.get(name);
	}
	
	public static Consumable getConsumableFromItem(ItemStack item) {
		Consumable consumable = null;
		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
		if (NMSItem.hasTag()) {
			NBTTagCompound tagComp = NMSItem.getTag();
			if (tagComp.hasKey("name")) {
				String name = tagComp.getString("name");
				if (consumables.containsKey(name)) {
					consumable = consumables.get(name);
				}
			}
		}
		return consumable;
	}
	
	/*//////////////////////////
	 * GIVE ITEM AND UPDATE
	 *//////////////////////////
	
	public static void giveConsumable(Player p, Consumable con, int amount) {
		//give item
		while (amount > 0) {
			ItemStack conItem = generateConsumable(con);
			inventoryHandler.giveToPlayer(p, conItem, p.getLocation());
			amount--;
		}
	}
	
	public static ItemStack generateConsumable(Consumable consumable) {
		ItemStack item = new ItemStack(consumable.getMaterial());
		
		//add nms tags
		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tagComp = NMSItem.hasTag() ? NMSItem.getTag() : new NBTTagCompound();
		tagComp.setString("name", consumable.getName());
		tagComp.setString("uuid", UUID.randomUUID().toString());
		tagComp.setInt("uses", consumable.getMaxUses());
		NMSItem.setTag(tagComp);
		item = CraftItemStack.asBukkitCopy(NMSItem);
		
		//update display data of item
		updateConsumableItem(item);
		return item;
	}
	
	//placeholders: %uses%
	public static void updateConsumableItem(ItemStack item) {
		Consumable consumable = getConsumableFromItem(item);
		if (consumable != null) {
			ItemMeta meta = item.getItemMeta();
			net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
			NBTTagCompound tagComp = NMSItem.getTag();
			int uses = tagComp.getInt("uses");
			
			//displayName
			String displayName = consumable.getDisplayName();
			displayName = ChatColor.translateAlternateColorCodes('&', displayName);
			meta.setDisplayName(displayName);
			
			//lore
			List<String> loreList = new ArrayList<>();
			List<String> lore = consumable.getLore();
			for (int i = 0; i < lore.size(); i++) {
				loreList.add(ChatColor.translateAlternateColorCodes('&', lore.get(i).replaceAll("%uses%", uses + "")));
			}
			meta.setLore(loreList);
			
			//set custom texture
			meta.setCustomModelData(consumable.getModelData());
			meta.setUnbreakable(true);
			
			meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
			meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			
			item.setItemMeta(meta);
			item.setType(consumable.getMaterial());
		}
	}
	
	/*////////////////////
	 * REDUCE ITEM USES
	 *////////////////////
	
	public static void reduceUses(ItemStack item, int slot, Player p) {
		Consumable consumable = getConsumableFromItem(item);
		if (consumable != null) {
			net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
			NBTTagCompound tagComp = NMSItem.getTag();
			int usesLeft = tagComp.getInt("uses") - 1;
			//remove item if no uses left
			if (usesLeft < 1) {
				p.getInventory().setItem(slot, new ItemStack(Material.AIR));
			}
			//decrease usages and update item
			else {
				tagComp.setInt("uses", usesLeft);
				NMSItem.setTag(tagComp);
				item = CraftItemStack.asBukkitCopy(NMSItem);
				updateConsumableItem(item);
				p.getInventory().setItem(slot, item);
			}
		}
	}
	
	/*///////////////////////////
	 * CONSUMABLE RUNNABLE
	 *///////////////////////////
	
	public static HashMap<UUID, CunsumableInstance> consumablesInUse = new HashMap<>();
	
	public static void startConsumableRunnable() {
		final int runnableSpeed = 2;
		new BukkitRunnable() {
			@Override
			public void run() {
				List<UUID> removableUUIDs = new ArrayList<>();
				//run trough consumables and use/remove time
				for (Entry<UUID, CunsumableInstance> entry : consumablesInUse.entrySet()) {
					UUID uuid = entry.getKey();
					CunsumableInstance con = entry.getValue();
					Consumable consumable = con.consumable;
					//check if instance is invalid
					if (!Bukkit.getOfflinePlayer(uuid).isOnline()) {
						removableUUIDs.add(uuid);
						continue;
					}
					Player p = Bukkit.getPlayer(uuid);
					if (!isConsumable(p.getInventory().getItemInMainHand())) {
						removableUUIDs.add(uuid);
						continue;
					}
					if (p.getInventory().getHeldItemSlot() != con.slot) {
						removableUUIDs.add(uuid);
						continue;
					}
					if (p.getInventory().getItemInMainHand() == con.item) {
						removableUUIDs.add(uuid);
						continue;
					}
					//remove time
					if (con.timeLeft > 0) {
						if (con.timeLeft % 10 == 0 && con.timeLeft > 20) p.playSound(p.getLocation(), consumable.getUseSound(), 1, 1);
						p.setWalkSpeed((float) consumable.getWalkSpeed());
						DecimalFormat format = new DecimalFormat("###########0.0");
						double time = (double) (con.timeLeft / (20d / runnableSpeed)) - runnableSpeed / (20d / runnableSpeed);
						time = time < 0 ? 0 : time;
						p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', ChatColor.GRAY + "" + ChatColor.BOLD + "Using " + consumable.getDisplayName() + ChatColor.GRAY + ChatColor.BOLD + "... time remaining " + ChatColor.YELLOW + ChatColor.BOLD + format.format(time))));
						con.timeLeft -= runnableSpeed;
					} 
					//use consumable 
					else {
						if (consumable.getBandage()) HealthEffects.removeBleeding(p);
						if (consumable.getConcussion()) HealthEffects.removeConcussion(p);
						if (consumable.getSplint()) HealthEffects.removeBrokenLeg(p);
						if (consumable.getHeal() > 0) {
							double conHealth = consumable.getHeal();
							double health = p.getHealth();
							double maxHealth = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
							double finalHealth = health + conHealth > maxHealth ? maxHealth : health + conHealth;
							p.setHealth(finalHealth);
							reduceUses(con.item, con.slot, p);
						}
						p.setWalkSpeed(0.2f);
						removableUUIDs.add(uuid);
						p.playSound(p.getLocation(), consumable.getFinishSound(), 1, 1);
						p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', ChatColor.GRAY + "" + ChatColor.BOLD + "Successfully used " + consumable.getDisplayName() + ChatColor.GRAY + ChatColor.BOLD + "!")));
					}
				}
				//remove used consumable instances
				for (UUID uuid : removableUUIDs) {
					consumablesInUse.remove(uuid);
					if (Bukkit.getOfflinePlayer(uuid).isOnline()) {
						Player p = Bukkit.getPlayer(uuid);
						p.setWalkSpeed(0.2f);
					}
				}
			}
		}.runTaskTimer(Main.getPlugin(Main.class), runnableSpeed, runnableSpeed);
	}
	
	public static void useConsumable(ItemStack item, int slot, Player p) {
		if (isConsumable(item)) {
			CunsumableInstance con = new CunsumableInstance(item, slot, p);
			if (!consumablesInUse.containsKey(p.getUniqueId())) {
				consumablesInUse.put(p.getUniqueId(), con);
			}
		}
	}
	
	private static class CunsumableInstance {
		
		public final ItemStack item;
		public final int slot;
		public int timeLeft;
		public final Consumable consumable;
		
		public CunsumableInstance(ItemStack item, int slot, Player p) {
			this.item = item;
			this.slot = slot;
			this.consumable = getConsumableFromItem(item);
			this.timeLeft = consumable.getUseTime();
		}
	}
}
