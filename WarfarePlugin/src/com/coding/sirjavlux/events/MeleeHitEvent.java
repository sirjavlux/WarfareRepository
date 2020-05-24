package com.coding.sirjavlux.events;

import org.bukkit.Location;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.Husk;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pillager;
import org.bukkit.entity.Player;
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
import org.bukkit.util.Vector;

import com.coding.sirjavlux.armors.Armor;
import com.coding.sirjavlux.armors.ArmorManager;
import com.coding.sirjavlux.core.ConfigManager;
import com.coding.sirjavlux.melee.Melee;
import com.coding.sirjavlux.projectiles.BodyPart;

public class MeleeHitEvent extends Event implements Cancellable {

	private Melee melee;
	private boolean isCancelled = false;
	private LivingEntity entity, damager;
	private double damage, armorProt; 
	private BodyPart part;
    private final ItemStack protectingPiece;
    private Armor armor;
    
	public MeleeHitEvent(Melee melee, LivingEntity entity, LivingEntity damager) {
		this.melee = melee;
		this.entity = entity;
		this.damager = damager;
		this.part = isBodyPartRegistrable(entity) ? getCalculateHitBodyPart() : BodyPart.Chest;
		this.protectingPiece = getHitArmorPice();
		if (ArmorManager.isArmor(protectingPiece)) {
			armor = ArmorManager.getArmorFromItem(protectingPiece);
		}
		calculateDamage();
	}
	
	//get protecting armor pice
    private ItemStack getHitArmorPice() {
		ItemStack piece = null;
		switch (part) {
		//chest
		case Chest: piece = entity.getEquipment().getChestplate();
			break;
		//head
		case Head: piece = entity.getEquipment().getHelmet();
			break;
		//leg
		case Leg: piece = entity.getEquipment().getLeggings();
			break;
		}
		return piece;
    }
    
	/*/////////////////////////
	 * GETTERS AND SETTERS
	 */////////////////////////
    private static final HandlerList HANDLERS = new HandlerList();
    
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
    
    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }
    
    public ItemStack getHitArmorPiece() {
    	return this.protectingPiece;
    }
    
    public double getArmorProtection() {
    	return this.armorProt;
    }
    
    private void calculateDamage() {
    	double damage = melee.getDamage();
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
			damage = damage - damage * armorProt;
		}
		this.damage = damage;
    }
    
	public Melee getMelee() { return melee; }
	public LivingEntity getEntity() { return entity; }
	public LivingEntity getDamager() { return damager; }
	public double getDamage() { return damage; }
	public BodyPart getHitBodyPart() { return part; }
	public Armor getHitArmor() { return armor; }
	
	public void setDamage(double damage) { this.damage = damage; }
	
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
		
		Location eLoc = entity.getLocation();
		Location loc = damager.getEyeLocation();
		Vector dir = damager.getEyeLocation().getDirection().clone().normalize();
		
		int count = 0;
		while (eLoc.clone().subtract(0, eLoc.getY(), 0).distance(loc.clone().subtract(0, loc.getY(), 0)) > 0.27) {
			Location forward = loc.clone().add(dir.clone().multiply(0.03));
			Location backward = loc.clone().subtract(dir.clone().multiply(0.03));
			double dist1 = eLoc.clone().subtract(0, eLoc.getY(), 0).distance(forward.clone().subtract(0, forward.getY(), 0));
			double dist2 = eLoc.clone().subtract(0, eLoc.getY(), 0).distance(backward.clone().subtract(0, backward.getY(), 0));
			if (dist1 < dist2) {
				loc.add(dir.clone().multiply(dist1 * 0.3));
			} else loc.subtract(dir.clone().multiply(dist2 * 0.3));
			if (count > 80) {
				break;
			}
			count++;
		}
		
		double headHeight = 1.5;
		double chestHeight = 0.8;
		
		if (entity instanceof Player) {
			Player target = (Player) entity;
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
