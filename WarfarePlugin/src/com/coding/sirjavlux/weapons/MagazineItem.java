package com.coding.sirjavlux.weapons;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.coding.sirjavlux.types.Ammo;
import com.coding.sirjavlux.types.Magazine;

public class MagazineItem {
	
	private final Magazine mag;
	private final UUID uuid;
	private List<Ammo> rounds;
	private Ammo nextAmmo;
	
	public MagazineItem(Magazine mag, UUID uuid) {
		this.mag = mag;
		this.uuid = uuid;
		rounds = new ArrayList<>();
		updateNextAmmo();
	}
	
	public void addRounds(Ammo ammo, int amount) {
		int ammoCap = mag.getAmmoCapasity();
		int startSize = rounds.size();
		for (int i = 0; i < amount; i++) {
			if (startSize + i + 1 > ammoCap) {
				break;
			} else {
				rounds.add(ammo);
			}
		}
		updateNextAmmo();
	}
	
	public Ammo removeBullet() {
		Ammo lastAmmo = null;
		if (rounds.size() > 0) {
			lastAmmo = rounds.get(rounds.size() - 1);
			rounds.remove(rounds.size() - 1);
			updateNextAmmo();
		}
		return lastAmmo;
	}
	
	public void update(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		
		int ammo = rounds.size();
		int maxAmmo = mag.getAmmoCapasity();
		
		//displayName
		String displayName = mag.getDisplayName();
		displayName = ChatColor.translateAlternateColorCodes('&', displayName.replaceAll("%ammo%", String.valueOf(ammo))
				.replaceAll("%max-ammo%", String.valueOf(maxAmmo)));
		meta.setDisplayName(displayName);
		
		//lore
		String[] lore = mag.getLore();
		List<String> loreList = new ArrayList<>();
		for (int i = 0; i < lore.length; i++) {
			loreList.add(ChatColor.translateAlternateColorCodes('&', lore[i].replaceAll("%ammo%", String.valueOf(ammo))
					.replaceAll("%max-ammo%", String.valueOf(maxAmmo))));
		}
		meta.setLore(loreList);
		
		//set custom texture
		meta.setCustomModelData(mag.getCustomModelData());
		meta.setUnbreakable(true);
		
		item.setItemMeta(meta);
		item.setType(mag.getMaterial());
		updateNextAmmo();
	}
	
	private void updateNextAmmo() {
		nextAmmo = rounds.size() > 0 ? rounds.get(rounds.size() - 1) : null;
	}
	
	public Magazine getMagazine() { return mag; }
	public UUID getUniqueId() { return uuid; }
	public List<Ammo> getRounds() { return rounds; }
	public Ammo getNextAmmo() { return nextAmmo; }
	public void setRounds(List<Ammo> rounds) { this.rounds = rounds == null ? new ArrayList<Ammo>() : rounds; }
}
