package com.coding.sirjavlux.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.coding.sirjavlux.types.Weapon;
import com.coding.sirjavlux.weapons.WeaponManager;

import net.minecraft.server.v1_15_R1.NBTTagCompound;

public class ScopeUtils {

	public static List<UUID> scopedPlayers = new ArrayList<>();
	
	public static void scope(Player p, float amount) {
		p.setWalkSpeed((float) (amount * -1));
		scopedPlayers.add(p.getUniqueId());
		ItemStack item = p.getInventory().getItemInMainHand();
		if (WeaponManager.isWeapon(item)) {
			net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(item);
			NBTTagCompound tagComp = NMSItem.getTag();
			Weapon weapon = WeaponManager.getStoredWeapon(tagComp.getString("name"));
			p.playSound(p.getLocation(), weapon.getScopeSound(), 1, 1);
		}
	}
	
	public static void unscope(Player p) {
		if (scopedPlayers.contains(p.getUniqueId())) {
			p.setWalkSpeed(0.2f);
			scopedPlayers.remove(p.getUniqueId());
		}
	}
	
	public static boolean isScoped(Player p) {
		return scopedPlayers.contains(p.getUniqueId());
	}
}
