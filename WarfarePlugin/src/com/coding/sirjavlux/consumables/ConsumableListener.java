package com.coding.sirjavlux.consumables;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_15_R1.NBTTagCompound;

public class ConsumableListener implements Listener {

	@EventHandler
	public void consumableUseEvent(PlayerInteractEvent e) {
		ItemStack item = e.getItem();
		Player p = e.getPlayer();
		Action action = e.getAction();
		if ((item != null) && (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK))) {
			if (ConsumableManager.isConsumable(item)) {
				ConsumableManager.useConsumable(item, p.getInventory().getHeldItemSlot(), p);
			}
		}
	}
	
	@EventHandler
	public void ivClickEvent(InventoryClickEvent e) {
		ItemStack item = e.getCurrentItem();
		ItemStack cursor = e.getCursor();
		ClickType click = e.getClick();
		Player p = (Player) e.getWhoClicked();
		
		/*///////////////////////////////
		 * FIX CONSUMABLE STACKING
		 *///////////////////////////////
		
		//if consumable
		if (ConsumableManager.isConsumable(item) && ConsumableManager.isConsumable(cursor) && (click.equals(ClickType.RIGHT) || click.equals(ClickType.LEFT))) {
			net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
			NBTTagCompound tagComp = NMSItem.getTag();
			Consumable con = ConsumableManager.getStoredConsumable(tagComp.getString("name"));
			net.minecraft.server.v1_15_R1.ItemStack NMSItemC = CraftItemStack.asNMSCopy(cursor);
			NBTTagCompound tagCompC = NMSItemC.getTag();
			Consumable conC = ConsumableManager.getStoredConsumable(tagCompC.getString("name"));
			if (con != null && conC != null) {
				if (con.getMaxStackSize() > 1 && conC.getMaxStackSize() > 1) {
					if (conC.getName().equals(con.getName())) {
						int amountEmpty = con.getMaxStackSize() - item.getAmount();
						int cAmount = cursor.getAmount();
						if (amountEmpty > 0 && cAmount > 0) {
							int amountToAdd = click.equals(ClickType.RIGHT) ? 1 : (cAmount > amountEmpty ? amountEmpty : cAmount);
							item.setAmount(item.getAmount() + amountToAdd);
							if (amountToAdd >= cAmount) {
								p.setItemOnCursor(new ItemStack(Material.AIR));
							} else {
								ItemStack cItem = cursor.clone();
								cItem.setAmount(cItem.getAmount() - amountToAdd);
								p.setItemOnCursor(cItem);
							}
							e.setCancelled(true);
							return;	
						}
					}	
				}
			}
		}
	}
}
