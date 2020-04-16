package com.coding.sirjavlux.core;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.coding.sirjavlux.projectiles.ProjectileManager;
import com.coding.sirjavlux.types.Ammo;
import com.coding.sirjavlux.types.Weapon;

import net.minecraft.server.v1_15_R1.NBTTagCompound;

public class WeaponUseListener implements Listener {

	private static HashMap<UUID, Long> lastShot = new HashMap<>();
	private static HashMap<UUID, Long> lastClickInput = new HashMap<>();
	private static HashMap<UUID, Boolean> isShooting = new HashMap<>();
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		UUID uuid = e.getPlayer().getUniqueId();
		lastShot.put(uuid, System.currentTimeMillis());
		lastClickInput.put(uuid,System.currentTimeMillis());
		isShooting.put(uuid, false);
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		UUID uuid = e.getPlayer().getUniqueId();
		lastShot.remove(uuid);
		lastClickInput.remove(uuid);
		isShooting.remove(uuid);
	}
	
	@EventHandler
	public void playerInteractEvent(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		Action action = e.getAction();
		ItemStack item = e.getItem();
		//check if weapon
		if (WeaponManager.isWeapon(item)) {
			UUID uuid = p.getUniqueId();
			lastClickInput.replace(uuid, System.currentTimeMillis());
			
			if ((action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) && p.isSneaking()) {
				
			}
			else if ((action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) && p.isSneaking()) {
				
			}
			else if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) {
				shoot(item, p);
			}
			else if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
				shoot(item, p);
			}
			
			e.setCancelled(true);
		}
	}
	
	private static void shoot(ItemStack item, Player p) {
		
		//calculate ticks since last shot and return if to fast
		//Long lastTimeMillis = lastShot.get(uuid);
		//double ticks = (System.currentTimeMillis() - lastTimeMillis) / 50;
		//System.out.println("ticks since last shot " + ticks);
		
		startAutoShooting(item, p);
	}
	
	/*/////////////////////////////
	 * AUTOMATIC SHOOTING
	 */////////////////////////////
	private static void startAutoShooting(ItemStack item, Player p) {
		//get nbt tag and item
		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tagComp = NMSItem.getTag();
		Weapon weapon = WeaponManager.getStoredWeapon(tagComp.getString("name"));
		double fireRate = weapon.getFireRate();
		long ticksCooldown = (long) (20 / fireRate) < 1 ? 1 : (long) (20 / fireRate);
		
		UUID uuid = p.getUniqueId();
		if (!isShooting.get(uuid)) {
			isShooting.replace(uuid, true);
			new BukkitRunnable() {
				long ticksCooldown = (long) (20 / fireRate) < 1 ? 1 : (long) (20 / fireRate);
				int ammoUsed = 0;
				int ammo = tagComp.getInt("barrelAmmo") + tagComp.getInt("magAmmo");
				
				String barrelRounds = tagComp.getString("barrelRounds");
				String magRounds = tagComp.getString("magRounds");
				
				@Override
				public void run() {
					if (!p.isOnline() || (System.currentTimeMillis() - lastClickInput.get(uuid)) / 50 > 4) {
						WeaponManager.reduceAmmo(ammoUsed, item, p);
						cancel();
						isShooting.replace(uuid, false);
					} else {
						//check if to early from last shot
						if ((System.currentTimeMillis() - lastShot.get(uuid))  / 50 >= ticksCooldown * 0.7) {
							//shoot if bullets in barrel
							if (ammo > 0) {
								ammo--;
								ammoUsed++;
								String ammoStr = ((!barrelRounds.contains(",")) ? barrelRounds : barrelRounds.substring(barrelRounds.lastIndexOf(","))).replaceAll(",", "");
								if (WeaponManager.isAmmunition(ammoStr)) {
									Ammo ammo = WeaponManager.getStoredAmmo(ammoStr);
									double damage = ammo.getDamage();
									double speed = ammo.getSpeed();
									ProjectileManager.fireProjectile(p, speed, damage);
								}
								
								//remove front bullet
								barrelRounds = barrelRounds.contains(",") ? barrelRounds.substring(0, barrelRounds.lastIndexOf(",")) : "";
								
								//add from magazine to barrel if has mag and remove from mag
								if (weapon.requiresMagazine()) {
									if (tagComp.getString("mag") != "none") {
										String bulletToAdd = ((!magRounds.contains(",")) ? magRounds : magRounds.substring(magRounds.lastIndexOf(","))).replaceAll(",", "");
										barrelRounds = barrelRounds.isEmpty() ? bulletToAdd : "," + bulletToAdd;
										//remove from magrounds
										magRounds = !magRounds.contains(",") ? magRounds.substring(0, magRounds.lastIndexOf(",")) : "";
									}
								}
								lastShot.replace(uuid, System.currentTimeMillis());
								
								//cancel runnable if empty ammo
								if (ammo < 1) {
									WeaponManager.reduceAmmo(ammoUsed, item, p);
									cancel();
									isShooting.replace(uuid, false);
								}
							} else {
								p.sendMessage(ChatColor.RED + "No ammo left, reload!");
								cancel();
								isShooting.replace(uuid, false);
							}	
						}
					}
				}
				
			}.runTaskTimer(Main.getPlugin(Main.class), 0, ticksCooldown);
		}
	}
	
	/*/////////////////////////////
	 * SEMI-AUTOMATIC SHOOTING
	 */////////////////////////////
	
	
	/*/////////////////////////////
	 * SINGLE-FIRE SHOOTING
	 */////////////////////////////
	
	
	/*/////////////////////////////
	 * BURST SHOOTING
	 */////////////////////////////
	
}
