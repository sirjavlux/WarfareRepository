package com.coding.sirjavlux.projectiles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Gate;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import com.coding.sirjavlux.core.ConfigManager;
import com.coding.sirjavlux.core.Main;
import com.coding.sirjavlux.effects.Effect;
import com.coding.sirjavlux.effects.ExplosiveEffect;
import com.coding.sirjavlux.effects.IncindiaryEffect;
import com.coding.sirjavlux.events.BulletHitEvent;
import com.coding.sirjavlux.events.EntityDamagedByBulletEvent;
import com.coding.sirjavlux.health.HealthEffects;
import com.coding.sirjavlux.types.Ammo;
import com.coding.sirjavlux.types.AmmoType;
import com.coding.sirjavlux.types.Weapon;
import com.coding.sirjavlux.utils.SoundUtils;

import net.minecraft.server.v1_15_R1.EntityLiving;
import net.minecraft.server.v1_15_R1.EntitySnowball;
import net.minecraft.server.v1_15_R1.ItemStack;
import net.minecraft.server.v1_15_R1.MovingObjectPosition;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityStatus;
import net.minecraft.server.v1_15_R1.MovingObjectPosition.EnumMovingObjectType;

public class Projectile extends EntitySnowball {

	Weapon weapon;
	Ammo ammo;
	World bukkitWorld;
	Location playerLoc;
	double penetration;
	double bulletSpeed;
	List<LivingEntity> hitEntities;
	Block block;
	
	public Ammo getAmmo() {
		return ammo;
	}
	
	public Projectile(net.minecraft.server.v1_15_R1.World world, EntityLiving e, ItemStack item, Weapon weapon, Ammo ammo) {
		super(world, e);
		
		this.setItem(item);
		this.weapon = weapon;
		this.ammo = ammo;
		CraftEntity entity = this.getBukkitEntity();
		Player p = (Player) this.getShooter().getBukkitEntity();
		this.playerLoc = p.getLocation();
		this.bukkitWorld = p.getWorld();
		this.bulletSpeed = ammo.getSpeed();
		this.penetration = ammo.getArmorPenetration();
		this.hitEntities = new ArrayList<>();
		entity.setVelocity(p.getEyeLocation().getDirection().normalize().multiply(bulletSpeed));
		entity.setCustomNameVisible(false);
		if (ammo.getAmmoType().equals(AmmoType.Flame)) {
			Main.getInstance().getParticleSpawner().addFlameProjectileEffect(this);
		} else if (ammo.getTrail() != null) {
			Main.getInstance().getParticleSpawner().addProjectileTrailEffect(this);
		}
		updateName();
	}

