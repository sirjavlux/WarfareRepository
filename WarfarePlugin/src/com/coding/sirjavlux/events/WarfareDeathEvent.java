package com.coding.sirjavlux.events;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WarfareDeathEvent extends Event implements Cancellable {

	private boolean isCancelled = false;
	private LivingEntity entity, damager;
	private String deathMessage;
	private WarfareDeathCause deathCause;
	
	public WarfareDeathEvent(LivingEntity entity, LivingEntity damager, WarfareDeathCause deathCause) {
		this.entity = entity;
		this.damager = damager;
		this.deathCause = deathCause;
		this.deathMessage = entity instanceof Player && damager instanceof Player ? createDeathMessage() : "";
	}
	
	private String createDeathMessage() {
		String message = "test";
		switch (deathCause) {
		case Bleeding:
			break;
		case Bullet:
			break;
		case Effect:
			break;
		case Melee:
			break;
		case Thirst:
			break;
		}
		return message;
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
    public String getDeathMessage() { return deathMessage; }
    public WarfareDeathCause getWarfareDeathCause() { return deathCause; }
    
    public void setDeathMessage(String message) { deathMessage = message; }
    
    public enum WarfareDeathCause {
    	Effect,
    	Bullet,
    	Melee,
    	Thirst,
    	Bleeding
    }
}
