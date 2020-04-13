package com.coding.sirjavlux.projectiles;

import net.minecraft.server.v1_15_R1.EntityLiving;
import net.minecraft.server.v1_15_R1.EntitySnowball;
import net.minecraft.server.v1_15_R1.ItemStack;
import net.minecraft.server.v1_15_R1.World;
import net.minecraft.server.v1_15_R1.MovingObjectPosition;

public class Projectile extends EntitySnowball {

    public Projectile(World world, EntityLiving e, ItemStack item, double speedMultiplier) {
		super(world, e);
		
		this.setItem(item);
		this.getBukkitEntity().setVelocity(this.getBukkitEntity().getVelocity().multiply(speedMultiplier));
	}

	@Override
    protected void a(MovingObjectPosition movingobjectposition) {
		System.out.print(movingobjectposition.getPos().x + " " + movingobjectposition.getPos().y + " " + movingobjectposition.getPos().z);
    	super.a(movingobjectposition);
    }
}
