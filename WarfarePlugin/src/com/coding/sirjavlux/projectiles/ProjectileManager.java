package com.coding.sirjavlux.projectiles;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import com.coding.sirjavlux.core.ConfigManager;
import com.coding.sirjavlux.events.BulletFireEvent;
import com.coding.sirjavlux.grenades.Grenade;
import com.coding.sirjavlux.types.Ammo;
import com.coding.sirjavlux.types.AmmoType;
import com.coding.sirjavlux.types.Weapon;

import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.PacketPlayOutPosition;
import net.minecraft.server.v1_15_R1.PacketPlayOutPosition.EnumPlayerTeleportFlags;
import net.minecraft.server.v1_15_R1.Vec3D;
import net.minecraft.server.v1_15_R1.World;

public class ProjectileManager {

	private static Set<EnumPlayerTeleportFlags> teleportFlags = new HashSet<>(Arrays.asList(EnumPlayerTeleportFlags.X, EnumPlayerTeleportFlags.Y, EnumPlayerTeleportFlags.Z, EnumPlayerTeleportFlags.X_ROT, EnumPlayerTeleportFlags.Y_ROT));
	
	public static void fireProjectile(Player p, Weapon weapon, Ammo ammo) {
		EntityPlayer eP = ((CraftPlayer) p).getHandle();
		World w = eP.getWorld();
		ItemStack item = new ItemStack(ammo.getShootMaterial());
		BulletFireEvent event = new BulletFireEvent(ammo, p);
		Bukkit.getPluginManager().callEvent(event);
		
		if (!event.isCancelled()) {
			//shoot projectile
			if (ammo.getAmmoType().equals(AmmoType.Split)) {
				for (int i = 0; i < ammo.getSplitBulletAmount(); i++) {
					Projectile ptile = new Projectile(w, eP, CraftItemStack.asNMSCopy(item), weapon, event.getAmmo());
					Vec3D dir = ptile.getMot();
					Random r = new Random();
					double randomX = 1 - ammo.getBulletSpread() + (1 + ammo.getBulletSpread() - (1 - ammo.getBulletSpread())) * r.nextDouble();
					double randomY = 1 - ammo.getBulletSpread() * 2 + (1 + ammo.getBulletSpread() * 2 - (1 - ammo.getBulletSpread() * 2)) * r.nextDouble();
					double randomZ = 1 - ammo.getBulletSpread() + (1 + ammo.getBulletSpread() - (1 - ammo.getBulletSpread())) * r.nextDouble();
					ptile.setMot(dir.add((dir.getX() * randomX) - dir.getX(), (dir.getY() * randomY) - dir.getY(), (dir.getZ() * randomZ) - dir.getZ()));
					CustomEntitySnowballRegistry.spawnEntity(ptile, w, ammo);
				}
 			} else {
 				Projectile ptile = new Projectile(w, eP, CraftItemStack.asNMSCopy(item), weapon, event.getAmmo());
 				CustomEntitySnowballRegistry.spawnEntity(ptile, w, ammo);
 			}
			//manage recoil
			if (ConfigManager.recoilEnabled()) {
				float yawModifier = ConfigManager.getRecoilYawModifier();
				Random r = new Random();
				double recoil = ammo.getRecoil();
				double recoilRed = weapon.getRecoilReduction();
				double finalRecoil = recoil - recoilRed < 0 ? 0 : recoil - recoilRed;
				float yawRecMod = (float) (finalRecoil * yawModifier);
				float yawAdd = -yawModifier + r.nextFloat() * (yawRecMod - -yawModifier);
				float pitchAdd = (float) (finalRecoil - yawAdd) * -1f;
				PacketPlayOutPosition packet = new PacketPlayOutPosition(0.0, 0.0, 0.0, yawAdd, pitchAdd, teleportFlags, 0);
			    ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
			}
			//manage knockback
			if (ConfigManager.knockbackEnabled()) {
				double knockback = ammo.getKnockback();
				double knockbackRed = weapon.getKnockbackReduction();
				double velToAdd = knockback - knockbackRed < 0 ? 0 : knockback - knockbackRed;
				Vector vel = p.getEyeLocation().getDirection().normalize().multiply(velToAdd);
				p.setVelocity(p.getVelocity().subtract(vel));
			}
		}
	}
	
	public static void fireGrenadeProjectile(Player p, Grenade grenade) {
		EntityPlayer eP = ((CraftPlayer) p).getHandle();
		World w = eP.getWorld();
		ItemStack item = new ItemStack(grenade.getMaterial());
		ItemMeta meta = item.getItemMeta();
		meta.setCustomModelData(grenade.getModelData());
		item.setItemMeta(meta);
		
		GrenadeProjectile ptile = new GrenadeProjectile(w, eP, CraftItemStack.asNMSCopy(item), grenade);
		CustomEntitySnowballRegistry.spawnEntity(ptile, w);
	}
}
