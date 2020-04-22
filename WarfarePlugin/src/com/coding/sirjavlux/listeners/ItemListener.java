package com.coding.sirjavlux.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.coding.sirjavlux.types.Ammo;
import com.coding.sirjavlux.weapons.MagazineItem;
import com.coding.sirjavlux.weapons.WeaponItem;
import com.coding.sirjavlux.weapons.WeaponManager;

import net.minecraft.server.v1_15_R1.NBTTagCompound;

public class ItemListener implements Listener {

	@EventHandler
	public void onItemDespawnEvent(ItemDespawnEvent e) {
		 
	}
	
	@EventHandler
	public void playerDropItem(PlayerDropItemEvent e) {
		Item drop = e.getItemDrop();
		ItemStack item = drop.getItemStack();
		if (WeaponManager.isWeapon(item)) {
			net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
			NBTTagCompound tagComp = NMSItem.getTag();
			UUID uuid = UUID.fromString(tagComp.getString("uuid"));
			WeaponItem weaponItem = WeaponManager.getWeaponItem(uuid);
			if (weaponItem != null) {
				weaponItem.hardUpdate(item);
				drop.setItemStack(WeaponManager.saveWeaponData(item));	
			}
		}
	}
	
	@EventHandler
	public void inventoryCreativeClickEvent(InventoryCreativeEvent e) {
		ItemStack cursor = e.getCursor();
		Inventory iv = e.getClickedInventory();
		int slot = e.getSlot();
		ItemStack[] contents = iv.getContents();
		
		if (iv != null) {
			//check middle click copy item
			if (cursor != null) {
				if (WeaponManager.isWeapon(cursor) || WeaponManager.isMagazine(cursor)) {
					ItemStack finalItem = cursor.clone();
					net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(finalItem);
					NBTTagCompound tagComp = NMSItem.getTag();
					UUID uuid = UUID.fromString(tagComp.getString("uuid"));
					for (int i = 0; i < contents.length; i++) {
						ItemStack cItem = contents[i];
						if (i != slot && cItem != null) {
							if (WeaponManager.isWeapon(cItem) || WeaponManager.isMagazine(cItem)) {
								net.minecraft.server.v1_15_R1.ItemStack NMSItemC = CraftItemStack.asNMSCopy(cItem);
								NBTTagCompound tagCompC = NMSItemC.getTag();
								UUID cUUID = UUID.fromString(tagCompC.getString("uuid"));
								if (cUUID.equals(uuid)) {
									uuid = WeaponManager.generateRandomSafeUUID();
									tagComp.setString("uuid", uuid.toString());
									NMSItem.setTag(tagComp);
									finalItem = CraftItemStack.asBukkitCopy(NMSItem);
									
									//load and set weapon
									if (WeaponManager.isWeapon(cursor)) {
										WeaponManager.loadWeaponData(finalItem);
									} else if (WeaponManager.isMagazine(cursor)) {
										WeaponManager.loadMagazineData(finalItem);
									}
									
									e.setCurrentItem(finalItem);
									e.setCancelled(true);
									return;
								}
							}
						}
					}	
				}
			}
		}
	}
	
