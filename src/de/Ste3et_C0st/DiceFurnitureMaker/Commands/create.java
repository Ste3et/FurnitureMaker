package de.Ste3et_C0st.DiceFurnitureMaker.Commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.Ste3et_C0st.DiceFurnitureMaker.ProjektModel;
import de.Ste3et_C0st.DiceFurnitureMaker.main;
import de.Ste3et_C0st.FurnitureLib.Command.command;
import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.ShematicLoader.ProjectLoader;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public class create{
	
	public create(CommandSender sender, Command cmd, String arg2, String[] args){
		if(sender instanceof Player){
			if(args.length==2){
				if(args[0].equalsIgnoreCase("create")){
					String name = args[1];
					if(isExist(name)){
						sender.sendMessage("§cThe Project already exist !");
						return;
					}else{
						if(!command.noPermissions(sender, "furniture.create")) return;
						if(!name.matches("[a-zA-Z.? ]*")){sender.sendMessage("§cYou insert a invalid name please use onle the chars a-z");return;}
						if(isInEditor((Player) sender)){sender.sendMessage("You are create at the time a Furniture"); return;}
	 					sender.sendMessage("§6You started Project: §2" + name);
						ProjektModel model = new ProjektModel(name, (Player) sender);
						Location loc = ((Player) sender).getLocation().getBlock().getLocation();
						loc.setYaw(((Player) sender).getLocation().getYaw());
						model.makeWall(loc, 10);
						model.giveItems((Player) sender);
						main.getInstance().getModelList().add(model);
						return;
					}
				}else{
					command.sendHelp((Player) sender);return;
				}
			}else if(args.length==3){
				if(args[0].equalsIgnoreCase("create")){
					if(!command.noPermissions(sender, "furniture.create.clone")) return;
					String name = args[1];
					String cloneSource = args[2];
					if(!isExist(cloneSource)){
						sender.sendMessage("§cThe Project doesnt exist !");
						return;
					}else{
						if(isExist(name)){
							sender.sendMessage("§cThe Project already exist !");
							return;
						}else{
							if(name.equalsIgnoreCase(cloneSource)){
								sender.sendMessage("§cYou entered the same name");
								return;
							}
					        if(!name.matches("[a-zA-Z.? ]*")){sender.sendMessage("§cYou insert a invalid name please use onle the chars a-z");return;}
							if(!command.noPermissions(sender, "furniture.create")) return;
							if(isInEditor((Player) sender)){sender.sendMessage("You are create at the time a Furniture"); return;}
		 					sender.sendMessage("§6You started Project: §2" + name);
							Project project = getProject(cloneSource);
		 					Location loc = ((Player) sender).getLocation().getBlock().getLocation();
							loc.setYaw(FurnitureLib.getInstance().getLocationUtil().FaceToYaw(BlockFace.NORTH.getOppositeFace()));
							ObjectID id = new ObjectID(project.getName(), project.getPlugin().getName(), loc);
							if(project.isEditorProject()){new ProjectLoader(id);}else{FurnitureLib.getInstance().spawn(project,id);}
							ProjektModel model = new ProjektModel(name, (Player) sender);
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
						}
					}
				}else{
					command.sendHelp((Player) sender);return;
				}
			}else{
				command.sendHelp((Player) sender);return;
			}
		}
		return;
	}
	
	private Project getProject(String s){
		for(Project project : FurnitureLib.getInstance().getFurnitureManager().getProjects()){
			if(project.getName().equalsIgnoreCase(s)){
				return project;
			}
		}
		return null;
	}
	
	private boolean isInEditor(Player player){
		for(ProjektModel model : main.getInstance().getModelList()){
			if(model.getPlayer()!=null){
				if(model.getPlayer().equals(player)){
					return true;
				}
			}
		}
		return false;
	}
	
	
	private boolean isExist(String str){
		for(Project pro : main.getInstance().getFurnitureLib().getFurnitureManager().getProjects()){if(pro.getName().equalsIgnoreCase(str)){return true;}}
		for(ProjektModel model : main.getInstance().getModelList()){if(!model.isDelete()){if(model.getProjectName().equalsIgnoreCase(str)){return true;}}}
		return false;
	}
}