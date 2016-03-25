package de.Ste3et_C0st.DiceFurnitureMaker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import de.Ste3et_C0st.FurnitureLib.Command.command;
import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.Type.PlaceableSide;

public class main extends JavaPlugin implements Listener,CommandExecutor{
	
	private FurnitureLib lib;
	private static main instance;
	private List<ProjektModel> modelList = new ArrayList<ProjektModel>();
	
	public static main getInstance(){return instance;}
	
	public void onEnable(){
		if(getServer().getPluginManager().isPluginEnabled("FurnitureLib")==false){getServer().getPluginManager().disablePlugin(this); return;}
		lib = (FurnitureLib) Bukkit.getPluginManager().getPlugin("FurnitureLib");
		instance = this;
		Bukkit.getPluginManager().registerEvents(this, this);
		
		File folder = new File("plugins/FurnitureLib/plugin/DiceEditor/");
		if(folder.exists()){
			for(File file : folder.listFiles()){
				if(file.exists()){
					try {
						new Project(file.getName().replaceAll(".yml", ""), this, new FileInputStream(file), PlaceableSide.TOP, ProjectLoader.class);
					} catch (Exception e) {e.printStackTrace();}
				}
			}
		}
		lib.registerPluginFurnitures(this);
	}
	
	
	public void onDisable(){
		for(ProjektModel model : modelList){
			model.remove();
		}
		
	}
	
	public FurnitureLib getFurnitureLib(){return lib;}
	
	public void registerProeject(String name) throws FileNotFoundException{
		InputStream stream = new FileInputStream(new File("plugins/FurnitureLib/plugin/DiceEditor/", name+".yml"));
		Project pro = new Project(name, this, stream, PlaceableSide.TOP, ProjectLoader.class);
		pro.setEditorProject(true);
		pro.setModel(stream);
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if(sender instanceof Player){
			if(cmd.getName().equalsIgnoreCase("model")){
				if(args.length==0){return true;}
				if(args.length==2){
					if(args[0].equalsIgnoreCase("create")){
						String name = args[1];
						if(isExist(name)){
							sender.sendMessage("The Project already exist !");
							return true;
						}else{
							if(!command.noPermissions(sender, "model.create")) return true;
							sender.sendMessage("You started Project: " + name);
							ProjektModel model = new ProjektModel(name, (Player) sender);
							Location loc = ((Player) sender).getLocation().getBlock().getLocation();
							loc.setYaw(((Player) sender).getLocation().getYaw());
							model.makeWall(loc, 10);
							model.giveItems((Player) sender);
							modelList.add(model);
							return true;
						}
					}
				}
			}
		}
		return false;
		
	}
	private boolean isExist(String str){
		for(Project pro : getFurnitureLib().getFurnitureManager().getProjects()){if(pro.getName().equalsIgnoreCase(str)){return true;}}
		for(ProjektModel model : modelList){if(!model.isDelete()){if(model.getProjectName().equalsIgnoreCase(str)){return true;}}}
		return false;
	}
}