	@Override
    protected void a(MovingObjectPosition movingobjectposition) {
		/*//////////////////////////////////////////
		 * check if hit block should be ignored
		 *//////////////////////////////////////////
		Location hitLoc = new Location(bukkitWorld, movingobjectposition.getPos().getX(), movingobjectposition.getPos().getY(), movingobjectposition.getPos().getZ());
		Location hitBlockLoc = hitLoc.getBlock().getLocation().add(0.5, 0.5, 0.5);
		block = hitBlockLoc.getBlock();
		Material mat = block.getType();
		//check if hit location has potential target
		if (movingobjectposition.getType().equals(EnumMovingObjectType.BLOCK)) {
			boolean passTrough = false;
			//if block is air check block in front
			org.bukkit.util.Vector dirVector = hitLoc.toVector().subtract(playerLoc.toVector());
			if (mat.equals(Material.AIR)) {
				dirVector.normalize();
				dirVector.setX(dirVector.getX() / 10);
				dirVector.setY(dirVector.getY() / 10);
				dirVector.setZ(dirVector.getZ() / 10);
				Location newHitLoc = hitLoc.toVector().add(dirVector).toLocation(bukkitWorld);
				block = newHitLoc.getBlock();
				mat = block.getType();
				if (mat.equals(Material.AIR)) passTrough = true;
			}
			if (ConfigManager.getIgnoredBlocks().contains(mat)) passTrough = true;
			//check if open fence gate
			else if (block.getBlockData() instanceof Gate) {
				Gate gate = (Gate) block.getBlockData();
				if (gate.isOpen()) passTrough = true;
			} 
			//check if entity inside block
			if (passTrough) {
				double accuracy = 20;
				LivingEntity closest = null;
				Player p = (Player) this.getShooter().getBukkitEntity();
				for (int i = 0; i < accuracy; i++) {
					Collection<Entity> entities = new ArrayList<>();
					Location loc = hitLoc.clone().add(dirVector.clone().multiply((1 / accuracy) * i));
					entities.addAll(loc.getWorld().getNearbyEntities(loc, 1 / 20, 2, 1 / 20));
					for (Entity entity : entities) {
						if (entity instanceof LivingEntity && !p.getUniqueId().equals(entity.getUniqueId())) {
							if (closest == null) closest = (LivingEntity) entity;
							else if (closest.getLocation().distance(hitLoc) > entity.getLocation().distance(hitLoc)) closest = (LivingEntity) entity;
						}
					}
					if (closest != null) break;
				}
				//hurt closest target if not null
				if (closest != null) {
					hitEntities.add(closest);
					CraftEntity craftEntity = this.getBukkitEntity();
					org.bukkit.entity.Projectile projectile = (org.bukkit.entity.Projectile) craftEntity;
					double speed = entityDamageEvent(closest, projectile);
					bulletSpeed = speed;
					//collateral if speed is greater than 0
					if (speed > 0) {
						projectile.setVelocity(projectile.getVelocity().normalize().multiply(bulletSpeed));
						return;
					}
				} else return;
			}
		} 
		
		/*///////////////////////////////////
		 * If hit target is an entity
		 *///////////////////////////////////
		else if (movingobjectposition.getType().equals(EnumMovingObjectType.ENTITY)) {
			Collection<Entity> nearbyEntities = hitLoc.getWorld().getNearbyEntities(hitLoc, 1, 1, 1);
			LivingEntity hitEntity = null;
			for (Entity entity : nearbyEntities) {
				if (!(entity instanceof LivingEntity)) continue;
				if (hitEntity == null) hitEntity = (LivingEntity) entity;
				else {
					Location loc = entity.getLocation();
					double distance = hitLoc.distance(loc);
					double oldDistance = hitLoc.distance(hitEntity.getLocation());
					if (distance < oldDistance) {
						hitEntity = (LivingEntity) entity;
					}
				}
			}
			if (hitEntity != null) {
				if (!hitEntities.contains(hitEntity)) {
					hitEntities.add(hitEntity);
					CraftEntity craftEntity = this.getBukkitEntity();
					org.bukkit.entity.Projectile projectile = (org.bukkit.entity.Projectile) craftEntity;
					double speed = entityDamageEvent(hitEntity, projectile);
					bulletSpeed = speed;
					//collateral if speed is greater than 0
					if (speed > 0) {
						projectile.setVelocity(projectile.getVelocity().normalize().multiply(bulletSpeed));
						return;
					} 
				}
			}
		}
		projectileHitEvent(hitLoc);
		this.killEntity();
		return;
    }
	
