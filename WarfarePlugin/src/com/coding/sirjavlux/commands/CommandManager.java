package com.coding.sirjavlux.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandManager implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		//help command
		if (args.length == 0) {
			
		} 
		//other command
		else {
			StringBuilder input = new StringBuilder(label);
			for (String str : args) {
				input.append(" " + str);
			}
			String command = args[0].toUpperCase();
			switch (command) {
			case "GIVE": 
				if (sender.hasPermission("wf.give")) GiveCommand.execute(sender, cmd, label, args);
				else sender.sendMessage(ChatColor.RED + "You didn't have permission to use this command!");
				break;
			default: sender.sendMessage(ChatColor.GRAY + "The entered command " + ChatColor.RED + input + ChatColor.GRAY + " wasn't valid, try wf for help.");
				break;
			}
		}
		return false;
	}

}
