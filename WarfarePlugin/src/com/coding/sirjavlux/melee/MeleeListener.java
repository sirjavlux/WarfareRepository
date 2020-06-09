package com.coding.sirjavlux.melee;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import com.coding.sirjavlux.armors.Armor;
import com.coding.sirjavlux.armors.ArmorManager;
import com.coding.sirjavlux.events.MeleeHitEvent;
import com.coding.sirjavlux.events.WarfareDeathEvent;
import com.coding.sirjavlux.events.WarfareDeathEvent.WarfareDeathCause;

public class MeleeListener implements Listener {

	@EventHandler
	public void damageEvent(EntityDamageByEntityEvent e) {
		Entity damager = e.getDamager();
		Entity entity = e.getEntity();
		if (damager instanceof LivingEntity && entity instanceof LivingEntity) {
			ItemStack hand = ((LivingEntity) damager).getEquipment().getItemInMainHand();
			if (hand != null) {
				if (MeleeManager.isMelee(hand)) {
					Melee melee = MeleeManager.getMeleeFromItem(hand);
					MeleeHitEvent hitEvent = new MeleeHitEvent(melee, (LivingEntity) entity, (LivingEntity) damager);
					if (!hitEvent.isCancelled()) {
						double damage = hitEvent.getDamage();
						e.setDamage(damage);
						//damage armor if custom armor
						ItemStack hitPiece = hitEvent.getHitArmorPiece();
						Armor armor = hitEvent.getHitArmor();
						if (hitPiece != null && armor == null) {
							if (hitPiece.getItemMeta() instanceof Damageable) {
								double armorDamage = melee.getArmorDamage();
								Damageable damageableItem = ((Damageable) hitPiece.getItemMeta());
								int maxDurability = hitPiece.getType().getMaxDurability();
								int durability = maxDurability - damageableItem.getDamage();
								int finalDurability = (int) (durability - armorDamage < 0 ? 0 : durability - armorDamage);
								damageableItem.setDamage(maxDurability - finalDurability);
								hitPiece.setItemMeta((ItemMeta) damageableItem);
								switch(hitEvent.getHitBodyPart()) {
								case Chest: ((LivingEntity) entity).getEquipment().setChestplate(hitPiece);
									break;
								case Head: ((LivingEntity) entity).getEquipment().setHelmet(hitPiece);
									break;
								case Leg: ((LivingEntity) entity).getEquipment().setLeggings(hitPiece);
									break;
								}
							}	
						} else if (hitPiece != null && armor != null) {
							double armorDamage = melee.getArmorDamage();
							int durability = ArmorManager.getDurability(hitPiece);
							int finalDurability = (int) (durability - armorDamage < 0 ? 0 : durability - armorDamage);
							switch(hitEvent.getHitBodyPart()) {
							case Chest: ((LivingEntity) entity).getEquipment().setChestplate(MeleeManager.setDurability(hitPiece, finalDurability));
								break;
							case Head: ((LivingEntity) entity).getEquipment().setHelmet(MeleeManager.setDurability(hitPiece, finalDurability));
								break;
							case Leg: ((LivingEntity) entity).getEquipment().setLeggings(MeleeManager.setDurability(hitPiece, finalDurability));
								break;
							}
						}
						//lower sharpness
						int durability = MeleeManager.getDurability(hand);
						int finalDurability = durability - 1 < 0 ? 0 : durability - 1;
						ItemStack newItem = MeleeManager.setDurability(hand, finalDurability);
						((LivingEntity) damager).getEquipment().setItemInMainHand(newItem);
					}
				}
			}
			if (e.getDamage() >= ((LivingEntity) entity).getHealth()) {
				WarfareDeathEvent wEvent = new WarfareDeathEvent((LivingEntity) entity, (LivingEntity) damager, WarfareDeathCause.Melee);
				Bukkit.getPluginManager().callEvent(wEvent);
				if (!wEvent.isCancelled() && !wEvent.getDeathMessage().isEmpty()) for (Player p : Bukkit.getOnlinePlayers()) p.sendMessage(wEvent.getDeathMessage());
			}
		}
	}
}
