package com.coding.sirjavlux.listeners;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.coding.sirjavlux.core.ConfigManager;
import com.coding.sirjavlux.health.HealthEffects;
import com.coding.sirjavlux.utils.ScopeUtils;
import com.coding.sirjavlux.weapons.WeaponItem;
import com.coding.sirjavlux.weapons.WeaponManager;

import net.minecraft.server.v1_15_R1.NBTTagCompound;

public class PlayerListener implements Listener {

	@EventHandler
	public void playerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		Inventory iv = p.getInventory();
		playersInAir.put(uuid, null);
		for (ItemStack item : iv.getContents()) {
			//if weapon
			if (WeaponManager.isWeapon(item)) {
				//load data and set to weapon item obj
				WeaponManager.loadWeaponData(item);
			} 
			//if magazine
			else if (WeaponManager.isMagazine(item)) {
				//load data and set to magazine item obj
				WeaponManager.loadMagazineData(item);
			}
		}
		p.setWalkSpeed(0.2f);
	}
	
	@EventHandler
	public void playerLeave(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		playersInAir.remove(uuid);
	}
	
	private static HashMap<UUID, Location> playersInAir = new HashMap<>();
	
	@EventHandler
	public void playerStartFallEvent(PlayerMoveEvent e) {
		if ((e.getTo().getBlockX() != e.getFrom().getBlockX() || e.getTo().getBlockZ() != e.getFrom().getBlockZ() || e.getTo().getBlockY() != e.getFrom().getBlockY()) && e.getFrom().getWorld().equals(e.getTo().getWorld())) {
			Player p = e.getPlayer();
			UUID uuid = p.getUniqueId();
			Location pLoc = p.getLocation();
			if (!p.isOnGround() && playersInAir.get(uuid) == null) {
				playersInAir.replace(uuid, pLoc);
			} else if (p.isOnGround() &&  playersInAir.get(uuid) != null) {
				if (playersInAir.get(uuid).distance(pLoc) < 3) playersInAir.replace(uuid, null);
			}
		}
	}
	
	@EventHandler
	public void playerDamageEvent(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player && e.getCause().equals(DamageCause.FALL) && !(e.getEntity() instanceof NPC)) {
			Player p = (Player) e.getEntity();
			UUID uuid = p.getUniqueId();
			Location pLoc = p.getLocation();
			double distance = playersInAir.get(uuid).distance(pLoc);
			if (distance >= ConfigManager.getBreakLegHeight() && ConfigManager.breakLegEnabled()) {
				HealthEffects.breakLeg(p);
			}
			playersInAir.replace(uuid, null);
		}
	}
	
	@EventHandler
	public void switchSlotEvent(PlayerItemHeldEvent e) {
		Player p = e.getPlayer();
		if (ScopeUtils.isScoped(p)) ScopeUtils.unscope(p);		
		//check if weapon
		ItemStack item = p.getInventory().getItem(e.getPreviousSlot());
		if (WeaponManager.isWeapon(item)) {
    		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
    		NBTTagCompound tagComp = NMSItem.getTag();
			WeaponItem weaponItem = WeaponManager.getWeaponItem(UUID.fromString(tagComp.getString("uuid")));
			weaponItem.hardUpdate(item, p);
		}
	}
	
	@EventHandler
	public void playerDeathEvent(PlayerDeathEvent e) {
		e.setDeathMessage("");
	}
}
