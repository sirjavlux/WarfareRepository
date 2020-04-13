package com.coding.sirjavlux.projectiles;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_15_R1.EntityLiving;
import net.minecraft.server.v1_15_R1.EntitySnowball;
import net.minecraft.server.v1_15_R1.ItemStack;
import net.minecraft.server.v1_15_R1.World;
import net.minecraft.server.v1_15_R1.MovingObjectPosition;

public class Projectile extends EntitySnowball {

	double damage;
	Location hitLoc;
	
    public Projectile(World world, EntityLiving e, ItemStack item, double speedMultiplier, double damage) {
		super(world, e);
		
		this.setItem(item);
		this.damage = damage;
		CraftEntity entity = this.getBukkitEntity();
		Player p = (Player) this.getShooter().getBukkitEntity();
		hitLoc = entity.getLocation();
		entity.setVelocity(p.getEyeLocation().getDirection().multiply(speedMultiplier));
		entity.setCustomNameVisible(false);
		updateName();
	}

	@Override
    protected void a(MovingObjectPosition movingobjectposition) {
		Location loc = this.getBukkitEntity().getLocation();
		hitLoc = loc;
		updateName();
		super.a(movingobjectposition);
    }
	
	private void updateName() {
		CraftEntity entity = this.getBukkitEntity();
		entity.setCustomName("projectile," + damage);
	}
}
