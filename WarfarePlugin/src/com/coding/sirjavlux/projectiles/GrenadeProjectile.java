package com.coding.sirjavlux.projectiles;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Gate;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.material.TrapDoor;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import com.coding.sirjavlux.core.ConfigManager;
import com.coding.sirjavlux.effects.Effect;
import com.coding.sirjavlux.effects.ExplosiveEffect;
import com.coding.sirjavlux.effects.FireEffect;
import com.coding.sirjavlux.effects.FlashEffect;
import com.coding.sirjavlux.effects.SmokeEffect;
import com.coding.sirjavlux.grenades.Grenade;
import com.coding.sirjavlux.grenades.GrenadeManager;
import com.coding.sirjavlux.grenades.GrenadeManager.GrenadeTimer;

import net.minecraft.server.v1_15_R1.*;

@SuppressWarnings("deprecation")
public class GrenadeProjectile extends EntitySnowball {

	private World bukkitWorld;
	private double speed;
	private Grenade grenade;
	private Player p;
	private UUID timerUUID;
	private ItemStack item;
	
	public GrenadeProjectile(net.minecraft.server.v1_15_R1.World world, EntityLiving e, ItemStack item, Grenade grenade) {
		super(world, e);
		CraftEntity entity = this.getBukkitEntity();
		this.p = (Player) this.getShooter().getBukkitEntity();
		this.setItem(item);
		this.bukkitWorld = p.getWorld();
		this.speed = grenade.getSpeed();
		this.grenade = grenade;
		this.item = item;
		entity.setVelocity(p.getEyeLocation().getDirection().normalize().multiply(speed));
		entity.setCustomNameVisible(false);
		timerUUID = GrenadeManager.addGrenadeTimer(entity.getUniqueId(), grenade, p);
	}

	@Override
    protected void a(MovingObjectPosition movingobjectposition) {
		Location hitLoc = new Location(bukkitWorld, movingobjectposition.getPos().getX(), movingobjectposition.getPos().getY(), movingobjectposition.getPos().getZ());
		Vector dir = this.getBukkitEntity().getVelocity();
		Block block = hitLoc.clone().add(dir.clone().normalize().multiply(0.5)).getBlock();
		Material mat = block.getType();
		//check if block should be ignored
		if (ConfigManager.getIgnoredBlocks().contains(mat)) return;
		//check if open fence gate
		else if (block.getBlockData() instanceof Gate) {
			Gate gate = (Gate) block.getBlockData();
			if (gate.isOpen()) return;
		} 
		//bounce or detonate grenades
		switch (grenade.getType()) {
		case Fire: 
			hitLoc.getWorld().playSound(hitLoc, grenade.getExplodeSound(), 5, 1);
			Effect effect = new FireEffect(hitLoc, grenade.getDuration(), grenade.getExplosionDamage(), grenade.getExplosionRange(), 0.2, grenade.getFireTicks(), (int) (4d * grenade.getExplosionRange()), grenade.getExplosionDamageDrop(), p, this.getBukkitEntity().getVelocity());
			effect.playEffect();
			break;
		case Flash: 
			hitLoc.getWorld().playSound(hitLoc, grenade.getExplodeSound(), 5, 1);
			effect = new FlashEffect(hitLoc, grenade.getDuration(), grenade.getExplosionDamage(), grenade.getExplosionRange(), grenade.getExplosionRange(), grenade.getFireTicks(), (int) (4d * grenade.getExplosionRange()), this.getBukkitEntity().getVelocity());
			effect.playEffect();
			break;
		case Smoke: 
			hitLoc.getWorld().playSound(hitLoc, grenade.getExplodeSound(), 5, 1);
			effect = new SmokeEffect(hitLoc, grenade.getDuration(), grenade.getExplosionDamage(), grenade.getExplosionRange(), grenade.getExplosionRange() * 0.72, grenade.getFireTicks(), this.getBukkitEntity().getVelocity());
			effect.playEffect();
			break;
		case Explosion:
			hitLoc.getWorld().playSound(hitLoc, grenade.getExplodeSound(), 5, 1);
			effect = new ExplosiveEffect(hitLoc, grenade.getExplosionDamage(), grenade.getExplosionRange(), grenade.getExplosionRange(), grenade.getFireTicks(), grenade.getExplosionDamageDrop(), p);
			effect.playEffect();
			break;
		case Explosion_Delayed:
			bounceEntity(hitLoc);
			return;
		case Fire_Delayed:
			bounceEntity(hitLoc);
			return;
		case Flash_Delayed:
			bounceEntity(hitLoc);
			return;
		case Smoke_Delayed:
			bounceEntity(hitLoc);
			return;
		}
		this.killEntity();
		return;
    }
	
