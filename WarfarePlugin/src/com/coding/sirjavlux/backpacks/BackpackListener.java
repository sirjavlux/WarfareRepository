package com.coding.sirjavlux.backpacks;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import net.minecraft.server.v1_15_R1.NBTTagCompound;

public class BackpackListener implements Listener {

	@EventHandler
	public void ivClickEvent(InventoryClickEvent e) {
		Inventory iv = e.getClickedInventory();
		if (iv != null) {
			int slot = e.getSlot();
			InventoryAction action = e.getAction();
			Player p = (Player) e.getWhoClicked();
			if (iv.getType() == InventoryType.PLAYER) {
				ItemStack clicked = e.getCurrentItem();
				ItemStack cursor = e.getCursor();
				if (slot == 40) {
					//if clicked is backpack
					if (BackpackManager.isBackpack(clicked)) {
						BackpackManager.unloadBackPackItemsFromPlayer(p);
					}
					//if cursor is backpack
					if (BackpackManager.isBackpack(cursor)) {
						BackpackManager.loadBackPackItemsToPlayer(p, cursor);
					}	
				} else if (slot > 8 && slot < 27) {
					int size = 0;
					if (BackpackManager.isBackpack(p.getInventory().getItemInOffHand())) {
						Backpack pack = BackpackManager.getBackpackFromItem(p.getInventory().getItemInOffHand());
						size = pack.getPackSpace();
					} else updateInventory(p);
					if (slot < 9 + 18 - size) {
						e.setCancelled(true);
						p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
					} else if (BackpackManager.isBackpack(e.getCursor())) {
						e.setCancelled(true);
						p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
					} else if (BackpackManager.getSlotBlockItem().isSimilar(clicked)) {
						e.setCancelled(true);
						updateInventory(p);
					} else {
						net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(iv.getItem(40));
						NBTTagCompound tagComp = NMSItem.getTag();
						UUID uuid = UUID.fromString(tagComp.getString("uuid"));
						for (int i = 0; i < size; i++) {
							int bSlot = 8 + 18 - i;
							if (bSlot != slot) {
								ItemStack updateItem = iv.getItem(bSlot);
								BackpackManager.updateItemInInstance(p, uuid, updateItem == null ? new ItemStack(Material.AIR) : updateItem.clone(), size - i - 1);
							} else {
								//on swap action
								if (action == InventoryAction.SWAP_WITH_CURSOR || action == InventoryAction.PLACE_ALL || action == InventoryAction.PICKUP_ALL || action == InventoryAction.PLACE_SOME) {
									if (cursor.isSimilar(clicked)) {
										int cursorAmount = cursor.getAmount();
										ItemStack finalItem = clicked.clone();
										finalItem.setAmount(clicked.getAmount() + cursorAmount > clicked.getMaxStackSize() ? clicked.getMaxStackSize() : clicked.getAmount() + cursorAmount);
										BackpackManager.updateItemInInstance(p, uuid, finalItem.clone(), slot - 9 - (18 - size));
									} else BackpackManager.updateItemInInstance(p, uuid, cursor.clone(), slot - 9 - (18 - size));
								}
								//on place some
								else if (action == InventoryAction.PLACE_ONE) {
									ItemStack finalItem = cursor.clone();
									if (clicked.getType() != Material.AIR) {
										finalItem = clicked.clone();
										finalItem.setAmount(finalItem.getAmount() + 1 > finalItem.getMaxStackSize() ? finalItem.getMaxStackSize() : finalItem.getAmount() + 1);
									} else finalItem.setAmount(1);
									BackpackManager.updateItemInInstance(p, uuid, finalItem.clone(), slot - 9 - (18 - size));
								}
								//pickup one
								else if (action == InventoryAction.PICKUP_HALF) {
									int amountLeft = clicked.getAmount() / 2;
									ItemStack finalItem = clicked.clone();
									finalItem.setAmount(amountLeft);
									BackpackManager.updateItemInInstance(p, uuid, finalItem.clone(), slot - 9 - (18 - size));
								}
								//if no click matches cancel event
								else if (action != InventoryAction.CLONE_STACK) e.setCancelled(true);
							}
						}
					}
				}
			}
			if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void itemDragEvent(InventoryDragEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler 
	public void swapHandItemsEvent(PlayerSwapHandItemsEvent e) {
		ItemStack off = e.getOffHandItem();
		ItemStack main = e.getMainHandItem();
		Player p = e.getPlayer();
		//if clicked is backpack
		if (BackpackManager.isBackpack(main)) {
			BackpackManager.unloadBackPackItemsFromPlayer(p);
		}
		//if cursor is backpack
		if (BackpackManager.isBackpack(off)) {
			BackpackManager.loadBackPackItemsToPlayer(p, off);
		}	
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		updateInventory(p);
	}
	
	@EventHandler 
	public void respawnEvent(PlayerRespawnEvent e) {
		Player p = e.getPlayer();
		updateInventory(p);
	}
	
	@EventHandler 
	public void playerDeathEvent(PlayerDeathEvent e) {
		int i = 0;
		for (ItemStack item : new ArrayList<>(e.getDrops())) {
			if (item != null) {
				if (item.isSimilar(BackpackManager.getSlotBlockItem())) e.getDrops().set(i, new ItemStack(Material.AIR));
			}
			i++;
		}
	}
	
	public void updateInventory(Player p) {
		PlayerInventory iv = p.getInventory();
		ItemStack offHand = iv.getItemInOffHand();
		if (BackpackManager.isBackpack(offHand)) {
			Backpack pack = BackpackManager.getBackpackFromItem(offHand);
			int size = pack.getPackSpace();
			for (int i = 0; i < 18 - size; i++) {
				int slot = 9 + i;
				iv.setItem(slot, BackpackManager.getSlotBlockItem());
			}
		} else {
			for (int i = 0; i < 18; i++) {
				int slot = 9 + i;
				iv.setItem(slot, BackpackManager.getSlotBlockItem());
			}
		}
	}
	
	@EventHandler
	public void playerLeaveEvent(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		int count = 0;
		for (ItemStack item : p.getInventory().getContents()) {
			if (BackpackManager.isBackpack(item)) p.getInventory().setItem(count, BackpackManager.saveBackpackData(p, item));
			count++;
		}
	}
	
	@EventHandler
	public void ivMoveEvent(InventoryClickEvent e) {
		ItemStack item = e.getCursor();
		ItemStack current = e.getCurrentItem();
		Inventory iv = e.getClickedInventory();
		ClickType click = e.getClick();
		if (iv != null) {
			Player p = (Player) e.getWhoClicked();
			if (iv.getType() != InventoryType.PLAYER) {
				if (BackpackManager.isBackpack(item)) {
					int slot = e.getSlot();
					e.setCancelled(true);
					p.setItemOnCursor(e.getCurrentItem());
					iv.setItem(slot, BackpackManager.saveBackpackData(p, item));
				}
			} else if (iv.getType() == InventoryType.PLAYER && (click == ClickType.SHIFT_LEFT || click == ClickType.SHIFT_RIGHT)) {
				if (BackpackManager.isBackpack(current)) {
					e.setCurrentItem(BackpackManager.saveBackpackData(p, current));
				}
			}
		}
	}
	
	@EventHandler
	public void dropEvent(PlayerDropItemEvent e) {
		ItemStack item = e.getItemDrop().getItemStack();
		Player p = e.getPlayer();
		if (BackpackManager.isBackpack(item)) {
			e.getItemDrop().setItemStack(BackpackManager.saveBackpackData(p, item));
		}
	}
}
