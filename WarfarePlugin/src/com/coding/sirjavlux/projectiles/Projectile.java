package com.coding.sirjavlux.projectiles;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Gate;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.entity.Player;

import com.coding.sirjavlux.core.ConfigManager;
import com.coding.sirjavlux.types.Ammo;
import com.coding.sirjavlux.types.Weapon;

import net.minecraft.server.v1_15_R1.EntityLiving;
import net.minecraft.server.v1_15_R1.EntitySnowball;
import net.minecraft.server.v1_15_R1.ItemStack;
import net.minecraft.server.v1_15_R1.MovingObjectPosition;
import net.minecraft.server.v1_15_R1.MovingObjectPosition.EnumMovingObjectType;

public class Projectile extends EntitySnowball {

	Weapon weapon;
	Ammo ammo;
	World bukkitWorld;
	Location playerLoc;
	
    public Projectile(net.minecraft.server.v1_15_R1.World world, EntityLiving e, ItemStack item, double speedMultiplier, Weapon weapon, Ammo ammo) {
		super(world, e);
		
		this.setItem(item);
		this.weapon = weapon;
		this.ammo = ammo;
		CraftEntity entity = this.getBukkitEntity();
		Player p = (Player) this.getShooter().getBukkitEntity();
		this.playerLoc = p.getLocation();
		this.bukkitWorld = p.getWorld();
		entity.setVelocity(p.getEyeLocation().getDirection().multiply(speedMultiplier));
		entity.setCustomNameVisible(false);
		updateName();
	}

	@Override
    protected void a(MovingObjectPosition movingobjectposition) {
		/*//////////////////////////////////////////
		 * check if hit block should be ignored
		 *//////////////////////////////////////////
		Location hitLoc = new Location(bukkitWorld, movingobjectposition.getPos().getX(), movingobjectposition.getPos().getY(), movingobjectposition.getPos().getZ());
		Location hitBlockLoc = hitLoc.getBlock().getLocation().add(0.5, 0.5, 0.5);
		Block block = hitBlockLoc.getBlock();
		Material mat = block.getType();
		//check if hit location has potential target
		if (movingobjectposition.getType().equals(EnumMovingObjectType.BLOCK)) {
			//if block is air check block in front
			if (mat.equals(Material.AIR)) {
				org.bukkit.util.Vector dirVector = hitLoc.toVector().subtract(playerLoc.toVector());
				dirVector.normalize();
				dirVector.setX(dirVector.getX() / 10);
				dirVector.setY(dirVector.getY() / 10);
				dirVector.setZ(dirVector.getZ() / 10);
				Location newHitLoc = hitLoc.toVector().add(dirVector).toLocation(bukkitWorld);
				block = newHitLoc.getBlock();
				mat = block.getType();
				if (mat.equals(Material.AIR)) return;
			}
			if (ConfigManager.getIgnoredBlocks().contains(mat)) return;
			//check if open fence gate
			else if (block.getBlockData() instanceof Gate) {
				Gate gate = (Gate) block.getBlockData();
				if (gate.isOpen()) return;
			}
		}
		super.a(movingobjectposition);
    }
	
	private void updateName() {
		CraftEntity entity = this.getBukkitEntity();
		entity.setCustomName("projectile," + weapon.getName() + "," + ammo.getName());
	}
}
