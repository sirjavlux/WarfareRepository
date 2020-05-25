package com.coding.sirjavlux.repair;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.coding.sirjavlux.armors.ArmorManager;
import com.coding.sirjavlux.core.Main;
import com.coding.sirjavlux.melee.MeleeManager;

import net.minecraft.server.v1_15_R1.NBTTagCompound;

public class RepairListener implements Listener {

	public RepairListener() {
		startDelayCooldown();
	}
	
	@EventHandler
	public void damageEvent(InventoryClickEvent e) {
		Inventory iv = e.getClickedInventory();
		if (iv != null) {
			Player p = (Player) e.getWhoClicked();
			ItemStack clicked = e.getCurrentItem();
			ItemStack cursor = e.getCursor();
			int slot = e.getSlot();
			if (RepairManager.isRepair(cursor)) {
				//get type
				RepairType type = null;
				if (MeleeManager.isMelee(clicked)) type = RepairType.MELEEREPAIR;
				if (ArmorManager.isArmor(clicked)) type = RepairType.ARMORREPAIR;
				if (type == null) return;
				//get data
				e.setCancelled(true);
				Repair repair = RepairManager.getRepairFromItem(cursor);
				int perUse = repair.getRepairPerUse();
				int delay = repair.getUseDelay();
				//check delay
				net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(cursor);
				NBTTagCompound tagComp = NMSItem.getTag();
				UUID uuid = UUID.fromString(tagComp.getString("uuid"));
				if (repairDelay.containsKey(uuid)) {
					if (repairDelay.get(uuid) > 0) return;
					else repairDelay.replace(uuid, delay);
				} else repairDelay.put(uuid, delay);
				//start repair
				switch (type) {
				case ARMORREPAIR:
					int durability = ArmorManager.getDurability(clicked);
					int maxDurability = ArmorManager.getMaxDurability(clicked);
					int useDur = durability + perUse > maxDurability ? perUse - (durability + perUse - maxDurability) : perUse;
					int dur = RepairManager.getDurability(cursor) - useDur;
					if (dur <= 0) {
						p.setItemOnCursor(new ItemStack(Material.AIR));
					} else {
						ItemStack newCursor = RepairManager.setDurability(cursor, dur).clone();
						p.setItemOnCursor(newCursor);
					}
					ItemStack newClicked = ArmorManager.setDurability(clicked, durability + perUse).clone();
					iv.setItem(slot, newClicked);
					break;
				case MELEEREPAIR:
					durability = MeleeManager.getDurability(clicked);
					maxDurability = MeleeManager.getMaxDurability(clicked);
					useDur = durability + perUse > maxDurability ? perUse - (durability + perUse - maxDurability) : perUse;
					dur = RepairManager.getDurability(cursor) - useDur;
					if (dur <= 0) {
						p.setItemOnCursor(new ItemStack(Material.AIR));
					} else {
						ItemStack newCursor = RepairManager.setDurability(cursor, dur).clone();
						p.setItemOnCursor(newCursor);
					}
					newClicked = MeleeManager.setDurability(clicked, durability + perUse).clone();
					iv.setItem(slot, newClicked);
					break;
				}
			}
		}
	}
	
	private HashMap<UUID, Integer> repairDelay = new HashMap<>();
	
	private void startDelayCooldown() {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Entry<UUID, Integer> entry : new HashMap<>(repairDelay).entrySet()) {
					UUID uuid = entry.getKey();
					int delay = entry.getValue();
					if (delay > 0) {
						if (repairDelay.containsKey(uuid)) repairDelay.replace(uuid, delay - 1);
					}
				}
			}
		}.runTaskTimerAsynchronously(Main.getPlugin(Main.class), 1, 1);
	}
}
