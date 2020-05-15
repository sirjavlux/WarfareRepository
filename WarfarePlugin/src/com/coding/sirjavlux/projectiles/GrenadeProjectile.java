package com.coding.sirjavlux.projectiles;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.entity.Player;

import com.coding.sirjavlux.effects.Effect;
import com.coding.sirjavlux.effects.ExplosiveEffect;
import com.coding.sirjavlux.effects.FireEffect;
import com.coding.sirjavlux.effects.FlashEffect;
import com.coding.sirjavlux.effects.SmokeEffect;
import com.coding.sirjavlux.grenades.Grenade;

import net.minecraft.server.v1_15_R1.EntityLiving;
import net.minecraft.server.v1_15_R1.EntitySnowball;
import net.minecraft.server.v1_15_R1.ItemStack;
import net.minecraft.server.v1_15_R1.MovingObjectPosition;

public class GrenadeProjectile extends EntitySnowball {

	World bukkitWorld;
	double speed;
	Grenade grenade;
	Player p;
	
	public GrenadeProjectile(net.minecraft.server.v1_15_R1.World world, EntityLiving e, ItemStack item, Grenade grenade) {
		super(world, e);
		CraftEntity entity = this.getBukkitEntity();
		this.p = (Player) this.getShooter().getBukkitEntity();
		this.setItem(item);
		this.bukkitWorld = p.getWorld();
		this.speed = grenade.getSpeed();
		this.grenade = grenade;
		entity.setVelocity(p.getEyeLocation().getDirection().normalize().multiply(speed));
		entity.setCustomNameVisible(false);
	}

	@Override
    protected void a(MovingObjectPosition movingobjectposition) {
		Location hitLoc = new Location(bukkitWorld, movingobjectposition.getPos().getX(), movingobjectposition.getPos().getY(), movingobjectposition.getPos().getZ());
		hitLoc.getWorld().playSound(hitLoc, grenade.getExplodeSound(), 5, 1);
		switch (grenade.getType()) {
		case Fire: 
			Effect effect = new FireEffect(hitLoc, grenade.getDuration(), grenade.getExplosionDamage(), grenade.getExplosionRange(), 0.2, grenade.getFireTicks(), (int) (4d * grenade.getExplosionRange()), grenade.getExplosionDamageDrop(), p, this.getBukkitEntity().getVelocity());
			effect.playEffect();
			break;
		case Flash: 
			effect = new FlashEffect(hitLoc, grenade.getDuration(), grenade.getExplosionDamage(), grenade.getExplosionRange(), grenade.getExplosionRange(), grenade.getFireTicks(), (int) (4d * grenade.getExplosionRange()), this.getBukkitEntity().getVelocity());
			effect.playEffect();
			break;
		case Smoke: 
			effect = new SmokeEffect(hitLoc, grenade.getDuration(), grenade.getExplosionDamage(), grenade.getExplosionRange(), grenade.getExplosionRange() * 0.72, grenade.getFireTicks(), this.getBukkitEntity().getVelocity());
			effect.playEffect();
			break;
		case Explosion:
			effect = new ExplosiveEffect(hitLoc, grenade.getExplosionDamage(), grenade.getExplosionRange(), grenade.getExplosionRange(), grenade.getFireTicks(), grenade.getExplosionDamageDrop(), p);
			effect.playEffect();
			break;
		}
		this.killEntity();
		return;
    }
}
