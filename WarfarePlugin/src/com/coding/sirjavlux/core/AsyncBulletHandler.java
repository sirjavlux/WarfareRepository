package com.coding.sirjavlux.core;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
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
import com.coding.sirjavlux.types.AmmoType;
import com.coding.sirjavlux.types.Weapon;
import com.coding.sirjavlux.types.WeaponType;
import com.coding.sirjavlux.utils.Color;

import net.minecraft.server.v1_15_R1.NBTTagCompound;

public class AsyncBulletHandler implements Listener {
	
	//private Thread thread;
	private static HashMap<UUID, Long> lastClickInput = new HashMap<>();
	private static HashMap<UUID, Long> lastShot = new HashMap<>();
	//List<Bullet> waitingShot = new ArrayList<Bullet>();
	private static HashMap<UUID, ActiveWeapon> activeWeapons = new HashMap<>();
	
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
			
			e.setCancelled(true);
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
    			for (Entry<UUID, ActiveWeapon> entry : activeWeapons.entrySet()) {
    				UUID uuid = entry.getKey();
    				ActiveWeapon activeWeapon = entry.getValue();
    	    		Weapon weapon = activeWeapon.getWeapon();
    	    		
    	    		//continue and remove current firing instance if requirements aren't met
    	    		if (!Bukkit.getOfflinePlayer(uuid).isOnline()) {
    	    			activeWeapons.remove(uuid);
						continue;
					} else if ((System.currentTimeMillis() - lastClickInput.get(uuid)) / 50 > 4 && !weapon.getType().equals(WeaponType.Burst)) {
						WeaponManager.reduceAmmo(activeWeapon.getAmmoUsed(), activeWeapon.getItemStack(), Bukkit.getPlayer(uuid));
						activeWeapons.remove(uuid);
						continue;
					}
    	    		
    	    		//check if requirements for each weapon type are met
    	    		int burstBulletsLeft = weapon.getBurstAmount() - activeWeapon.getAmmoUsed();
    	    		long lastS = lastShot.get(uuid);
    	    		switch (weapon.getType()) {
					case Auto: 
						if (activeWeapon.getStartAmmoAmount() - activeWeapon.getAmmoUsed() < 1) {
							WeaponManager.reduceAmmo(activeWeapon.getAmmoUsed(), activeWeapon.getItemStack(), Bukkit.getPlayer(uuid));
							activeWeapons.remove(uuid);
							continue;
						} else if (20 / weapon.getFireRate() > (System.currentTimeMillis() - lastS) / 50) continue;
						break;
					case Burst: 
						if (burstBulletsLeft < 1 || activeWeapon.getStartAmmoAmount() - activeWeapon.getAmmoUsed() < 1) {
							WeaponManager.reduceAmmo(activeWeapon.getAmmoUsed(), activeWeapon.getItemStack(), Bukkit.getPlayer(uuid));
							activeWeapons.remove(uuid);
							continue;
						} else if (20 / weapon.getBurstSpeed() > (System.currentTimeMillis() - lastS) / 50) continue;
						break;
					default: System.out.println(Color.RED + "ERROR The weapon " + weapon.getName() + " wasn't an auto or burst type!");
						continue;
    	    		}
    	    		
    	    		//setup shot and add bullet to waiting list
    	    		Ammo ammo = activeWeapon.getNextAmmo();
    	    		Bullet bullet = new Bullet(uuid, weapon, ammo);
    	    		if (Bukkit.getOfflinePlayer(bullet.getPlayerUniqueId()).isOnline()) {
						Player p = Bukkit.getPlayer(bullet.getPlayerUniqueId());
						shootBullet(p, weapon, ammo);
					}
    	    		
    	    		//update active weapon data and finish shooting if burst finished or no ammo left
    	    		boolean isEmpty = activeWeapon.removeRound();
    	    		if (isEmpty || (burstBulletsLeft-- < 1 && weapon.getType().equals(WeaponType.Burst))) {
    	    			WeaponManager.reduceAmmo(activeWeapon.getAmmoUsed(), activeWeapon.getItemStack(), Bukkit.getPlayer(uuid));
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
    		String name = tagComp.getString("name");
    		Weapon weapon = WeaponManager.getStoredWeapon(name);
    		double fireRate = 20 / weapon.getFireRate();
    		long lastS = lastShot.get(uuid);
    		//check fire rate against system time
			if (fireRate < (System.currentTimeMillis() - lastS) / 50) {
				//shoot weapon depending on type
				switch (weapon.getType()) {
				case Auto: 
					if (!activeWeapons.containsKey(uuid)) activeWeapons.put(uuid, new ActiveWeapon(item));
					break;
				case Burst:	
					if (!activeWeapons.containsKey(uuid)) activeWeapons.put(uuid, new ActiveWeapon(item));
					break;
				default:
					//get next ammo in barrel and shoot a single bullet
					String barrelRounds = tagComp.getString("barrelRounds");
					String ammoStr = ((!barrelRounds.contains(",")) ? barrelRounds : barrelRounds.substring(barrelRounds.lastIndexOf(","))).replaceAll(",", "");
					if (WeaponManager.isAmmunition(ammoStr)) {
						Ammo ammo = WeaponManager.getStoredAmmo(ammoStr);
						shootBullet(p, weapon, ammo);
						WeaponManager.reduceAmmo(1, item, p);
					}
					break;
				}
			}
		}
	}
    
	@SuppressWarnings("unused")
    private static class Bullet {
    	
    	private UUID uuid;
    	private Weapon weapon;
    	private Ammo ammo;
    	
    	public Bullet(UUID uuid, Weapon weapon, Ammo ammo) {
    		this.uuid = uuid;
    		this.weapon = weapon;
    		this.ammo = ammo;
    	}
    	
    	public UUID getPlayerUniqueId() { return uuid; }
		public Weapon getWeapon() { return weapon; }
    	public Ammo getAmmo() { return ammo; }
    }
    
    private static class ActiveWeapon {
    	
    	private final ItemStack item;
    	private final NBTTagCompound tagComp;
    	private final Weapon weapon;
    	private final int startAmmo;
    	private Ammo nextAmmo;
    	private int ammoUsed;
    	private String barrelRounds;
    	private String magRounds;
    	
    	public ActiveWeapon(ItemStack item) {
    		this.item = item;
    		//get nbt tag and item
    		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
    		this.tagComp = NMSItem.getTag();
    		this.weapon = WeaponManager.getStoredWeapon(tagComp.getString("name"));
    		this.barrelRounds = tagComp.getString("barrelRounds");
    		this.magRounds = tagComp.getString("magRounds");
    		this.startAmmo = tagComp.getInt("barrelAmmo") + tagComp.getInt("magAmmo");
    		updateNextAmmo();
    	}
    	
    	public boolean removeRound() {
    		ammoUsed++;
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
			updateNextAmmo();
			return barrelRounds.isEmpty();
    	}
    	
    	public void updateNextAmmo() {
			String ammoStr = ((!barrelRounds.contains(",")) ? barrelRounds : barrelRounds.substring(barrelRounds.lastIndexOf(","))).replaceAll(",", "");
			if (WeaponManager.isAmmunition(ammoStr)) {
				this.nextAmmo = WeaponManager.getStoredAmmo(ammoStr);
			}
    	}
    	
    	public ItemStack getItemStack() { return item; }
    	public Weapon getWeapon() { return weapon; }
    	public Ammo getNextAmmo() { return nextAmmo; }
    	public int getAmmoUsed() { return ammoUsed; }
    	public int getStartAmmoAmount() { return startAmmo; }
    }
}
