package com.coding.sirjavlux.effects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.coding.sirjavlux.core.Main;
import com.coding.sirjavlux.effectUtils.EffectParticle;
import com.coding.sirjavlux.events.EntityDamagedByEffectEvent;

import net.minecraft.server.v1_15_R1.PacketPlayOutEntityStatus;

public class FireEffect implements Effect {

	private double damage, radius, height, damageRed;
	private int duration, fireTicks, cooldown, startCooldown, intensity;
	private List<EffectParticle> particles;
	private Location loc;
	private List<DamageCuboid> damageLocations;
	private List<LivingEntity> entities;
	private LivingEntity entity;
	private UUID uuid;
	private Vector projectileDir;
	
	public FireEffect(Location loc, int duration, double damage, double radius, double height, int fireTicks, int intensity, double damageRed, LivingEntity entity, Vector projectileDir) {
		this.damage = damage;
		this.radius = radius;
		this.height = height;
		this.fireTicks = fireTicks;
		this.duration = duration;
		this.loc = loc;
		this.intensity = intensity;
		this.damageRed = damageRed;
		this.entity = entity;
		this.projectileDir = projectileDir;
		particles = new ArrayList<>();
		damageLocations = new ArrayList<>();
		entities = new ArrayList<>();
		uuid = UUID.randomUUID();
		startCooldown = 10;
		cooldown = startCooldown;
	}
	
	public void calculateCenterLocation() {
		//fix loc
		boolean isSolid = true;
		Location temp = loc.clone();
		int count = 0;
		while (isSolid) {
			temp.subtract(projectileDir.clone().normalize().multiply(1d / 10d));
			if (temp.getBlock().isPassable()) {
				loc = temp;
				isSolid = false;
			} else if (count > 80) isSolid = false;
			count++;
		}
	}
	
	private Effect getEffect() {
		return this;
	}
	
