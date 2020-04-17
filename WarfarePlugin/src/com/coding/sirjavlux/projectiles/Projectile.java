package com.coding.sirjavlux.projectiles;

import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.entity.Player;

import com.coding.sirjavlux.types.Ammo;
import com.coding.sirjavlux.types.Weapon;

import net.minecraft.server.v1_15_R1.EntityLiving;
import net.minecraft.server.v1_15_R1.EntitySnowball;
import net.minecraft.server.v1_15_R1.ItemStack;
import net.minecraft.server.v1_15_R1.World;
import net.minecraft.server.v1_15_R1.MovingObjectPosition;

public class Projectile extends EntitySnowball {

	Weapon weapon;
	Ammo ammo;
	
    public Projectile(World world, EntityLiving e, ItemStack item, double speedMultiplier, Weapon weapon, Ammo ammo) {
		super(world, e);
		
		this.setItem(item);
		this.weapon = weapon;
		this.ammo = ammo;
		CraftEntity entity = this.getBukkitEntity();
		Player p = (Player) this.getShooter().getBukkitEntity();
		entity.setVelocity(p.getEyeLocation().getDirection().multiply(speedMultiplier));
		entity.setCustomNameVisible(false);
		updateName();
	}

	@Override
    protected void a(MovingObjectPosition movingobjectposition) {
		super.a(movingobjectposition);
    }
	
	private void updateName() {
		CraftEntity entity = this.getBukkitEntity();
		entity.setCustomName("projectile," + weapon.getName() + "," + ammo.getName());
	}
}
