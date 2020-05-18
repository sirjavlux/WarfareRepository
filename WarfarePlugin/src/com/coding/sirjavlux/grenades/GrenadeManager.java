package com.coding.sirjavlux.grenades;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.coding.sirjavlux.core.Main;
import com.coding.sirjavlux.effects.Effect;
import com.coding.sirjavlux.effects.ExplosiveEffect;
import com.coding.sirjavlux.effects.FireEffect;
import com.coding.sirjavlux.effects.FlashEffect;
import com.coding.sirjavlux.effects.SmokeEffect;
import com.coding.sirjavlux.utils.inventoryHandler;

import net.minecraft.server.v1_15_R1.NBTTagCompound;

public class GrenadeManager {

	protected static HashMap<String, Grenade> grenades = new HashMap<>();
	
	public static boolean isGrenade(String name) {
		return grenades.containsKey(name);
	}
	
	public static boolean isGrenade(ItemStack item) {
		boolean grenade = false;
		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
		if (NMSItem.hasTag()) {
			NBTTagCompound tagComp = NMSItem.getTag();
			if (tagComp.hasKey("name")) {
				if (grenades.containsKey(tagComp.getString("name"))) {
					grenade = true;
				}
			}
		}
		return grenade;
	}
	
	public static Grenade getStoredGrenade(String name) {
		return grenades.get(name);
	}
	
	public static Grenade getGrenadeFromItem(ItemStack item) {
		Grenade grenade = null;
		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
		if (NMSItem.hasTag()) {
			NBTTagCompound tagComp = NMSItem.getTag();
			if (tagComp.hasKey("name")) {
				String name = tagComp.getString("name");
				if (grenades.containsKey(name)) {
					grenade = grenades.get(name);
				}
			}
		}
		return grenade;
	}
	
	/*//////////////////////////
	 * GIVE ITEM AND UPDATE
	 *//////////////////////////
	
	public static void giveGrenade(Player p, Grenade grenade, int amount) {
		int maxStack = grenade.getMaxStackSize();
		//give item
		while (amount > 0) {
			ItemStack grenadeItem = generateGrenade(grenade, 1);
			int amountToAdd = amount > maxStack ? maxStack : amount;
			amount -= amountToAdd;
			grenadeItem.setAmount(amountToAdd);
			inventoryHandler.giveToPlayer(p, grenadeItem, p.getLocation());
		}
	}
	
	public static ItemStack generateGrenade(Grenade grenade, int amount) {
		ItemStack item = new ItemStack(grenade.getMaterial());
		
		//add nms tags
		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tagComp = NMSItem.hasTag() ? NMSItem.getTag() : new NBTTagCompound();
		tagComp.setString("name", grenade.getName());
		tagComp.setString("uuid", UUID.randomUUID().toString());
		NMSItem.setTag(tagComp);
		item = CraftItemStack.asBukkitCopy(NMSItem);
		
		//update display data of item
		updateGrenadeItem(item);
		item.setAmount(amount > item.getMaxStackSize() ? item.getMaxStackSize() : amount);
		return item;
	}
	
	public static void updateGrenadeItem(ItemStack item) {
		Grenade grenade = getGrenadeFromItem(item);
		if (grenade != null) {
			ItemMeta meta = item.getItemMeta();
			
			//displayName
			String displayName = grenade.getDisplayName();
			displayName = ChatColor.translateAlternateColorCodes('&', displayName);
			meta.setDisplayName(displayName);
			
			//lore
			List<String> loreList = new ArrayList<>();
			List<String> lore = grenade.getLore();
			for (int i = 0; i < lore.size(); i++) {
				loreList.add(ChatColor.translateAlternateColorCodes('&', lore.get(i)));
			}
			meta.setLore(loreList);
			
			//set custom texture
			meta.setCustomModelData(grenade.getModelData());
			meta.setUnbreakable(true);
			
			meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
			meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			
			item.setItemMeta(meta);
			item.setType(grenade.getMaterial());
		}
	}
	
	/*////////////////////////////
	 *GRENADE HANDLER 
	 *////////////////////////////
	
