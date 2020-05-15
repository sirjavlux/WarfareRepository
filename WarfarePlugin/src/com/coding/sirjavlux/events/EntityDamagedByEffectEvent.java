package com.coding.sirjavlux.events;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import com.coding.sirjavlux.core.ConfigManager;

public class EntityDamagedByEffectEvent extends Event implements Cancellable {

	private boolean isCancelled = false;
	private LivingEntity entity, damager;
	private double damage;
	
	public EntityDamagedByEffectEvent(LivingEntity entity, LivingEntity damager, double damage) {
		this.entity = entity;
		this.damager = damager;
		this.damage = calculateDamage(damage);
	}
	
	/*/////////////////////////
	 * DAMAGE CALCULATOR
	 */////////////////////////
	private double calculateDamage(double damage) {	
		//calculate damage protection
		double armorProt = 0;
		for (ItemStack item : entity.getEquipment().getArmorContents()) {
			if (item != null) {
				double prot = ConfigManager.getItemArmorProtection(item);
				armorProt += prot * (1 - armorProt);
			}
		}
		damage = damage - damage * armorProt;
		return damage;
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
    
    public LivingEntity getTarget() { return entity; }
    public LivingEntity getDamager() { return damager; }
    public double getDamage() { return damage; }
    public void setDamage(double damage) { this.damage = damage; }
}
