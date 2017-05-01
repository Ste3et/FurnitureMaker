package de.Ste3et_C0st.DiceFurnitureMaker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import de.Ste3et_C0st.DiceFurnitureMaker.Commands.create;
import de.Ste3et_C0st.DiceFurnitureMaker.Commands.edit;
import de.Ste3et_C0st.DiceFurnitureMaker.Commands.importer;
import de.Ste3et_C0st.DiceFurnitureMaker.Commands.update;
import de.Ste3et_C0st.DiceFurnitureMaker.Commands.upload;
import de.Ste3et_C0st.FurnitureLib.Command.SubCommand;
import de.Ste3et_C0st.FurnitureLib.Command.command;
import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.ShematicLoader.ProjectLoader;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.Type.PlaceableSide;

public class main extends JavaPlugin implements Listener,CommandExecutor{
	
	private FurnitureLib lib;
	private static main instance;
	private List<ProjektModel> modelList = new ArrayList<ProjektModel>();
	public static main getInstance(){return instance;}
	public List<ProjektModel> getModelList(){return this.modelList;}
	public void onEnable(){
		if(getServer().getPluginManager().isPluginEnabled("FurnitureLib")==false){getServer().getPluginManager().disablePlugin(this); return;}
		lib = (FurnitureLib) Bukkit.getPluginManager().getPlugin("FurnitureLib");
		instance = this;
		if(lib.getDescription().getVersion().startsWith("1.7") || lib.getDescription().getVersion().startsWith("1.6") || lib.getDescription().getVersion().startsWith("1.8") || lib.getDescription().getVersion().startsWith("1.9")){
			Bukkit.getPluginManager().registerEvents(this, this);
			command.addCommand(new SubCommand("create", create.class, "§6You can create new Furnitures\n§6or clone a exsist furniture", "/furniture create <name> (cloneSource)", "§3/furniture create §e<name> §a(cloneSource)"));
			command.addCommand(new SubCommand("edit", edit.class, "§6You can edit an own createt Model", "/furniture edit <name>", "§3/furniture edit §e<name>"));
			command.addCommand(new SubCommand("upload", upload.class, "§6You can upload the furniture Model", "/furniture upload <name>", "§3/furniture upload §e<name>"));
			command.addCommand(new SubCommand("update", update.class, "§6You can upload the Furniture Model", "/furniture update <name> <password> <id>", "§3/furniture update §e<name> <id> <password>"));
			
			if(getServer().getBukkitVersion().startsWith("1.11")){
				command.addCommand(new SubCommand("import", importer.class, "§6Import ArmorStands into your Project Editor", "/furniture import <id>", "§3/furniture import §e<id>"));
			}else{
				System.out.println("Your Server deos not support the Import command for ArmorStands");
			}
		}else{
			lib.send("FurnitureLib Version > 1.6.x not found");
			lib.send("DiceFurniture deos not load");
		}

	}
	
	
	public void onDisable(){
		for(ProjektModel model : modelList){
			model.remove();
		}
	}
	
	public FurnitureLib getFurnitureLib(){return lib;}
	
	public void registerProeject(String name, PlaceableSide side) throws FileNotFoundException{
		File file = new File("plugins/FurnitureLib/Crafting/", name+".yml");
		InputStream stream = new FileInputStream(file);
		Project pro = new Project(name, FurnitureLib.getInstance(), stream, side, ProjectLoader.class);
		pro.setEditorProject(true);
		pro.setModel(stream);
	}
}