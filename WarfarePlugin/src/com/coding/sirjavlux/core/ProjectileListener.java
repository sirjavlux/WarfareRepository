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
import org.bukkit.util.Vector;

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
						
						//System.out.println("damage " + event.damage());
						
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
