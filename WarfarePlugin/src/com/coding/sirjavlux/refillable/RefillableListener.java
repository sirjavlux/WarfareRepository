package com.coding.sirjavlux.refillable;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.coding.sirjavlux.refillable.RefillableManager.RefillableUseType;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class RefillableListener implements Listener {

	@EventHandler
	public void damageEvent(PlayerInteractEvent e) {
		ItemStack item = e.getItem();
		Action action = e.getAction();
		Player p = e.getPlayer();
		if (RefillableManager.isRefillable(item)) {
			Refillable ref = RefillableManager.getRefillableFromItem(item);
			if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
				Block block = e.getClickedBlock();
				if (block != null) {
					switch (ref.getRefillableType()) {
					case Water:
						//check if water in front of player
						boolean isWater = false;
						Location eyeLoc = p.getEyeLocation().clone();
						Vector dir = eyeLoc.getDirection().clone().normalize();
						for (int i = 0; i < 4; i++) {
							if (eyeLoc.getBlock().getType() == Material.WATER) {
								isWater = true;
								break;
							} else eyeLoc = eyeLoc.add(dir);
						}
						if (isWater) {
							RefillableManager.useRefillable(item, p.getInventory().getHeldItemSlot(), p, RefillableUseType.Fill);
						}
						break;
					}
				}
			} else {
				if (RefillableManager.getFill(item) <= 0) {
					p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&7&lThe refillable " + ref.getDisplayName() + " &7&lwas empty!")));
					return;
				}
				switch (ref.getRefillableType()) {
				case Water:
					RefillableManager.useRefillable(item, p.getInventory().getHeldItemSlot(), p, RefillableUseType.Use);
					break;
				}
			}
		}
	}
}
