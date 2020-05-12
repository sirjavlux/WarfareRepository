package com.coding.sirjavlux.weapons;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import com.coding.sirjavlux.core.Main;
import com.coding.sirjavlux.types.Ammo;
import com.coding.sirjavlux.types.Weapon;
import com.coding.sirjavlux.utils.ScopeUtils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_15_R1.NBTTagCompound;

public class WeaponReloadHandler implements Listener {

	private HashMap<UUID, Reload> reloads;
	final int runnableSpeed = 2;
	
	public WeaponReloadHandler() {
		startReloadReader();
		this.reloads = new HashMap<>();
	}
	
	private void startReloadReader() {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Entry<UUID, Reload> entry : reloads.entrySet()) {
					UUID uuid = entry.getKey();
					Reload reload = entry.getValue();
					int time = reload.getTime();
					WeaponItem weaponItem = reload.getWeaponItem();
					boolean remove = true;
					time -= runnableSpeed;
					ItemStack item = reload.getItemStack();
					//check if reload is valid
					if (Bukkit.getOfflinePlayer(uuid).isOnline()) {
						Player p = Bukkit.getPlayer(uuid);
						PlayerInventory iv = p.getInventory();
						ItemStack heldItem = iv.getItemInMainHand();
						if (WeaponManager.isWeapon(heldItem)) {
							net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
							NBTTagCompound tagComp = NMSItem.getTag();
							UUID uuid1 = UUID.fromString(tagComp.getString("uuid"));
							net.minecraft.server.v1_15_R1.ItemStack NMSItemH = CraftItemStack.asNMSCopy(heldItem);
							NBTTagCompound tagCompH = NMSItemH.getTag();
							UUID uuid2 = UUID.fromString(tagCompH.getString("uuid"));
							if (uuid1.equals(uuid2)) {
								//reload weapon
								switch (reload.getType()) {
								case Magazine:
									//check if still has valid magazine in iv
									int slot = 0;
									int count = 0;
									MagazineItem magItem = null;
									for (ItemStack i : p.getInventory().getContents()) {
										if (WeaponManager.isMagazine(i)) {
											net.minecraft.server.v1_15_R1.ItemStack NMSItemL = CraftItemStack.asNMSCopy(i);
											NBTTagCompound tagCompL = NMSItemL.getTag();
											String roundStr = tagCompL.getString("ammo");
											int ammo = roundStr.isEmpty() ? 0 : roundStr.split(",").length;
											UUID uuidL = UUID.fromString(tagCompL.getString("uuid"));
											MagazineItem magItemL = WeaponManager.getMagazineItem(uuidL);
											if (magItemL.getMagazine().getCaliber().equals(weaponItem.getWeapon().getCaliber()) && magItemL.getRounds().size() > 0) {
												MagazineItem oldMagItem = weaponItem.getMagazineItem();
												if (oldMagItem != null) {
													if (oldMagItem.getRounds().size() >= ammo) {
														count++;
														continue;
													}
												}
												if (magItem == null) {
													magItem = magItemL;
													slot = count;
												}
												else if (ammo > magItem.getRounds().size()) {
													magItem = magItemL;
													slot = count;
												}
											}
										}
										count++;
									}
									
									//reload magazine
									if (magItem != null) {
										if (time <= 0) {
											MagazineItem oldMagItem = weaponItem.getMagazineItem();
											if (oldMagItem == null) iv.setItem(slot, new ItemStack(Material.AIR));
											else {
												ItemStack oldItem = WeaponManager.generateMagazine(oldMagItem.getMagazine());
												net.minecraft.server.v1_15_R1.ItemStack NMSItemC = CraftItemStack.asNMSCopy(oldItem);
												NBTTagCompound tagCompC = NMSItemC.getTag();
												UUID uuidMag = UUID.fromString(tagCompC.getString("uuid"));
												MagazineItem newOldMagItem = WeaponManager.getMagazineItem(uuidMag);
												newOldMagItem.setRounds(oldMagItem.getRounds());
												weaponItem.setMagazineItem(magItem);
												newOldMagItem.update(oldItem);
												WeaponManager.saveMagazineData(oldItem);
												iv.setItem(slot, new ItemStack(oldItem));
											}
											weaponItem.setMagazineItem(magItem);
											weaponItem.hardUpdate(heldItem);
											weaponItem.updateNextAmmo();
										} else {
											remove = false;
										}
									}									
									break;
								case Single:
									//check if inventory contains valid ammo
									Ammo ammo = null;
									ItemStack ammoItemStack = null;
									slot = 0;
									if (weaponItem.getBarrelAmmo().size() < weaponItem.getWeapon().getBarrelAmmoCap()) {
										for (ItemStack i : p.getInventory().getContents()) {
											if (WeaponManager.isAmmunition(i)) {
												net.minecraft.server.v1_15_R1.ItemStack NMSItemL = CraftItemStack.asNMSCopy(i);
												NBTTagCompound tagCompL = NMSItemL.getTag();
												String name = tagCompL.getString("name");
												Ammo ammoTemp = WeaponManager.getStoredAmmo(name);
												ammoItemStack = i;
												if (ammoTemp.getCaliber().equals(weaponItem.getWeapon().getCaliber())) {
													ammo = ammoTemp;
													break;
												}
											}
											slot++;
										}	
									}
									
									//reload 1 bullet
									if (ammo != null && time <= 0) {
										weaponItem.getBarrelAmmo().add(ammo);
										weaponItem.updateNextAmmo();
										weaponItem.hardUpdate(heldItem);
										int ammoItemAmount = ammoItemStack.getAmount();
										if (ammoItemAmount > 1) ammoItemStack.setAmount(ammoItemAmount - 1);
										else ammoItemStack = new ItemStack(Material.AIR);
										iv.setItem(slot, ammoItemStack);
										if (weaponItem.getBarrelAmmo().size() < weaponItem.getWeapon().getBarrelAmmoCap()) {
											time = weaponItem.getWeapon().getReloadSpeed();
											remove = false;
										}
									} else if (ammo != null) remove = false;
									break;
								}
							}
						}
					}
					//remove from reload
					if (remove) {
						reloads.remove(uuid);
						if (time <= 0 && Bukkit.getOfflinePlayer(uuid).isOnline()) {
							Player p = Bukkit.getPlayer(uuid);
							p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GRAY + "" + ChatColor.BOLD + "Reload Complete"));
						}
						continue;
					} 
					//display reload in actionbar and unscope if scoped
					else if (Bukkit.getOfflinePlayer(uuid).isOnline()) {
						Player p = Bukkit.getPlayer(uuid);
						ScopeUtils.unscope(p);
						DecimalFormat format = new DecimalFormat("###########0.0");
						if (time > 0) {
							p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GRAY + "" + ChatColor.BOLD + "Reloading... time remaining " + ChatColor.YELLOW + ChatColor.BOLD + format.format((double) (time / (20d / runnableSpeed)) - runnableSpeed / (20d / runnableSpeed))));
						}
						reload.setTime(time);
					}
				}
			}
		}.runTaskTimer(Main.getPlugin(Main.class), runnableSpeed, runnableSpeed);
	}
	
	public boolean isReloading(Player p) {
		return reloads.containsKey(p.getUniqueId()) ? true : false;
	}
	
	@EventHandler
	public void playerDropItem(PlayerDropItemEvent e) {
		Item drop = e.getItemDrop();
		ItemStack item = drop.getItemStack();
		if (WeaponManager.isWeapon(item)) {
			net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
			NBTTagCompound tagComp = NMSItem.getTag();
			UUID uuid = UUID.fromString(tagComp.getString("uuid"));
			Player p = e.getPlayer();
			WeaponItem weaponItem = WeaponManager.getWeaponItem(uuid);
			Weapon weapon = weaponItem.getWeapon();
			//if weapon uses magazines
			if (weapon.requiresMagazine()) {
				Reload reload = new Reload(weaponItem, item, ReloadType.Magazine);
				reloads.put(p.getUniqueId(), reload);
			}
			//if not using magazines
			else {
				Reload reload = new Reload(weaponItem, item, ReloadType.Single);
				reloads.put(p.getUniqueId(), reload);
			}
			e.setCancelled(true);
			return;
		}
	}
	
	private class Reload {
		
		private ReloadType type;
		private WeaponItem weaponItem;
		private ItemStack item;
		private int time;
		
		public Reload(WeaponItem weaponItem, ItemStack item, ReloadType type) {
			this.type = type;
			this.weaponItem = weaponItem;
			this.item = item;
			this.time = weaponItem.getWeapon().getReloadSpeed();
		}
		
		public ReloadType getType() { return type; }
		public WeaponItem getWeaponItem() { return weaponItem; }
		public ItemStack getItemStack() { return item; }
		public int getTime() { return time; }
		public void setTime(int time) { this.time = time; } 
	} 
	
	private enum ReloadType {
		Single,
		Magazine
	}
}
