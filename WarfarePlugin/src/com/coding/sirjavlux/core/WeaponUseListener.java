package com.coding.sirjavlux.core;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.coding.sirjavlux.projectiles.ProjectileManager;
import com.coding.sirjavlux.types.Ammo;

import net.minecraft.server.v1_15_R1.NBTTagCompound;

public class WeaponUseListener implements Listener {

	@EventHandler
	public void playerInteractEvent(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		Action action = e.getAction();
		ItemStack item = e.getItem();
		//check if weapon
		if (WeaponManager.isWeapon(item)) {
			
			//get nbt tag and item
			net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
			NBTTagCompound tagComp = NMSItem.getTag();
			//Weapon weapon = WeaponManager.getStoredWeapon(tagComp.getString("name"));
			int roundsInBarrel = tagComp.getInt("barrelAmmo");
			
			///////////////////
			//RELOAD
			///////////////////
			
			if ((action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) && p.isSneaking()) {
				
			}
			
			///////////////////
			//SHOOT
			///////////////////
			
			else if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) {
				if (roundsInBarrel > 0) {
					String barrelRounds = tagComp.getString("barrelRounds");
					String ammoStr = (barrelRounds.substring(barrelRounds.lastIndexOf(",")).isEmpty() ? barrelRounds : barrelRounds.substring(barrelRounds.lastIndexOf(","))).replaceAll(",", "");
					if (WeaponManager.isAmmunition(ammoStr)) {
						Ammo ammo = WeaponManager.getStoredAmmo(ammoStr);
						double damage = ammo.getDamage();
						double speed = ammo.getSpeed();
						ProjectileManager.fireProjectile(p, speed, damage);
					}
					WeaponManager.reduceAmmo(1, item, p);
				} else {
					p.sendMessage(ChatColor.RED + "No ammo left, reload!");
				}
			}
			
			///////////////////
			//SCOPE
			///////////////////
			
			else if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
				
			}
			
			e.setCancelled(true);
		}
	}
}