	@Override
	public void playEffect() {
		new BukkitRunnable() {
			@Override
			public void run() {
				calculateCenterLocation();
				Location center = loc.clone();
				List<Block> addedLocations = new ArrayList<>();
				List<EffectParticle> particles = new ArrayList<>();
				double effectSpeed = 0.4;
				//first drop
				int fallAmount = 5;
				boolean groundHit = false;
				int fallCount = 0;
				int fallDuration = 0;
				double modifier = 0.04;
				//create fallVectors
				Location locBelow = center.clone().subtract(0, 0.1, 0);
				Vector dir = locBelow.toVector().subtract(center.toVector()).normalize();
				HashMap<Vector, Location> fallLocations = new HashMap<>();
				for (int i = 0; i < fallAmount; i++) {
					double x = 0 - modifier + (0 + modifier - (0 - modifier)) * Math.random();
					double z = 0 - modifier + (0 + modifier - (0 - modifier)) * Math.random();
					Vector tempDir = dir.clone();
					tempDir.setX(tempDir.getX() + x);
					tempDir.setZ(tempDir.getZ() + z);
					fallLocations.put(tempDir.clone(), center.clone());
				}
				while (!groundHit) {
					Block blockBelow = center.clone().subtract(0, 0.1, 0).getBlock();
					//break loop
					if ((!blockBelow.getType().isAir() && !blockBelow.isEmpty() && !blockBelow.isLiquid() && !blockBelow.isPassable()) || (double) fallCount / (double) intensity > 100) {
						//if no bottom, play set particles
						if (fallCount / intensity > 100) {
							setParticles(particles);
							Main.getInstance().getParticleSpawner().addEffect(getEffect());
							return;
						}
						else {
							groundHit = true;
						}
					} 
					//place particle and reduce center height
					else {
						double y = center.getY();
						for (Entry<Vector, Location> entry : fallLocations.entrySet()) {
							Location loc = entry.getValue();
							Vector direction = entry.getKey();
							y = loc.getY() < y ? loc.getY() : y;
							loc.add(direction.clone().multiply(1d / (double) intensity));
							particles.add(new EffectParticle(loc.clone(), Particle.FLAME, (int) (fallCount / (effectSpeed * intensity)), UUID.randomUUID()));
						
						}
						center.setY(y);
					}
					fallCount++;
				}
				fallDuration = (int) (fallCount / (effectSpeed * intensity));
				//place surrounding fire and add fire locations
				int startIntensity = intensity;
				int delay = 20;
				for (int i = 0; i < (duration < delay ? delay : duration) / delay; i++) {
					for (double h = 0; h < height; h += (height / (double) startIntensity) * ((radius * 2) / height)) {
						for (double r = 0; r < radius * (height < 1 ? 1 : (h / height < 0.3 ? h / (height / (1 + 2 * (1 - h / height))) : (1 - (h / height) / (1 + 2 * (1 - h / height))))); r += (radius / (double) startIntensity)) {
							int intensity = (int) ((double) startIntensity * (r / radius));
						    double increment = (2 * Math.PI) / (double) intensity;
						    for (int i2 = 0; i2 < intensity; i2++) {
								Location tempCenter = center.clone();
						        double angle = ((i2 + 1) * increment) * (1 - Math.random() / 3);
						        double x = (r * (1 - Math.random() / 2)) * Math.cos(angle);
						        double z = (r * (1 - Math.random() / 2)) * Math.sin(angle);
						        tempCenter.add(x, h, z);
						        Block blockBelow = tempCenter.clone().subtract(0, 0.2, 0).getBlock();
						        //if hit block is solid and block above is passable
						        if (tempCenter.clone().add(0, 1, 0).getBlock().isPassable() && !tempCenter.getBlock().isPassable()) {
						        	particles.add(new EffectParticle(tempCenter.clone().add(0, 1.1, 0), Particle.FLAME, i * delay + fallDuration, UUID.randomUUID()));					        	
						        	if (!addedLocations.contains(tempCenter.clone().add(0, 1.1, 0).getBlock()) && i == 0) {
						        		Location blockLoc = tempCenter.clone().add(0, 1.1, 0).getBlock().getLocation();
						        		addedLocations.add(blockLoc.clone().getBlock());
						        		damageLocations.add(new DamageCuboid(blockLoc.clone(), blockLoc.clone().add(1, 1, 1), i * delay + fallDuration));
						        	}
						        }
						        //if block is passable and block below is solid
						        if (tempCenter.getBlock().isPassable() && !blockBelow.isPassable()) {
									particles.add(new EffectParticle(tempCenter.clone().add(0, 0.1, 0), Particle.FLAME, i * delay + fallDuration, UUID.randomUUID()));	
									if (!addedLocations.contains(tempCenter.clone().add(0, 0.1, 0).getBlock()) && i == 0) {
						        		Location blockLoc = tempCenter.clone().add(0, 0.1, 0).getBlock().getLocation();
						        		addedLocations.add(blockLoc.clone().getBlock());
						        		damageLocations.add(new DamageCuboid(blockLoc.clone(), blockLoc.clone().add(1, 1, 1), i * delay + fallDuration));
						        	}
						        }
						        //if block below and above are passable 
						        else if (tempCenter.getBlock().isPassable() && blockBelow.isPassable()) {
						        	boolean reachedGround = false;
						        	double rand = Math.random();
						        	fallCount = 0;
						        	while (!reachedGround) {
						        		blockBelow = tempCenter.clone().subtract(0, 0.2, 0).getBlock();
						        		if (fallCount / intensity > 15) {
						        			reachedGround = true;
						        		} else {
											if (i > 0) {
												if (!blockBelow.isPassable()) {
							        				particles.add(new EffectParticle(tempCenter.clone(), Particle.FLAME, i * delay + (int) (fallCount / (effectSpeed * intensity)) + fallDuration, UUID.randomUUID()));
							        				reachedGround = true;
							        			}
											} else {
												//solid
							        			if (!blockBelow.isPassable()) {
							        				particles.add(new EffectParticle(tempCenter.clone(), Particle.FLAME, i * delay + (int) (fallCount / (effectSpeed * intensity)) + fallDuration, UUID.randomUUID()));
							        				if (!addedLocations.contains(tempCenter.clone().getBlock())) {
										        		Location blockLoc = tempCenter.clone().getBlock().getLocation();
										        		addedLocations.add(blockLoc.clone().getBlock());
										        		damageLocations.add(new DamageCuboid(blockLoc.clone(), blockLoc.clone().add(1, 1, 1), i * delay + (int) (fallCount / (effectSpeed * intensity)) + fallDuration));
							        				}
							        				reachedGround = true;
							        			}
							        			//air
							        			else if (rand < 0.18) {
							        				particles.add(new EffectParticle(tempCenter.clone(), Particle.FLAME, i * delay + (int) (fallCount / (effectSpeed * intensity)) + fallDuration, UUID.randomUUID()));
							        			}
											}
						        		}
										fallCount++;
										tempCenter.subtract(0, 1d / (double) intensity, 0);
						        	}
						        }
						    }
						}
					}
				}
				//spawn set particles
				setParticles(particles);
				Main.getInstance().getParticleSpawner().addEffect(getEffect());
				return;
			}
		}.runTaskAsynchronously(Main.getInstance());
	}

