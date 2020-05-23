package com.planetgallium.kitpvp.command;

import com.planetgallium.kitpvp.util.Resources;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitCommand implements CommandExecutor {

	private Resources resources;
	
	public KitCommand(Resources resources) {
		this.resources = resources;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		
		if (command.getName().equalsIgnoreCase("ckit")) {
			
			if (sender instanceof Player) {
					
				Player p = (Player) sender;
					
				if (args.length == 0) {

					p.performCommand("kp menu");
					return true;
					
				} else if (args.length == 1) {
					
					p.performCommand("kp kit " + args[0]);
					return true;
					
				} else if (args.length == 2) {

					p.performCommand("kp kit " + args[0] + " " + args[1]);
					return true;

				}
				
			}
			
		}
		
		return false;
		
	}
	
}