	/*///////////////////////////////////
	 * Entity damage event by bullet
	 *///////////////////////////////////
	private double entityDamageEvent(LivingEntity entity, org.bukkit.entity.Projectile projectile) {
		//run event
		double speed = projectile.getVelocity().length();
		if (speed > ammo.getSpeed()) speed = ammo.getSpeed();
		//fire event
		EntityDamagedByBulletEvent event = new EntityDamagedByBulletEvent(entity, projectile, penetration);
		Bukkit.getPluginManager().callEvent(event);
		//if not cancelled deal continue event
		if (!event.isCancelled()) {
			if (entity.getHealth() > 0) {
				double startHealth = entity.getHealth();
				double finalHealth = startHealth - event.damage() < 0 ? 0 : startHealth - event.damage();
				entity.setHealth(finalHealth);
				//fire status packet
				net.minecraft.server.v1_15_R1.Entity ce = ((CraftEntity) entity).getHandle();
				PacketPlayOutEntityStatus statusPacket = new PacketPlayOutEntityStatus(ce, (byte) 2);
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (entity.getLocation().distance(p.getLocation()) < 30) ((CraftPlayer) p).getHandle().playerConnection.sendPacket(statusPacket);
				}
				//burn entity if possible
				entity.setFireTicks(ammo.getHitBurnTicks());
				//deal knock back
				double knockback = event.getAmmo().getKnockBack();
				Location entityLoc = entity.getLocation();
				Location pLoc = projectile.getLocation();
				Vector knockDir = entityLoc.subtract(0, entityLoc.getY(), 0).toVector().subtract(pLoc.subtract(0, pLoc.getY(), 0).toVector());
				knockDir.setY(0.65);
				entity.setVelocity(entity.getVelocity().add(knockDir.multiply(knockback / 3)));
				//destroy armor depending on round armor damage
				org.bukkit.inventory.ItemStack hitArmor = event.getHitArmorPiece();
				if (hitArmor != null) {
					if (hitArmor.getItemMeta() instanceof Damageable) {
						double armorDamage = event.getAmmo().getArmorDamage();
						Damageable damageableItem = ((Damageable) hitArmor.getItemMeta());
						int maxDurability = hitArmor.getType().getMaxDurability();
						int durability = maxDurability - damageableItem.getDamage();
						int finalDurability = (int) (durability - armorDamage < 0 ? 0 : durability - armorDamage);
						damageableItem.setDamage(maxDurability - finalDurability);
						hitArmor.setItemMeta((ItemMeta) damageableItem);
						switch(event.getHitBodyPart()) {
						case Chest: entity.getEquipment().setChestplate(hitArmor);
							break;
						case Head: entity.getEquipment().setHelmet(hitArmor);
							break;
						case Leg: entity.getEquipment().setLeggings(hitArmor);
							break;
						}
					}	
				}
				//calculate concussion, broken legs, bleeding
				double bleedingDamage = 0;
				boolean brokenBone = false;
				boolean concussion = false;
				
				switch(event.getHitBodyPart()) {
				case Chest: 
					break;
				case Head: 
					double concussionChance = ConfigManager.getConcussionChance() + ConfigManager.getConcussionDamageChance() * event.damage();
					if (concussionChance > Math.random()) concussion = true;
					break;
				case Leg: 
					double brokenBoneChance = ConfigManager.getBreakLegDamageChance() * event.damage();
					if (brokenBoneChance > Math.random()) brokenBone = true;
					break;
				}
				
				double bleedingChance = ConfigManager.getBleadingDamageChance() * event.damage();
				if (bleedingChance > Math.random()) bleedingDamage = ConfigManager.getBleedingPerDamage() * event.damage();
				
				if (bleedingDamage > 0 && ConfigManager.bleedingEnabled()) {
					HealthEffects.addBleeding(entity, bleedingDamage);
				}
				if (entity instanceof Player) {
					Player p = (Player) entity;
					if (brokenBone && ConfigManager.breakLegEnabled()) {
						HealthEffects.breakLeg(p);
					}
					if (concussion && ConfigManager.concussionEnabled()) {
						HealthEffects.concussion(p);
					}
				}
			}
			//calculate reduced collateral speed
			double penRed = ConfigManager.getPenetrationReductionArmor();
			double speedRed = ConfigManager.getSpeedReductionEntity();
			double armorProt = event.getArmorProtection();
			double entityPenRed = ConfigManager.getPenetrationReductionEntity();
			penetration -= (armorProt * penRed) + penetration * entityPenRed;
			double penSpeedReduc = speedRed * 1 - (penetration * ammo.getArmorPenetration()) + 1 - (penetration * ammo.getArmorPenetration());
			speed -= ammo.getSpeed() * penSpeedReduc;
			//play sounds
			Location loc = entity.getLocation();
			if (armorProt > 0) loc.getWorld().playSound(loc, ammo.getHitArmorSound(), 1.5f, 1);
			else loc.getWorld().playSound(loc, ammo.getHitFleshSound(), 1, 1);
		} else {
			speed = 0;
		}		
		