	private void bounceEntity(Location hitLoc) {
		CraftEntity projectile = this.getBukkitEntity();
		Vector dir = projectile.getVelocity();
		double speed = dir.length();
		
		//play bounce sound
		hitLoc.getWorld().playSound(hitLoc, grenade.getBounceSound(), 0.68f, 1);
		
		if (speed < 0.25D) {
            Item item = hitLoc.getWorld().dropItem(hitLoc, CraftItemStack.asBukkitCopy(this.item));  
            item.setVelocity(new Vector(0, 0, 0));
            item.setInvulnerable(true);
            item.setPickupDelay(1000000);
            item.getLocation().setDirection(this.getBukkitEntity().getLocation().getDirection());
            
            UUID itemUUID = item.getUniqueId();
            GrenadeTimer timer = GrenadeManager.getGrenadeTimer(timerUUID);
            timer.setUniqueId(itemUUID);
			this.killEntity();
		}

		Block hitBlock = hitLoc.getBlock();
		BlockFace blockFace = BlockFace.UP;

		//check if block above is grass
		Block grassTest = hitLoc.clone().subtract(dir.clone().normalize().multiply(0.5)).add(0, 1, 0).getBlock();
		if (((grassTest.getType().name().contains("GRASS") && !grassTest.getType().name().contains("BLOCK")) 
				|| grassTest.getType().name().contains("FLOWER")) 
				&& hitBlock.getType() != grassTest.getType()) {
			hitLoc.setY(Math.round(hitLoc.getY()) + 1.1);
			hitBlock = hitLoc.getBlock();
		}
		// special cases:
		if (isWoodenTrigger(hitBlock.getType())) return;
		if (!isHollowUpDownType(hitBlock)) {
			BlockIterator blockIterator = new BlockIterator(hitLoc.getWorld(), hitLoc.toVector(), dir, 0.0D, 3);

			Block previousBlock = hitBlock;
			Block nextBlock = blockIterator.next();
			if (isWoodenTrigger(nextBlock.getType())) return;

			// to make sure, that previousBlock and nextBlock are not the same block
			while (blockIterator.hasNext() && (nextBlock.getType() == Material.AIR || nextBlock.isLiquid() || nextBlock.equals(hitBlock))) {
				previousBlock = nextBlock;
				nextBlock = blockIterator.next();
				if (isWoodenTrigger(nextBlock.getType())) return;
			}

			// direction
			blockFace = nextBlock.getFace(previousBlock);

		}
		
		if (blockFace != null) {
			if (blockFace == BlockFace.SELF) {
				blockFace = BlockFace.UP;
			}

			if (isWoodenTrigger(hitBlock.getRelative(blockFace).getType())) return;

			Vector mirrorDirection = new Vector(blockFace.getModX(), blockFace.getModY(), blockFace.getModZ());
			double dotProduct = dir.dot(mirrorDirection);
			mirrorDirection = mirrorDirection.multiply(dotProduct).multiply(2.0D);

			// reduce projectile speed
			speed *= 0.51D;
			
			//set velocity
			projectile.setVelocity(dir.subtract(mirrorDirection).normalize().multiply(speed));
		}
	}
	
	private boolean isHollowUpDownType(Block block) {
		Material type = block.getType();
		return isStep(type) || type.name().contains("CARPET") || type == Material.SNOW
				|| type == Material.REPEATER || type == Material.COMPARATOR || type == Material.GRASS || type == Material.TALL_GRASS || (type.name().contains("SEA") && !type.name().contains("LANTERN"))
				|| type == Material.CAULDRON || type.name().contains("BED") || type == Material.DAYLIGHT_DETECTOR || type.name().contains("FLOWER")
				|| type.name().contains("RAIL") || type == Material.DETECTOR_RAIL || type == Material.POWERED_RAIL
				|| type == Material.ACTIVATOR_RAIL || type == Material.HEAVY_WEIGHTED_PRESSURE_PLATE || type == Material.LIGHT_WEIGHTED_PRESSURE_PLATE
				|| type == Material.STONE_PRESSURE_PLATE || (type.name().contains("TRAPDOOR") && !(new TrapDoor(type, block.getData()).isOpen()));
	}

	private boolean isStep(Material type) {
		return type.name().contains("SLAB");
	}

//	private boolean isStair(Material type) {
//		return type.name().contains("STAIR");
//	}

	private boolean isWoodenTrigger(Material type) {
		return type.name().contains("BUTTON");
	}
}
