package com.coding.sirjavlux.consumables;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

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
}
