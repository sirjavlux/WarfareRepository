package com.coding.sirjavlux.events;

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

import com.coding.sirjavlux.armors.Armor;
import com.coding.sirjavlux.armors.ArmorManager;
import com.coding.sirjavlux.core.ConfigManager;
import com.coding.sirjavlux.projectiles.BodyPart;
import com.coding.sirjavlux.types.Ammo;
import com.coding.sirjavlux.types.Weapon;
import com.coding.sirjavlux.weapons.WeaponManager;

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
    private final ItemStack protectingPiece;
    private double armorProt;
    private Armor armor;
    
    public EntityDamagedByBulletEvent(LivingEntity damaged, Projectile projectile, double pen) {
        this.damaged = damaged;
        this.projectile = projectile;
		String[] data = projectile.getCustomName().split(",");
		String wName = data[1];
		String bName = data[2];
        this.ammo = WeaponManager.getStoredAmmo(bName);
        this.weapon = WeaponManager.getStoredWeapon(wName);
        this.shooter = projectile.getShooter();
        this.itemUsed = this.shooter instanceof Player ? ((Player) shooter).getInventory().getItemInMainHand() : null;
		this.pen = pen;
		//get other body part then chest if zombie, player or skeleton
		this.hitPart = isBodyPartRegistrable(damaged) ? getCalculateHitBodyPart() : BodyPart.Chest;
		this.protectingPiece = getHitArmorPice();
		System.out.println("hit area " + hitPart.name());
		if (ArmorManager.isArmor(protectingPiece)) {
			armor = ArmorManager.getArmorFromItem(protectingPiece);
		}
		this.damage = calculateDamage(damaged, ammo.getDamage() * Double.parseDouble(hitPart.toString()), pen);
    }
    
	//get protecting armor pice
    private ItemStack getHitArmorPice() {
		ItemStack piece = null;
		switch (hitPart) {
		//chest
		case Chest: piece = damaged.getEquipment().getChestplate();
			break;
		//head
		case Head: piece = damaged.getEquipment().getHelmet();
			break;
		//leg
		case Leg: piece = damaged.getEquipment().getLeggings();
			break;
		}
		return piece;
    }
    
    /*///////////////////////////
     * DAMAGE CALCULATOR 0.4
     *///////////////////////////
	private double calculateDamage(LivingEntity entity, double damage, double pen) {	
		//calculate damage protection
		if (protectingPiece != null) {
			this.armorProt = ConfigManager.getItemArmorProtection(protectingPiece);
			if (armor != null) {
				armorProt = armor.getProtection(); //calculate protectiopn depending on armor protection if custom armor item
				float durability = ArmorManager.getDurability(protectingPiece);
				float maxDurability = ArmorManager.getMaxDurability(protectingPiece);
				float differance = durability / maxDurability;
				armorProt *= differance;
			}
			armorProt = armorProt - armorProt * pen;
			damage = damage - damage * armorProt;
			double damageRedSpeed = 1 / ((1 - (((projectile.getVelocity().length() > ammo.getSpeed() ? ammo.getSpeed() : projectile.getVelocity().length()) * 100)) / (ammo.getSpeed() * 100)) / (1 / ConfigManager.getDamageReductionSpeed()));
			double damageRedPen = 1 / ((1 - (pen * 100) / (ammo.getArmorPenetration() * 100)) / (1 / ConfigManager.getDamageReductionPenetration()));
			damage = damage - damage / damageRedSpeed; //speed loss damage reduction
			damage = damage - damage / damageRedPen; //penetration loss damage reduction
		}
		return damage ;
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

    public String getHitSound() {
    	return armor != null ? armor.getHitArmorSound() : "";
    }
    
    public Armor getArmor() {
    	return armor;
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
    
    public ItemStack getHitArmorPiece() {
    	return this.protectingPiece;
    }
    
    public double getArmorProtection() {
    	return this.armorProt;
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
	
	protected BodyPart getCalculateHitBodyPart() {
		BodyPart hitBodyPart = BodyPart.Chest;
		
		Location eLoc = damaged.getLocation();
		Location loc = projectile.getLocation().clone();
		Vector dir = projectile.getVelocity().clone().normalize();
		
		int count = 0;
		while (eLoc.clone().subtract(0, eLoc.getY(), 0).distance(loc.clone().subtract(0, loc.getY(), 0)) > 0.2) {
			Location forward = loc.clone().add(dir.clone().multiply(0.03));
			Location backward = loc.clone().subtract(dir.clone().multiply(0.03));
			double dist1 = eLoc.clone().subtract(0, eLoc.getY(), 0).distance(forward.clone().subtract(0, forward.getY(), 0));
			double dist2 = eLoc.clone().subtract(0, eLoc.getY(), 0).distance(backward.clone().subtract(0, backward.getY(), 0));
			if (dist1 < dist2) {
				loc.add(dir.clone().multiply(dist1 > 0.3 ? dist1 * 0.7 : 0.03));
			} else loc.subtract(dir.clone().multiply(dist2 > 0.3 ? dist2 * 0.7 : 0.03));
			if (count > 90) {
				break;
			}
			count++;
		}
		
		double headHeight = 1.5;
		double chestHeight = 0.8;
		
		if (damaged instanceof Player) {
			Player target = (Player) damaged;
			if (target.isSneaking()) {
				headHeight -= 0.4;
				chestHeight -= 0.4;
			}
		}
		
		//above waist
		if (loc.getY() - eLoc.getY() > chestHeight) {
			//check if headshot
			if (loc.getY() - eLoc.getY() > headHeight) {
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
