package com.coding.sirjavlux.grenades;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.coding.sirjavlux.projectiles.ProjectileManager;

public class GrenadeListener implements Listener {

	@EventHandler
	public void grenadeUseEvent(PlayerInteractEvent e) {
		ItemStack item = e.getItem();
		Player p = e.getPlayer();
		int slot = p.getInventory().getHeldItemSlot();
		Action action = e.getAction();
		if ((item != null) && (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK))) {
			Grenade grenade = GrenadeManager.getGrenadeFromItem(item);
			if (grenade != null) {
				p.playSound(p.getLocation(), grenade.getThrowSound(), 1, 1);
				ProjectileManager.fireGrenadeProjectile(p, grenade);
				//reduce item stack
				if (item.getAmount() > 1) {
					item.setAmount(item.getAmount() - 1);
				} else {
					p.getInventory().setItem(slot, new ItemStack(Material.AIR));
				}
			}
		}
	}
	
	@EventHandler
	public void inventoryClick(InventoryClickEvent e) {
		ItemStack cursor = e.getCursor();
		ItemStack item = e.getCurrentItem();
		Inventory iv = e.getClickedInventory();
		ClickType click = e.getClick();
		HumanEntity p = e.getWhoClicked();
		
		/*///////////////////////////////
		 * FIX GRENADE STACKING
		 *///////////////////////////////
		
		if (iv != null && GrenadeManager.isGrenade(item)) {
			if (GrenadeManager.isGrenade(item) && GrenadeManager.isGrenade(cursor) && (click.equals(ClickType.RIGHT) || click.equals(ClickType.LEFT))) {
				Grenade grenade = GrenadeManager.getGrenadeFromItem(item);
				Grenade grenadeC = GrenadeManager.getGrenadeFromItem(cursor);
				if (grenade != null && grenadeC != null) {
					if (grenadeC.getName().equals(grenade.getName())) {
						int amountEmpty = grenade.getMaxStackSize() - item.getAmount();
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