	private static HashMap<UUID, GrenadeTimer> grenadeTimers = new HashMap<>();
	public static void startGrenadeHandler() {
		final int runnableSpeed = 5;
		new BukkitRunnable() {
			@Override
			public void run() {
				List<UUID> removeGrenades = new ArrayList<>();
				//go trough timers
				for (Entry<UUID, GrenadeTimer> entry : new HashMap<>(grenadeTimers).entrySet()) {
					GrenadeTimer timer = entry.getValue();
					UUID uuid = entry.getKey();
					Entity entity = Bukkit.getEntity(timer.getUniqueId());
					int timeLeft = timer.getTimeLeft() - runnableSpeed;
					if (timeLeft < 1) {
						if (entity != null) {
							if (!entity.isDead()) {
								Grenade grenade = timer.getGrenade();
								Location loc = entity.getLocation();
								Player p = timer.getPlayer();
								switch (grenade.getType()) {
								case Fire_Delayed: 
									loc.getWorld().playSound(loc, grenade.getExplodeSound(), 5, 1);
									Effect effect = new FireEffect(new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ()), grenade.getDuration(), grenade.getExplosionDamage(), grenade.getExplosionRange(), 0.2, grenade.getFireTicks(), (int) (4d * grenade.getExplosionRange()), grenade.getExplosionDamageDrop(), p, loc.getDirection().clone());
									effect.playEffect();
									break;
								case Flash_Delayed: 
									loc.getWorld().playSound(loc, grenade.getExplodeSound(), 5, 1);
									effect = new FlashEffect(new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ()), grenade.getDuration(), grenade.getExplosionDamage(), grenade.getExplosionRange(), grenade.getExplosionRange(), grenade.getFireTicks(), (int) (4d * grenade.getExplosionRange()), loc.getDirection().clone());
									effect.playEffect();
									break;
								case Smoke_Delayed: 
									loc.getWorld().playSound(loc, grenade.getExplodeSound(), 5, 1);
									effect = new SmokeEffect(new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ()), grenade.getDuration(), grenade.getExplosionDamage(), grenade.getExplosionRange(), grenade.getExplosionRange() * 0.72, grenade.getFireTicks(), loc.getDirection().clone());
									effect.playEffect();
									break;
								case Explosion_Delayed:
									loc.getWorld().playSound(loc, grenade.getExplodeSound(), 5, 1);
									effect = new ExplosiveEffect(new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ()), grenade.getExplosionDamage(), grenade.getExplosionRange(), grenade.getExplosionRange(), grenade.getFireTicks(), grenade.getExplosionDamageDrop(), p);
									effect.playEffect();
								default:
									break;
								}
							}
						}
						removeGrenades.add(uuid);
					} else {
						timer.setTimeLeft(timeLeft);
					}
				}
				//remove timers and kill entities
				for (UUID uuid : removeGrenades) {
					GrenadeTimer timer = grenadeTimers.get(uuid);
					Entity entity = timer.getEntity();
					if (entity != null) {
						List<Entity> passengers = entity.getPassengers();
						if (passengers.size() > 0) for (Entity passenger : passengers) passenger.remove();
						entity.remove();
					}
					grenadeTimers.remove(uuid);
				}
			}
		}.runTaskTimer(Main.getPlugin(Main.class), runnableSpeed, runnableSpeed);
	}
	
	public static UUID addGrenadeTimer(UUID uuid, Grenade grenade, Player p) {
		GrenadeTimer timer = new GrenadeTimer(uuid, grenade, p);
		UUID timerUUID = UUID.randomUUID();
		grenadeTimers.put(timerUUID, timer);
		return timerUUID;
	}
	
	public static GrenadeTimer getGrenadeTimer(UUID uuid) {
		return grenadeTimers.containsKey(uuid) ? grenadeTimers.get(uuid) : null;
	}
	
	public static class GrenadeTimer {
		private Player p;
		private int timeLeft;
		private UUID uuid;
		private Grenade grenade;
		
		public GrenadeTimer(UUID uuid, Grenade grenade, Player p) {
			this.uuid = uuid;
			this.grenade = grenade;
			this.timeLeft = grenade.getTimerTime();
			this.p = p;
		}
		
		public UUID getUniqueId() { return uuid; }
		public Grenade getGrenade() { return grenade; }
		public Entity getEntity() { return Bukkit.getEntity(uuid); }
		public int getTimeLeft() { return timeLeft; }
		public Player getPlayer() { return p; }
		
		public void setTimeLeft(int time) { timeLeft = time; }
		public void setUniqueId(UUID uuid) { this.uuid = uuid; }
	}
}
