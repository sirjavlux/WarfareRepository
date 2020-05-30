package com.coding.sirjavlux.listeners;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import com.coding.sirjavlux.core.Main;
import com.coding.sirjavlux.types.Weapon;
import com.coding.sirjavlux.weapons.WeaponItem;
import com.coding.sirjavlux.weapons.WeaponManager;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_15_R1.NBTTagCompound;

public class EquipListener implements Listener {
	
	public EquipListener() {
		startEquipRunnable();
	}
	
	@EventHandler
	public void onJoinEvent(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		PlayerInventory iv = p.getInventory();
		ItemStack item = iv.getItemInMainHand();
		//if weapon
		if (WeaponManager.isWeapon(item)) {
    		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
    		NBTTagCompound tagComp = NMSItem.getTag();
			UUID wUUID = UUID.fromString(tagComp.getString("uuid"));
			WeaponItem weaponItem = WeaponManager.getWeaponItem(wUUID);
			if (weaponItem == null) WeaponManager.loadWeaponData(item);
			weaponItem.resetEquipTime();
			equipingWeapons.put(p.getUniqueId(), wUUID);
		}
	}
	
	@EventHandler
	public void clickEvent(InventoryClickEvent e) {
		ItemStack cursor = e.getCursor();
		ItemStack current = e.getCurrentItem();
		Inventory iv = e.getClickedInventory();
		Player p = (Player) e.getWhoClicked();
		if (iv != null) {
			if (e.getSlot() == p.getInventory().getHeldItemSlot()) {
				if (WeaponManager.isWeapon(current)) {
					net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(current);
		    		NBTTagCompound tagComp = NMSItem.getTag();
					UUID wUUID = UUID.fromString(tagComp.getString("uuid"));
					WeaponItem weaponItem = WeaponManager.getWeaponItem(wUUID);
					if (weaponItem == null) WeaponManager.loadWeaponData(current);
					weaponItem.resetEquipTime();
					equipingWeapons.remove(p.getUniqueId());
				}
				if (WeaponManager.isWeapon(cursor)) {
		    		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(cursor);
		    		NBTTagCompound tagComp = NMSItem.getTag();
					UUID wUUID = UUID.fromString(tagComp.getString("uuid"));
					WeaponItem weaponItem = WeaponManager.getWeaponItem(wUUID);
					if (weaponItem == null) WeaponManager.loadWeaponData(cursor);
					weaponItem.resetEquipTime();
					equipingWeapons.put(p.getUniqueId(), wUUID);
				} 
			}
		}
	}
	
	@EventHandler
	public void hotbarSwapEvent(PlayerItemHeldEvent e) {
		Player p = e.getPlayer();
		PlayerInventory iv = p.getInventory();
		int newSlot = e.getNewSlot();
		int oldSlot = e.getPreviousSlot();
		ItemStack item = iv.getItem(newSlot);
		ItemStack oldItem = iv.getItem(oldSlot);
		//if previous is weapon
		if (WeaponManager.isWeapon(oldItem)) {
			net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(oldItem);
    		NBTTagCompound tagComp = NMSItem.getTag();
			UUID wUUID = UUID.fromString(tagComp.getString("uuid"));
			WeaponItem weaponItem = WeaponManager.getWeaponItem(wUUID);
			if (weaponItem == null) WeaponManager.loadWeaponData(oldItem);
			weaponItem.resetEquipTime();
			equipingWeapons.remove(p.getUniqueId());
		}
		//if weapon
		if (WeaponManager.isWeapon(item)) {
    		net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
    		NBTTagCompound tagComp = NMSItem.getTag();
			UUID wUUID = UUID.fromString(tagComp.getString("uuid"));
			WeaponItem weaponItem = WeaponManager.getWeaponItem(wUUID);
			if (weaponItem == null) WeaponManager.loadWeaponData(item);
			weaponItem.resetEquipTime();
			equipingWeapons.put(p.getUniqueId(), wUUID);
		}
	}
	
	private HashMap<UUID, UUID> equipingWeapons = new HashMap<>();
	
	private void startEquipRunnable() {
		final int runnableSpeed = 2;
		DecimalFormat format = new DecimalFormat("###########0.0");
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Entry<UUID, UUID> entry : new HashMap<>(equipingWeapons).entrySet()) {
					UUID uuid = entry.getKey();
					UUID wUUID = entry.getValue();
					WeaponItem weaponItem = WeaponManager.getWeaponItem(wUUID);
					Weapon weapon = weaponItem.getWeapon();
					if (!Bukkit.getOfflinePlayer(uuid).isOnline()) {
						equipingWeapons.remove(uuid);
					} else if (weaponItem.isEquiping()) {
						Player p = Bukkit.getPlayer(uuid);
						int time = weaponItem.getEquipTime();
						int finalTime = time - runnableSpeed < 0 ? 0 : time - runnableSpeed;
						if (equipingWeapons.containsKey(uuid)) weaponItem.setEquipTime(finalTime);
						double showTime = (double) finalTime / 20d;
						p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&7&lEquping " + weapon.getDisplayName() + "&7&l... Time remaining &e&l" + format.format(showTime))));
					} else {
						Player p = Bukkit.getPlayer(uuid);
						p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&7&lSuccessfully equiped " + weapon.getDisplayName() + "&7&l!")));
						equipingWeapons.remove(uuid);
					}
				}
			}
		}.runTaskTimerAsynchronously(Main.getPlugin(Main.class), runnableSpeed, runnableSpeed);
	}
}
