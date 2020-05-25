package com.coding.sirjavlux.commands;

import java.util.Arrays;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.coding.sirjavlux.armors.Armor;
import com.coding.sirjavlux.armors.ArmorManager;
import com.coding.sirjavlux.consumables.Consumable;
import com.coding.sirjavlux.consumables.ConsumableManager;
import com.coding.sirjavlux.grenades.Grenade;
import com.coding.sirjavlux.grenades.GrenadeManager;
import com.coding.sirjavlux.melee.Melee;
import com.coding.sirjavlux.melee.MeleeManager;
import com.coding.sirjavlux.repair.Repair;
import com.coding.sirjavlux.repair.RepairManager;
import com.coding.sirjavlux.types.Ammo;
import com.coding.sirjavlux.types.Magazine;
import com.coding.sirjavlux.types.Weapon;
import com.coding.sirjavlux.utils.StringHandler;
import com.coding.sirjavlux.weapons.WeaponManager;

public class GiveCommand extends CommandManager {
	
	protected static enum types {
		AMMO,
		WEAPON,
		MAGAZINE,
		GRENADE,
		CONSUMABLE,
		ARMOR,
		MELEE,
		REPAIR
	}
	
	protected static void execute(CommandSender sender, Command cmd, String label, String[] args) {
		//data
		Player target = null;
		String type = null;
		String name = null;
		String amountStr = null;
		int amount = 1;
		
		//give (player) <type> <name> (amount)
		if (args.length < 3) {
			sender.sendMessage(ChatColor.GRAY + "Invalid args please use 'give (player) <type> <name> (amount)'");
			return;
		}
		else if (Bukkit.getPlayer(args[1]) == null && !(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "The player " + args[1] + " wasn't valid!");
			return;
		}
		//if player is valid
		else if (Bukkit.getPlayer(args[1]) != null) {
			if (args.length < 4) {
				sender.sendMessage(ChatColor.GRAY + "Invalid args please use 'give (player) <type> <name> (amount)'");
				return;
			}
			target = Bukkit.getPlayer(args[1]);
			type = args[2].toUpperCase();
			name = args[3];
			amountStr = args.length > 4 ? args[4] : "1";
		} 
		//if giving to yourself
		else {
			target = (Player) sender;
			type = args[1].toUpperCase();
			name = args[2];
			amountStr = args.length > 3 ? args[3] : "1";
		}
		
		if (!StringHandler.isNumber(amountStr)) {
			sender.sendMessage(ChatColor.GRAY + "The entered number " + ChatColor.RED + amountStr + ChatColor.GRAY + " wasn't a valid number!");
			return;
		} else {
			amount = Integer.parseInt(amountStr);
		}
		final String typeCheck = type;
		Stream<types> stream = Arrays.stream(types.values());
		boolean typeValid = stream.anyMatch(str -> str.toString().equalsIgnoreCase(typeCheck));
		if (!typeValid) {
			sender.sendMessage(ChatColor.GRAY + "The entered type " + ChatColor.RED + args[2] + ChatColor.GRAY + " wasn't valid, valid types are ammo, weapon, magazine");
			return;
		}
		switch (types.valueOf(type)) {
		case WEAPON:
			if (!WeaponManager.isWeapon(name)) {
				sender.sendMessage(ChatColor.GRAY + "The entered weapon name " + ChatColor.RED + name + ChatColor.GRAY + " wasn't valid!");
				return;
			}
			Weapon weapon = WeaponManager.getStoredWeapon(name);
			WeaponManager.givePlayerWeapon(target, weapon);
			break;
		case MAGAZINE:
			if (!WeaponManager.isMagazine(name)) {
				sender.sendMessage(ChatColor.GRAY + "The entered magazine name " + ChatColor.RED + name + ChatColor.GRAY + " wasn't valid!");
				return;
			}
			Magazine magazine = WeaponManager.getStoredMagazine(name);
			WeaponManager.givePlayerMagazine(target, magazine);
			break;
		case AMMO:
			if (!WeaponManager.isAmmunition(name)) {
				sender.sendMessage(ChatColor.GRAY + "The entered ammunition name " + ChatColor.RED + name + ChatColor.GRAY + " wasn't valid!");
				return;
			}
			Ammo ammo = WeaponManager.getStoredAmmo(name);
			WeaponManager.giveAmmo(target, ammo, amount);
			break;
		case GRENADE:
			if (!GrenadeManager.isGrenade(name)) {
				sender.sendMessage(ChatColor.GRAY + "The entered grenade name " + ChatColor.RED + name + ChatColor.GRAY + " wasn't valid!");
				return;
			}
			Grenade grenade = GrenadeManager.getStoredGrenade(name);
			GrenadeManager.giveGrenade(target, grenade, amount);
			break;
		case CONSUMABLE:
			if (!ConsumableManager.isConsumable(name)) {
				sender.sendMessage(ChatColor.GRAY + "The entered consumable name " + ChatColor.RED + name + ChatColor.GRAY + " wasn't valid!");
				return;
			}
			Consumable consumable = ConsumableManager.getStoredConsumable(name);
			ConsumableManager.giveConsumable(target, consumable, amount);
			break;
		case ARMOR:
			if (!ArmorManager.isArmor(name)) {
				sender.sendMessage(ChatColor.GRAY + "The entered armor name " + ChatColor.RED + name + ChatColor.GRAY + " wasn't valid!");
				return;
			}
			Armor armor = ArmorManager.getStoredArmor(name);
			ArmorManager.giveArmor(target, armor, amount);
			break;
		case MELEE:
			if (!MeleeManager.isMelee(name)) {
				sender.sendMessage(ChatColor.GRAY + "The entered melee weapon name " + ChatColor.RED + name + ChatColor.GRAY + " wasn't valid!");
				return;
			}
			Melee melee = MeleeManager.getStoredMelee(name);
			MeleeManager.giveMelee(target, melee, amount);
			break;
		case REPAIR:
			if (!RepairManager.isRepair(name)) {
				sender.sendMessage(ChatColor.GRAY + "The entered repair item name " + ChatColor.RED + name + ChatColor.GRAY + " wasn't valid!");
				return;
			}
			Repair repair = RepairManager.getStoredRepair(name);
			RepairManager.giveRepair(target, repair, amount);
			break;
		}
	}
}
