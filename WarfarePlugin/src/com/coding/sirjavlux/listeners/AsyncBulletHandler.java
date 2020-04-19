package com.coding.sirjavlux.listeners;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
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

import com.coding.sirjavlux.core.Main;
import com.coding.sirjavlux.projectiles.ProjectileManager;
import com.coding.sirjavlux.types.Ammo;
import com.coding.sirjavlux.types.AmmoType;
import com.coding.sirjavlux.types.Weapon;
import com.coding.sirjavlux.types.WeaponType;
import com.coding.sirjavlux.utils.Color;
import com.coding.sirjavlux.weapons.WeaponItem;
import com.coding.sirjavlux.weapons.WeaponManager;

import net.minecraft.server.v1_15_R1.NBTTagCompound;

public class AsyncBulletHandler implements Listener {
	
	private static HashMap<UUID, Long> lastClickInput = new HashMap<>();
	private static HashMap<UUID, Long> lastShot = new HashMap<>();
	private static HashMap<UUID, WeaponItem> activeWeapons = new HashMap<>();
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		UUID uuid = e.getPlayer().getUniqueId();
		lastShot.put(uuid, System.currentTimeMillis());
		lastClickInput.put(uuid,System.currentTimeMillis());
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		UUID uuid = e.getPlayer().getUniqueId();
		lastShot.remove(uuid);
		lastClickInput.remove(uuid);
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
				startFireWeapon(p.getUniqueId(), item);
			}
			else if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
				startFireWeapon(p.getUniqueId(), item); 
			}
		}
	}
	
	public AsyncBulletHandler() {
		startBulletReader();
	}
    
	private void startBulletReader() {
		new BukkitRunnable() {
			@Override
			public void run() {
    			//run trough all active fireing weapons
    			for (Entry<UUID, WeaponItem> entry : activeWeapons.entrySet()) {
    				UUID uuid = entry.getKey();
    				WeaponItem activeWeapon = entry.getValue();
    	    		Weapon weapon = activeWeapon.getWeapon();
    	    		//continue and remove current firing instance if requirements aren't met
    	    		if (!Bukkit.getOfflinePlayer(uuid).isOnline()) {
    	    			activeWeapon.resetBurst();
    	    			activeWeapons.remove(uuid);
						continue;
					}
    	    		//cancel if player swaped item
    	    		Player p = Bukkit.getPlayer(uuid);
    	    		ItemStack hand = p.getInventory().getItemInMainHand();
    	    		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(hand);
    	    		NBTTagCompound tagComp = NMSItem.hasTag() ? NMSItem.getTag() : new NBTTagCompound();
    	    		if (!tagComp.hasKey("uuid")) {
    	    			int count = 0;
    	    			for (ItemStack item : p.getInventory().getContents()) {
    	    				if (WeaponManager.isWeapon(item)) {
    	        	    		net.minecraft.server.v1_15_R1.ItemStack NMSItemTemp = CraftItemStack.asNMSCopy(item);
    	        	    		NBTTagCompound tagTemp = NMSItemTemp.getTag();
    	    					UUID wUUID = UUID.fromString(tagTemp.getString("uuid"));
    	    					if (wUUID.equals(uuid)) {
        	    	    			activeWeapon.saveData(item, p, count);
        	    	    			activeWeapon.hardUpdate(item);
    	    					}
    	    				}
    	    				count++;
    	    			}
    	    			activeWeapons.remove(uuid);
						continue;
    	    		}
    	    		else if (!UUID.fromString(tagComp.getString("uuid")).equals(activeWeapon.getUniqueId())) {
    	    			activeWeapon.resetBurst();
    	    			int count = 0;
    	    			for (ItemStack item : p.getInventory().getContents()) {
    	    				if (WeaponManager.isWeapon(item)) {
    	        	    		net.minecraft.server.v1_15_R1.ItemStack NMSItemTemp = CraftItemStack.asNMSCopy(item);
    	        	    		NBTTagCompound tagTemp = NMSItemTemp.getTag();
    	    					UUID wUUID = UUID.fromString(tagTemp.getString("uuid"));
    	    					if (wUUID.equals(uuid)) {
        	    	    			activeWeapon.saveData(item, p, count);
        	    	    			activeWeapon.hardUpdate(item);
    	    					}
    	    				}
    	    				count++;
    	    			}
    	    			activeWeapons.remove(uuid);
						continue;
    	    		}
    	    		else if ((System.currentTimeMillis() - lastClickInput.get(uuid)) / 50 > 4 && !weapon.getType().equals(WeaponType.Burst)) {
    	    			activeWeapon.resetBurst();
    	    			activeWeapon.hardUpdate(hand);
    	    			activeWeapon.saveData(hand, p, p.getInventory().getHeldItemSlot());
    	    			activeWeapons.remove(uuid);
						continue;
    	    		}
    	    		//check if requirements for each weapon type are met
    	    		long lastS = lastShot.get(uuid);
    	    		switch (weapon.getType()) {
					case Auto: 
						if (activeWeapon.getBarrelAmmo().size() < 1) {
							p.sendMessage(ChatColor.RED + "Reload, ammunition empty!");
							activeWeapons.remove(uuid);
							continue;
						} else if (20 / weapon.getFireRate() > (System.currentTimeMillis() - lastS) / 50) continue;
						break;
					case Burst: 
						if (activeWeapon.getBurstAmountRemaning() < 1 || activeWeapon.getBarrelAmmo().size() < 1) {
							p.sendMessage(ChatColor.RED + "Reload, ammunition empty!");
							activeWeapons.remove(uuid);
							continue;
						} else if (20 / weapon.getBurstSpeed() > (System.currentTimeMillis() - lastS) / 50) continue;
						break;
					default: System.out.println(Color.RED + "ERROR The weapon " + weapon.getName() + " wasn't an auto or burst type!");
						continue;
    	    		}
    	    		//setup shot and add bullet to waiting list
    	    		Ammo ammo = activeWeapon.getNextAmmo();
    	    		shootBullet(p, weapon, ammo);
    	    		//update active weapon data and finish shooting if burst finished or no ammo left
    	    		boolean isEmpty = activeWeapon.removeBullet();
    	    		activeWeapon.update(hand);
    	    		if (isEmpty) {
    	    			activeWeapon.hardUpdate(hand);
    	    			activeWeapon.saveData(hand, p, p.getInventory().getHeldItemSlot());
    	    			activeWeapons.remove(uuid);
    	    		}
    			}
			}
		}.runTaskTimer(Main.getPlugin(Main.class), 1, 1);
	}
	
	private void shootBullet(Player p, Weapon weapon, Ammo ammo) {
		//shoot bullet if weapon and ammo are valid
		if (ammo != null && weapon != null) {
			double speed = ammo.getSpeed();
			AmmoType type = ammo.getAmmoType();
			ProjectileManager.fireProjectile(p, speed, weapon, ammo, type);
			lastShot.replace(p.getUniqueId(), System.currentTimeMillis());
		}
	}
	
	private void startFireWeapon(UUID uuid, ItemStack item) {
		if (WeaponManager.isWeapon(item) && Bukkit.getOfflinePlayer(uuid).isOnline()) {
			Player p = Bukkit.getPlayer(uuid);
			net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
    		NBTTagCompound tagComp = NMSItem.getTag();
    		UUID wUUID = UUID.fromString(tagComp.getString("uuid"));
			WeaponItem weaponItem = WeaponManager.getWeaponItem(wUUID);
    		Weapon weapon = weaponItem.getWeapon();
    		double fireRate = 20 / weapon.getFireRate();
    		long lastS = lastShot.get(uuid);
    		//check fire rate against system time
			if (fireRate < (System.currentTimeMillis() - lastS) / 50) {
				//shoot weapon depending on type
				switch (weapon.getType()) {
				case Auto: 
					if (!activeWeapons.containsKey(uuid)) activeWeapons.put(uuid, weaponItem);
					break;
				case Burst:	
					if (!activeWeapons.containsKey(uuid)) activeWeapons.put(uuid, weaponItem);
					break;
				default:
					//get next ammo in barrel and shoot a single bullet
					Ammo ammo = weaponItem.getNextAmmo();
					if (ammo != null) {
						shootBullet(p, weapon, ammo);
						weaponItem.removeBullet();
						weaponItem.hardUpdate(item);
						weaponItem.saveData(item, p, p.getInventory().getHeldItemSlot());
					} else {
						p.sendMessage(ChatColor.RED + "Reload, ammunition empty!");
					}
					break;
				}
			}
		}
	}
}