	@EventHandler
	public void inventoryClickEvent(InventoryClickEvent e) {
		ItemStack cursor = e.getCursor();
		ItemStack item = e.getCurrentItem();
		Inventory iv = e.getClickedInventory();
		ClickType click = e.getClick();
		HumanEntity p = e.getWhoClicked();
		
		/*///////////////////////////////
		 * UNLOAD AND LOAD MAGAZINE
		 *///////////////////////////////
		
		//if weapon
		if (iv != null && WeaponManager.isWeapon(item)) {
			net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
			NBTTagCompound tagComp = NMSItem.getTag();
			UUID uuid = UUID.fromString(tagComp.getString("uuid"));
			WeaponItem weaponItem = WeaponManager.getWeaponItem(uuid);
			if (weaponItem != null) {
				//load
				boolean cancel = false;
				if (WeaponManager.isMagazine(cursor) || WeaponManager.isAmmunition(cursor)) {
					//if mag and is loadable with mag
					if (WeaponManager.isMagazine(cursor) && weaponItem.getMagazineItem() == null) {
						net.minecraft.server.v1_15_R1.ItemStack NMSItemC = CraftItemStack.asNMSCopy(cursor);
						NBTTagCompound tagCompC = NMSItemC.getTag();
						UUID uuidMag = UUID.fromString(tagCompC.getString("uuid"));
						MagazineItem magItem = WeaponManager.getMagazineItem(uuidMag);
						weaponItem.setMagazineItem(magItem);
						weaponItem.hardUpdate(item);
						weaponItem.updateNextAmmo();
						p.setItemOnCursor(new ItemStack(Material.AIR));
						cancel = true;
					} 
					//if bullets 
					else if (WeaponManager.isAmmunition(cursor) && weaponItem.getBarrelAmmo().size() < weaponItem.getWeapon().getBarrelAmmoCap()) {
						net.minecraft.server.v1_15_R1.ItemStack NMSItemC = CraftItemStack.asNMSCopy(cursor);
						NBTTagCompound tagCompC = NMSItemC.getTag();
						String name = tagCompC.getString("name");
						int itemAmount = cursor.getAmount();
						int amountEmpty = weaponItem.getWeapon().getBarrelAmmoCap() - weaponItem.getBarrelAmmo().size();
						Ammo ammo = WeaponManager.getStoredAmmo(name);
						if (ammo != null) {
							List<Ammo> rounds = new ArrayList<>();
							for (int i = 0; i < amountEmpty; i++) {
								if (itemAmount > 0) {
									rounds.add(ammo);
									itemAmount--;
								} else {
									break;
								} 
							}
							rounds.addAll(weaponItem.getBarrelAmmo());
							weaponItem.setBarrelAmmo(rounds);
							if (itemAmount > 0) {
								ItemStack ammoItem = cursor.clone();
								ammoItem.setAmount(itemAmount);
								p.setItemOnCursor(ammoItem);
							}
							else p.setItemOnCursor(new ItemStack(Material.AIR));
							weaponItem.hardUpdate(item);
							weaponItem.updateNextAmmo();
							cancel = true;	
						}
					}
				}
				//unload 
				else if (click.equals(ClickType.RIGHT)) {
					//if has mag
					if (weaponItem.getMagazineItem() != null) {
						ItemStack mag = WeaponManager.generateMagazine(weaponItem.getMagazineItem().getMagazine());
						net.minecraft.server.v1_15_R1.ItemStack NMSItemMag = CraftItemStack.asNMSCopy(mag);
						NBTTagCompound tagCompMag = NMSItemMag.getTag();
						UUID uuidMag = UUID.fromString(tagCompMag.getString("uuid"));
						MagazineItem magItem = WeaponManager.getMagazineItem(uuidMag);
						magItem.setRounds(weaponItem.getMagazineItem().getRounds());
						magItem.update(mag);
						weaponItem.setMagazineItem(null);
						p.setItemOnCursor(mag);
						weaponItem.hardUpdate(item);
						weaponItem.updateNextAmmo();
						WeaponManager.saveWeaponData(item);
						cancel = true;
					}
					//if no mag
					else {
						List<Ammo> rounds = weaponItem.getBarrelAmmo();
						Ammo ammo = null;
						int amount = 0;
						for (Ammo round : new ArrayList<Ammo>(rounds)) {
							if (amount == 0) ammo = round;
							if (round.getName() != ammo.getName()) break;
							else if (amount >= ammo.getMaxStackSize()) break;
							else {
								rounds.remove(0);
								amount++;
							}
						}
						if (ammo != null) {
							weaponItem.setBarrelAmmo(rounds);
							p.setItemOnCursor(WeaponManager.generateAmmo(ammo, amount));
							weaponItem.hardUpdate(item);
							weaponItem.updateNextAmmo();
							WeaponManager.saveWeaponData(item);
							cancel = true;	
						}
					}
				}
				if (cancel) {
					e.setCancelled(true);
					return;
				}
			}
		}
		//if magazine
		else if (WeaponManager.isMagazine(item)) {
			//load 
			if (WeaponManager.isAmmunition(cursor)) {
				net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
				NBTTagCompound tagComp = NMSItem.getTag();
				UUID uuid = UUID.fromString(tagComp.getString("uuid"));
				MagazineItem magItem = WeaponManager.getMagazineItem(uuid);
				net.minecraft.server.v1_15_R1.ItemStack NMSItemC = CraftItemStack.asNMSCopy(cursor);
				NBTTagCompound tagCompC = NMSItemC.getTag();
				Ammo ammoC = WeaponManager.getStoredAmmo(tagCompC.getString("name"));
				if (magItem != null && ammoC != null) {
					if (ammoC.getCaliber().equalsIgnoreCase(magItem.getMagazine().getCaliber())) {
						List<Ammo> rounds = new ArrayList<>(magItem.getRounds());
						int roundsEmpty = magItem.getMagazine().getAmmoCapasity() - rounds.size();
						int amount = cursor.getAmount();
						for (int i = 0; i < roundsEmpty; i++) {
							if (amount > 0) {
								rounds.add(ammoC);
								amount--;
							} else {
								break;
							}
						}
						magItem.setRounds(rounds);
						magItem.update(item);
						if (amount > 0) {
							ItemStack cItem = cursor.clone();
							cItem.setAmount(amount);
							p.setItemOnCursor(cItem);
						} else {
							p.setItemOnCursor(new ItemStack(Material.AIR));
						}
						e.setCancelled(true);
						return;
					}
				}
			}
			//unload
			else if (click.equals(ClickType.RIGHT)) {
				net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
				NBTTagCompound tagComp = NMSItem.getTag();
				UUID uuid = UUID.fromString(tagComp.getString("uuid"));
				MagazineItem magItem = WeaponManager.getMagazineItem(uuid);
				List<Ammo> rounds = magItem.getRounds();
				Ammo ammo = null;
				int amount = 0;
				for (Ammo round : new ArrayList<Ammo>(rounds)) {
					if (amount == 0) ammo = round;
					if (round.getName() != ammo.getName()) break;
					else if (amount >= ammo.getMaxStackSize()) break;
					else {
						rounds.remove(0);
						amount++;
					}
				}
				if (ammo != null) {
					magItem.setRounds(rounds);
					magItem.update(item);
					p.setItemOnCursor(WeaponManager.generateAmmo(ammo, amount));
					WeaponManager.saveWeaponData(item);
					e.setCancelled(true);
					return;
				}
			}
		}
		//if ammo
		else if (WeaponManager.isAmmunition(item) && WeaponManager.isAmmunition(cursor) && (click.equals(ClickType.RIGHT) || click.equals(ClickType.LEFT))) {
			net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
			NBTTagCompound tagComp = NMSItem.getTag();
			Ammo ammo = WeaponManager.getStoredAmmo(tagComp.getString("name"));
			net.minecraft.server.v1_15_R1.ItemStack NMSItemC = CraftItemStack.asNMSCopy(cursor);
			NBTTagCompound tagCompC = NMSItemC.getTag();
			Ammo ammoC = WeaponManager.getStoredAmmo(tagCompC.getString("name"));
			if (ammo != null && ammoC != null) {
				if (ammoC.getName().equals(ammo.getName())) {
					int amountEmpty = ammo.getMaxStackSize() - item.getAmount();
					int cAmount = cursor.getAmount();
					if (amountEmpty > 0 && cAmount > 0) {
						int amountToAdd = click.equals(ClickType.RIGHT) ? 1 : (cAmount > amountEmpty ? amountEmpty : cAmount);
						item.setAmount(item.getAmount() + amountToAdd);
						if (amountToAdd >= cAmount) {
							p.setItemOnCursor(new ItemStack(Material.AIR));
						} else {
							ItemStack cItem = cursor.clone();
							cItem.setAmount(cItem.getAmount() - amountToAdd);
							p.setItemOnCursor(cItem);
						}
						e.setCancelled(true);
						return;	
					}
				}
			}
		}
	}
}
