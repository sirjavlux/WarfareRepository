package com.coding.sirjavlux.weapons;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.coding.sirjavlux.types.Ammo;
import com.coding.sirjavlux.types.Weapon;
import com.coding.sirjavlux.types.WeaponType;
import com.coding.sirjavlux.utils.ScopeUtils;

public class WeaponItem {
	
	private final Weapon weapon;
	private final UUID uuid;
	private MagazineItem mag;
	private List<Ammo> barrelAmmo;
	private Ammo nextAmmo;
	private int burstBulletsLeft;
	
	public WeaponItem(Weapon weapon, UUID uuid) {
		
		this.weapon = weapon;
		this.uuid = uuid;
		this.mag = null;
		this.barrelAmmo = new ArrayList<>();
		this.nextAmmo = null;
		this.burstBulletsLeft = weapon.getBurstAmount();
		
		//pre load if is loaded by default
		if (weapon.isLoadedByDefault()) {
			Ammo preLoadAmmo = weapon.getPreLoadAmmo();
			//barrel rounds
			for (int i = 0; i < weapon.getBarrelAmmoCap(); i++) {
				barrelAmmo.add(preLoadAmmo);
			}
			//mag rounds
			if (mag != null) {
				mag.addRounds(preLoadAmmo, mag.getMagazine().getAmmoCapasity());
			}
		}
		updateNextAmmo();
	}
	
	public boolean removeBullet() {
		//remove front bullet
		barrelAmmo.remove(0);
		//add from magazine to barrel if has mag and remove from mag
		if (weapon.requiresMagazine()) {
			if (mag != null) {
				Ammo swapAmmo = mag.removeBullet();
				if (swapAmmo != null) barrelAmmo.add(swapAmmo);
			}
		}
		//check if burst is empty
		boolean burstEmpty = false;
		if (weapon.getType().equals(WeaponType.Burst)) {
			burstBulletsLeft--;
			if (burstBulletsLeft < 1 || barrelAmmo.size() < 1) {
				burstBulletsLeft = weapon.getBurstAmount();
				burstEmpty = true;
			}
		}
		updateNextAmmo();
		return barrelAmmo.size() < 1 || burstEmpty;
	}
	
	public void updateNextAmmo() {
		//if has mag
		if (mag != null) {
			if (barrelAmmo.size() < weapon.getBarrelAmmoCap() && mag.getRounds().size() > 0) {
				for (int i = 0; i < weapon.getBarrelAmmoCap() - barrelAmmo.size(); i++) {
					List<Ammo> list = new ArrayList<>();
					list.add(mag.getNextAmmo());
					list.addAll(barrelAmmo);
					barrelAmmo = list;
					mag.removeBullet();
				}
			}	
		}
		this.nextAmmo = barrelAmmo.size() > 0 ? barrelAmmo.get(barrelAmmo.size() - 1) : null;
	}
	
	public void update(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		int magAmmo = getBarrelAmmo().size();
		int maxAmmo = weapon.getBarrelAmmoCap();
		if (mag != null) {
			magAmmo = mag.getRounds().size();
			maxAmmo = mag.getMagazine().getAmmoCapasity();
		}
		
		//displayName
		String displayName = weapon.getDisplayName();
		displayName = ChatColor.translateAlternateColorCodes('&', displayName.replaceAll("%ammo%", String.valueOf(magAmmo))
				.replaceAll("%max-ammo%", String.valueOf(maxAmmo)));
		meta.setDisplayName(displayName);
		
		//lore
		String[] lore = weapon.getLore();
		List<String> loreList = new ArrayList<>();
		for (int i = 0; i < lore.length; i++) {
			loreList.add(ChatColor.translateAlternateColorCodes('&', lore[i].replaceAll("%ammo%", String.valueOf(magAmmo))
					.replaceAll("%max-ammo%", String.valueOf(maxAmmo))));
		}
		meta.setLore(loreList);
		
		meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		
		item.setItemMeta(meta);
	}
	
	public void hardUpdate(ItemStack item, Player p) {
		ItemMeta meta = item.getItemMeta();
		int magAmmo = getBarrelAmmo().size();
		int maxAmmo = weapon.getBarrelAmmoCap();
		if (mag != null) {
			magAmmo = mag.getRounds().size();
			maxAmmo = mag.getMagazine().getAmmoCapasity();
		}
		
		//displayName
		String displayName = weapon.getDisplayName();
		displayName = ChatColor.translateAlternateColorCodes('&', displayName.replaceAll("%ammo%", String.valueOf(magAmmo))
				.replaceAll("%max-ammo%", String.valueOf(maxAmmo)));
		meta.setDisplayName(displayName);
		
		//lore
		String[] lore = weapon.getLore();
		List<String> loreList = new ArrayList<>();
		for (int i = 0; i < lore.length; i++) {
			loreList.add(ChatColor.translateAlternateColorCodes('&', lore[i].replaceAll("%ammo%", String.valueOf(magAmmo))
					.replaceAll("%max-ammo%", String.valueOf(maxAmmo))));
		}
		meta.setLore(loreList);
		
		//set custom texture
		meta.setUnbreakable(true);
		if (ScopeUtils.isScoped(p)) {
			item.setType(weapon.getScopeMaterial());
			meta.setCustomModelData(weapon.getScopeModelData());
		} else {
			item.setType(weapon.getMat());
			meta.setCustomModelData(weapon.getCustomModelData());
		}
		
		meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		
		item.setItemMeta(meta);
	}
	
	public void saveData(ItemStack item, Player p, int slot) {
		//save and set item
		item = WeaponManager.saveWeaponData(item);
		p.getInventory().setItem(slot, item);
	}
	
	public Weapon getWeapon() { return weapon; }
	public UUID getUniqueId() { return uuid; }
	public List<Ammo> getBarrelAmmo() { return barrelAmmo; }
	public Ammo getNextAmmo() { return nextAmmo; }
	public int getBurstAmountRemaning() { return burstBulletsLeft; }
	public void resetBurst() { burstBulletsLeft = weapon.getBurstAmount(); }
	public MagazineItem getMagazineItem() { return mag; }
	public void setBarrelAmmo(List<Ammo> barrelAmmo) { this.barrelAmmo = barrelAmmo == null ? new ArrayList<Ammo>() : barrelAmmo; }
	public void setMagazineItem(MagazineItem magItem) { this.mag = magItem; }
}