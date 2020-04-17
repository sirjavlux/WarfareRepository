package com.coding.sirjavlux.core;

import org.bukkit.Location;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.Husk;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pillager;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Stray;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Vindicator;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

import com.coding.sirjavlux.projectiles.BodyPart;

public class DamageListener implements Listener {
	
	@EventHandler
	public void projectileHitEvent(ProjectileHitEvent e) {
		if (e.getEntity().getShooter() instanceof Player && e.getHitEntity() != null) {
			Projectile ptile = e.getEntity();
			Player shooter = (Player) ptile.getShooter();
			if (ptile.getCustomName() != null) {
				String[] data = ptile.getCustomName().split(",");
				if (data[0].equalsIgnoreCase("projectile")) {
					Vector pDir = ptile.getLocation().getDirection();
					LivingEntity damaged = (LivingEntity) e.getHitEntity();
					double damage = Double.parseDouble(data[1]);
					
					//get other body part then chest if zombie, player or skeleton
					BodyPart part = isBodyPartRegistrable(damaged) ? getHitBodyPart(ptile.getLocation(), damaged.getLocation(), pDir) : BodyPart.Chest;
					
					System.out.println("damage " + damage * Double.parseDouble(part.toString()));
					damaged.damage((damage * Double.parseDouble(part.toString())), shooter);
				}
			}
		}
	}
	
	private boolean isBodyPartRegistrable(LivingEntity entity) {
		if (entity instanceof Player 
				|| entity instanceof Skeleton 
				|| entity instanceof Zombie
				|| entity instanceof Pillager
				|| entity instanceof Villager
				|| entity instanceof Vindicator
				|| entity instanceof WanderingTrader
				|| entity instanceof Evoker
				|| entity instanceof Husk
				|| entity instanceof ZombieVillager
				|| entity instanceof Witch
				|| entity instanceof Stray
				|| entity instanceof Drowned) {
			return true;
		} else {
			return false;
		}
	}
	
	///////////////////////////////////
	//body part registration 0.1
	///////////////////////////////////
	private BodyPart getHitBodyPart(Location hitLoc, Location pLoc, Vector oldDir) {
		BodyPart hitBodyPart = BodyPart.Chest;
		
		double oldDirY = oldDir.getY();
		Location l1 = pLoc.clone();
		Location l2 = hitLoc.clone();
		l1.setY(0);
		l2.setY(0);

		//create projectile direction
		Vector pDir = l1.toVector().subtract(l2.toVector());
		pDir.setX(pDir.getX() / (l1.distance(l2) + l1.distance(l2)));
		pDir.setZ(pDir.getZ() / (l1.distance(l2) + l1.distance(l2)));
		double distance = l1.distance(l2) + l1.distance(l2) * ((1 - pDir.lengthSquared()) > 0 ? (1 - pDir.lengthSquared()) : (1 - pDir.lengthSquared()) * -1);
		pDir.setY(oldDirY);
		
		hitLoc = hitLoc.toVector().subtract(pDir.multiply(distance)).toLocation(pLoc.getWorld());
		l2 = hitLoc.clone();
		l2.setY(0);
		
		//above waist
		if (hitLoc.getY() - pLoc.getY() > 0.8) {
			//check if headshot
			if (hitLoc.getY() - pLoc.getY() > 1.5) {
				System.out.println("headshot");
				hitBodyPart = BodyPart.Head;
			} 
			//else body shot
			else {
				System.out.println("bodyshot");
				hitBodyPart = BodyPart.Chest;
			}
		}
		//under waist legs
		else {
			hitBodyPart = BodyPart.Leg;
			System.out.println("legshot");
		}
		
		return hitBodyPart;
	}
}
