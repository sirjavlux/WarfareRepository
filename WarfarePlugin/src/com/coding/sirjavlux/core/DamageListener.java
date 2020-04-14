package com.coding.sirjavlux.core;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import com.coding.sirjavlux.projectiles.BodyPart;

public class DamageListener implements Listener {
	
	@EventHandler
	public void playerDamagePlayerEvent(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Projectile) {
			Projectile ptile = (Projectile) e.getDamager();
			if (ptile.getCustomName() != null) {
				String[] data = ptile.getCustomName().split(",");
				if (data[0].equalsIgnoreCase("projectile")) {
					Vector pDir = ptile.getLocation().getDirection();
					LivingEntity damaged = (LivingEntity) e.getEntity();
					double damage = Double.parseDouble(data[1]);
					
					BodyPart part = damaged instanceof Player ? getHitBodyPart(ptile.getLocation(), damaged.getLocation(), pDir) : BodyPart.Chest;
					
					getHitBodyPart(ptile.getLocation(), damaged.getLocation(), pDir); //temporary
					
					System.out.println("damage " + damage * Double.parseDouble(part.toString()));
					e.setDamage(damage * Double.parseDouble(part.toString()));
				}
			}
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
