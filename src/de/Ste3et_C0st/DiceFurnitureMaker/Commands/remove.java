package de.Ste3et_C0st.DiceFurnitureMaker.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.Ste3et_C0st.FurnitureLib.Command.command;

public class remove {

	public remove(CommandSender sender, Command cmd, String arg2, String[] args){
		if(sender instanceof Player){
			if(args.length!=2){command.sendHelp((Player) sender);return;}
			if(args[0].equalsIgnoreCase("remove")){
				
			}
		}}
	
}
