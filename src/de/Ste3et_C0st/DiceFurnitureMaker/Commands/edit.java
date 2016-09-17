package de.Ste3et_C0st.DiceFurnitureMaker.Commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.Ste3et_C0st.DiceFurnitureMaker.ProjektModel;
import de.Ste3et_C0st.FurnitureLib.Command.command;
import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.ShematicLoader.ProjectLoader;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public class edit {

	public edit(CommandSender sender, Command cmd, String arg2, String[] args){
		if(sender instanceof Player){
			if(args.length!=2){command.sendHelp((Player) sender);return;}
			if(args[0].equalsIgnoreCase("edit")){
				String name = args[1];
				Project project = isExist(name);
				if(project==null||!project.isEditorProject()){
					sender.sendMessage("§cThe Model deosnt exist !");
					return;
				}else{
					if(!command.noPermissions(sender, "furniture.edit")) return;
					Location loc = ((Player) sender).getLocation().getBlock().getLocation();
					loc.setYaw(FurnitureLib.getInstance().getLocationUtil().FaceToYaw(BlockFace.NORTH.getOppositeFace()));
					ObjectID id = new ObjectID(project.getName(), project.getPlugin().getName(), loc);
					if(project.isEditorProject()){new ProjectLoader(id, false);}else{FurnitureLib.getInstance().spawn(project,id);}
					ProjektModel model = new ProjektModel(project.getName(), (Player) sender);
					model.setObjectID(id);
					
					for(fEntity stand : id.getPacketList()){
						for(Player p : Bukkit.getOnlinePlayers()){
							if(!p.equals(sender)){
								stand.kill(p, true);
							}
						}
					}
					id.setPrivate(true);
					model.selectSingle(id.getPacketList().get(0));
					model.makeWall(loc, 10);
					model.giveItems((Player) sender);
					model.addItemPage1();
					model.setBlocks(id.getBlockList());
					id.getBlockList().clear();
					id.setSQLAction(SQLAction.REMOVE);
					return;
				}
			}
		}
		return;
	}
	
	private Project isExist(String s){
		for(Project project : FurnitureLib.getInstance().getFurnitureManager().getProjects()){
			if(project.getName().toLowerCase().equalsIgnoreCase(s.toLowerCase())){
				return project;
			}
		}
		return null;
	}
}
