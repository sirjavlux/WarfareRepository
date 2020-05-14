package com.coding.sirjavlux.effects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.Validate;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class DamageCuboid {

    private UUID worldID;
    private double x1, y1, z1, x2, y2, z2;
    private int activationDelay;
    
    public DamageCuboid(Location location, Location location2, int activationDelay) {
        Validate.notNull(location);
        Validate.notNull(location2);
        
        this.worldID = location.getWorld().getUID();

        this.x1 = Math.min(location.getX(), location2.getX());
        this.y1 = Math.min(location.getY(), location2.getY());
        this.z1 = Math.min(location.getZ(), location2.getZ());

        this.x2 = Math.max(location.getX(), location2.getX());
        this.y2 = Math.max(location.getY(), location2.getY());
        this.z2 = Math.max(location.getZ(), location2.getZ());
        
        this.activationDelay = activationDelay;
    }
    
    public World getWorld() { return Bukkit.getWorld(worldID); }
    public Location getMinLocation() { return new Location(getWorld(), x1, y1, z1); }
    public Location getMaxLocation() { return new Location(getWorld(), x2, y2, z2); }
    public int getActivationDelay() { return activationDelay; }
    public void setActivationDelay(int activationDelay) { this.activationDelay = activationDelay; }
    
    public List<Entity> getEntitiesInsideCuboid() {
    	List<Entity> entityList = new ArrayList<>();
    	Entity[] chunkEntities = getMinLocation().getChunk().getEntities();
    	for (Entity entity : chunkEntities) {
    		if (entity instanceof LivingEntity) if (isInsideCuboid(entity)) entityList.add(entity);
    	}
    	return entityList;
    }
    
    public boolean isInsideCuboid(Entity entity) {
    	Location min = getMinLocation();
    	Location max = getMaxLocation();
    	Location loc = entity.getLocation();
        if(loc.getX() >= min.getX() && loc.getX() <= max.getX()) {
            if(loc.getY() >= min.getY() && loc.getY() <=  max.getY()) {
                if(loc.getZ() >= min.getZ() && loc.getZ() <= max.getZ()) {
                    return true;
                }
            }
        }
        return false;
    }
}
