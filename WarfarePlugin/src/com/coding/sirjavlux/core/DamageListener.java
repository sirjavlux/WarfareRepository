package com.coding.sirjavlux.core;

import org.bukkit.Location;
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
			String[] data = ptile.getCustomName().split(",");
			if (data[0].equalsIgnoreCase("projectile")) {
				Vector pDir = ptile.getLocation().getDirection();
				LivingEntity damaged = (LivingEntity) e.getEntity();
				double damage = Double.parseDouble(data[1]);
				
				BodyPart part = damaged instanceof Player ? getHitBodyPart(ptile.getLocation(), damaged.getLocation(), pDir) : BodyPart.Chest;
				//temporary
				getHitBodyPart(ptile.getLocation(), damaged.getLocation(), pDir);
				
				System.out.println("damage " + damage * Double.parseDouble(part.toString()));
				e.setDamage(damage * Double.parseDouble(part.toString()));
			}
		}
	}
	
	private BodyPart getHitBodyPart(Location hitLoc, Location pLoc, Vector pDir) {
		BodyPart hitBodyPart = BodyPart.Chest;
		
		Location l1 = pLoc.clone();
		Location l2 = hitLoc.clone();
		l1.setY(0);
		l2.setY(0);
		
		//fix distance issue <-----
		System.out.println(l1.distance(l2));
		
		hitLoc = hitLoc.toVector().add(pDir.multiply(l1.distance(l2))).toLocation(pLoc.getWorld());
		l2 = hitLoc.clone();
		l2.setY(0);
		
		System.out.println(l1.distance(l2));
		
		//above waist
		if (hitLoc.getY() - pLoc.getY() > 0.85) {
			//check if headshot
			if (hitLoc.getY() - pLoc.getY() > 1.45) {
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
