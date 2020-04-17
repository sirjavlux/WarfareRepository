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
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import com.coding.sirjavlux.projectiles.BodyPart;
import com.coding.sirjavlux.types.Ammo;
import com.coding.sirjavlux.types.Weapon;

public class EntityDamagedByBulletEvent extends Event implements Cancellable {

    private final LivingEntity damaged;
    private final Projectile projectile;
    private final Ammo ammo;
    private final Weapon weapon;
    private boolean isCancelled = false;
    private final ProjectileSource shooter;
    private final ItemStack itemUsed;
    private final BodyPart hitPart;
    private final double damage;
    private final double pen;
    
    public EntityDamagedByBulletEvent(LivingEntity damaged, Projectile projectile) {
        this.damaged = damaged;
        this.projectile = projectile;
		String[] data = projectile.getCustomName().split(",");
		String wName = data[1];
		String bName = data[2];
        this.ammo = WeaponManager.getStoredAmmo(bName);
        this.weapon = WeaponManager.getStoredWeapon(wName);
        this.shooter = projectile.getShooter();
        this.itemUsed = this.shooter instanceof Player ? ((Player) shooter).getInventory().getItemInMainHand() : null;
		this.pen = ammo.getArmorPenetration();
		//get other body part then chest if zombie, player or skeleton
		Vector pDir = projectile.getLocation().getDirection();
		this.hitPart = isBodyPartRegistrable(damaged) ? getHitBodyPart(projectile.getLocation(), damaged.getLocation(), pDir) : BodyPart.Chest;
		this.damage = calculateDamage(damaged, ammo.getDamage() * Double.parseDouble(hitPart.toString()), pen);
    }

    /*///////////////////////////
     * DAMAGE CALCULATOR 0.1
     *///////////////////////////
	private double calculateDamage(LivingEntity entity, double damage, double pen) {		
		return damage;
	}
    
	/*/////////////////////////
	 * GETTERS AND SETTERS
	 */////////////////////////
    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public LivingEntity getEntity() {
        return this.damaged;
    }
    
    public Projectile getProjectile() {
    	return this.projectile;
    }
    
    public Ammo getAmmo() {
        return this.ammo;
    }
    
    public Weapon getWeapon() {
    	return this.weapon;
    }
    
    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }
    
    public ProjectileSource getShooter() {
    	return this.shooter;
    }
    
    public ItemStack getItem() {
    	return this.itemUsed;
    }
    
    public BodyPart getHitBodyPart() {
    	return this.hitPart;
    }
    
    public double damage() {
    	return this.damage;
    }
    
    public double penetration() {
    	return this.pen;
    }
    
	///////////////////////////////////
	//body part registration 0.1
	///////////////////////////////////
	protected static boolean isBodyPartRegistrable(LivingEntity entity) {
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
	
	protected BodyPart getHitBodyPart(Location hitLoc, Location pLoc, Vector oldDir) {
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
				//System.out.println("headshot");
				hitBodyPart = BodyPart.Head;
			} 
			//else body shot
			else {
				//System.out.println("bodyshot");
				hitBodyPart = BodyPart.Chest;
			}
		}
		//under waist legs
		else {
			//System.out.println("legshot");
			hitBodyPart = BodyPart.Leg;
		}
		
		return hitBodyPart;
	}
}