	@Override
	public void damageEntities() {
		cooldown--;
		if (cooldown < 1) {
			for (LivingEntity entity : entities) {
				if (entity instanceof Player) if (!((Player) entity).isOnline()) continue; //continue if player and is not online
				if (entity.getHealth() > 0 && (damage > 0 || fireTicks > 0)) {
					double damage = this.damage * (damageRed / loc.distance(entity.getLocation()));
					EntityDamagedByEffectEvent event = new EntityDamagedByEffectEvent(entity, this.entity, damage);
					if (!event.isCancelled()) {
						damage = event.getDamage();
						double finalHealth = entity.getHealth() - damage < 0 ? 0 : entity.getHealth() - damage;
						entity.setHealth(finalHealth);
						entity.setFireTicks(fireTicks);
						//fire status packet
						net.minecraft.server.v1_15_R1.Entity ce = ((CraftEntity) entity).getHandle();
						PacketPlayOutEntityStatus statusPacket = new PacketPlayOutEntityStatus(ce, (byte) 2);
						for (Player p : Bukkit.getOnlinePlayers()) {
							if (entity.getLocation().distance(p.getLocation()) < 30) ((CraftPlayer) p).getHandle().playerConnection.sendPacket(statusPacket);
						}	
					}
				}
			}
			cooldown = startCooldown;
		}
	}

	@Override
	public double getDamage() {
		return damage;
	}

	@Override
	public double getRadius() {
		return radius;
	}

	@Override
	public double getHeight() {
		return height;
	}

	@Override
	public void setDamage(double damage) {
		this.damage = damage;
	}

	@Override
	public void setRadius(double radius) {
		this.radius = radius;
	}

	@Override
	public void setHeight(double height) {
		this.height = height;
	}

	@Override
	public int getPlayerFireTicks() {
		return this.fireTicks;
	}

	@Override
	public void setPlayerFireTicks(int ticks) {
		this.fireTicks = ticks;
	}

	@Override
	public List<LivingEntity> getEntities() {
		return this.entities;
	}

	@Override
	public void setEntities(List<LivingEntity> entities) {
		this.entities = entities;
	}

	@Override
	public UUID getUniqueId() {
		return uuid;
	}

	@Override
	public int getEffectDuration() {
		return duration;
	}
	
	@Override
	public void setEffectDuration(int duration) {
		this.duration = duration;
	}

	@Override
	public List<EffectParticle> getParticles() {
		return particles;
	}

	@Override
	public void setParticles(List<EffectParticle> particles) {
		this.particles = particles;
	}

	@Override
	public Location getLocation() {
		return this.loc;
	}

	@Override
	public List<DamageCuboid> getDamageLocations() {
		return this.damageLocations;
	}
}
