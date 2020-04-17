package com.coding.sirjavlux.core;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import com.coding.sirjavlux.events.EntityDamagedByBulletEvent;

import net.minecraft.server.v1_15_R1.Entity;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityStatus;

public class ProjectileListener implements Listener {

	@EventHandler
	public void projectileHitEvent(ProjectileHitEvent e) {
		if (e.getEntity().getShooter() instanceof Player && e.getHitEntity() != null) {
			Projectile ptile = e.getEntity();
			//Player shooter = (Player) ptile.getShooter();
			if (ptile.getCustomName() != null) {
				String[] data = ptile.getCustomName().split(",");
				if (data[0].equalsIgnoreCase("projectile")) {
					//fire event
					EntityDamagedByBulletEvent event = new EntityDamagedByBulletEvent((LivingEntity) e.getHitEntity(), ptile);
					Bukkit.getPluginManager().callEvent(event);
					
					//if not cancelled deal continue event
					if (!event.isCancelled()) {
						LivingEntity entity = event.getEntity();
						double startHealth = entity.getHealth();
						double finalHealth = startHealth - event.damage() < 0 ? 0 : startHealth - event.damage();
						entity.setHealth(finalHealth);
						
						//fire status packet
						Entity ce = ((CraftEntity) entity).getHandle();
						PacketPlayOutEntityStatus statusPacket = new PacketPlayOutEntityStatus(ce, (byte) 2);
						for (Player p : Bukkit.getOnlinePlayers()) {
							if (entity.getLocation().distance(p.getLocation()) < 30) ((CraftPlayer) p).getHandle().playerConnection.sendPacket(statusPacket);
						}
						//deal knock back
						double knockback = event.getAmmo().getKnockBack();
						Location entityLoc = entity.getLocation();
						Location pLoc = ptile.getLocation();
						Vector knockDir = entityLoc.subtract(0, entityLoc.getY(), 0).toVector().subtract(pLoc.subtract(0, pLoc.getY(), 0).toVector());
						knockDir.setY(0.65);
						entity.setVelocity(entity.getVelocity().add(knockDir.multiply(knockback / 3)));
						//destroy armor depending on round armor damage
						ItemStack hitArmor = event.getHitArmorPiece();
						if (hitArmor.getItemMeta() instanceof Damageable) {
							double armorDamage = event.getAmmo().getArmorDamage();
							Damageable damageableItem = ((Damageable) hitArmor.getItemMeta());
							int maxDurability = hitArmor.getType().getMaxDurability();
							int durability = maxDurability - damageableItem.getDamage();
							int finalDurability = (int) (durability - armorDamage < 0 ? 0 : durability - armorDamage);
							damageableItem.setDamage(maxDurability - finalDurability);
							hitArmor.setItemMeta((ItemMeta) damageableItem);
							switch(event.getHitBodyPart()) {
							case Chest: entity.getEquipment().setChestplate(hitArmor);
								break;
							case Head: entity.getEquipment().setHelmet(hitArmor);
								break;
							case Leg: entity.getEquipment().setLeggings(hitArmor);
								break;
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void projectileDamageEvent(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Projectile) {
			Projectile ptile = (Projectile) e.getDamager();
			if (ptile.getCustomName() != null) {
				String[] data = ptile.getCustomName().split(",");
				if (data[0].equalsIgnoreCase("projectile")) {
					e.setCancelled(true);
				}
			}
		}
	}
}
