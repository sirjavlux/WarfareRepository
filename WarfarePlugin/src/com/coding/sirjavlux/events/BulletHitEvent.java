package com.coding.sirjavlux.events;

import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.projectiles.ProjectileSource;

import com.coding.sirjavlux.types.Ammo;

public class BulletHitEvent extends Event implements Cancellable {

	private Ammo ammo;
	private Projectile projectile;
	private ProjectileSource shooter;
	private boolean isCancelled = false;
	
	public BulletHitEvent(Ammo ammo, Projectile projectile) {
		this.ammo = ammo;
		this.projectile = projectile;
		this.shooter = projectile.getShooter();
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
    
	public Ammo getAmmo() { return ammo; }
	public void setAmmo(Ammo ammo) { this.ammo = ammo; }
	public Projectile getProjectile() { return projectile; }
	public ProjectileSource getShooter() { return shooter; }
}
