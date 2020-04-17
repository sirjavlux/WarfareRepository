package com.coding.sirjavlux.projectiles;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.coding.sirjavlux.types.Ammo;
import com.coding.sirjavlux.types.AmmoType;
import com.coding.sirjavlux.types.Weapon;

import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.World;

public class ProjectileManager {

	public static void fireProjectile(Player p, double speed, Weapon weapon, Ammo ammo, AmmoType type) {
		EntityPlayer eP = ((CraftPlayer) p).getHandle();
		World w = eP.getWorld();
		ItemStack item = new ItemStack(Material.STONE);
		
		Projectile ptile = new Projectile(w, eP, CraftItemStack.asNMSCopy(item), speed, weapon, ammo);
		CustomEntitySnowballRegistry.spawnEntity(ptile, w);
	}
	
	
}