		return speed;
	}
	
	/*///////////////////////////////////
	 * Projectile hit event
	 *///////////////////////////////////
	private void projectileHitEvent(Location loc) {
		CraftEntity craftEntity = this.getBukkitEntity();
		org.bukkit.entity.Projectile projectile = (org.bukkit.entity.Projectile) craftEntity;
		BulletHitEvent event = new BulletHitEvent(ammo, projectile);
		Bukkit.getPluginManager().callEvent(event);
		
		if (!event.isCancelled()) {
			Block block = getHitBlock(loc);
			try {
				if (!ammo.getHitGroundSound().isEmpty()) {
					loc.getWorld().playSound(loc, ammo.getHitGroundSound(), 0.27f, 1);
				} else {
					Sound sound = SoundUtils.getBlockBreakSound(block);
					loc.getWorld().playSound(loc, sound, 0.45f, 1);
				}
			} catch(Exception e) { }
			Ammo eventAmmo = event.getAmmo();
			AmmoType type = eventAmmo.getAmmoType();
			switch (type) {
			case Explosive: 
				loc.getWorld().playSound(loc, ammo.getExplodeSound(), 3.7f, 1);
				Effect effect = new ExplosiveEffect(loc, ammo.getExplotionDamage(), ammo.getExplotionRange(), ammo.getExplotionRange(), ammo.getExplosionFireTicks(), ammo.getExplotionDrop(), (LivingEntity) projectile.getShooter());
				effect.playEffect();
				break;
			case Flame:
				break;
			case Incindiary: 
				loc.getWorld().playSound(loc, ammo.getExplodeSound(), 2.9f, 1);
				effect = new IncindiaryEffect(loc, 0, ammo.getExplotionDamage(), ammo.getExplotionRange(), ammo.getExplotionRange() / 10, ammo.getExplosionFireTicks(), 9, ammo.getExplotionDrop(), (LivingEntity) projectile.getShooter());
				effect.playEffect();
				break;
			case Regular: 
				loc.getWorld().spawnParticle(Particle.BLOCK_CRACK, getHitParticleLocation(loc), 3, block.getBlockData());
				break;
			case Split:
				loc.getWorld().spawnParticle(Particle.BLOCK_CRACK, getHitParticleLocation(loc), 3, block.getBlockData());
				break;
			}
		}
	}
	
	public Location getHitParticleLocation(Location loc) {
		Location placeLoc = loc.clone();
		Vector dir = this.getBukkitEntity().getVelocity();
		boolean isAir = false;
		int count = 0;
		while (!isAir) {
			if (placeLoc.clone().subtract(0, 0.2, 0).getBlock().getType() == Material.AIR || count > 30) {
				isAir = true;
			} else {
				placeLoc.subtract(dir.clone().normalize().multiply(0.1));
			}
			count++;
		}
		return placeLoc;
	}
	
	public Block getHitBlock(Location loc) {
		Block block = loc.getBlock();
		Location tempLoc = loc.clone();
		if (loc.getBlock().getType() != Material.AIR) block = loc.getBlock();
		else {
			Vector dir = this.getBukkitEntity().getVelocity();
			boolean isBlock = false;
			int count = 0;
			while (!isBlock) {
				if (tempLoc.getBlock().getType() != Material.AIR) {
					block = loc.getBlock();
					break;
				} else {
					if (count > 15) break;
					tempLoc = tempLoc.add(dir.clone().normalize().multiply(0.2));
					count++;	
				}
			}
		}
		return block;
	}
	
	/*///////////////////////////////////
	 * Utility
	 *///////////////////////////////////
	private void updateName() {
		CraftEntity entity = this.getBukkitEntity();
		entity.setCustomName("projectile," + weapon.getName() + "," + ammo.getName());
	}
}
