package de.Ste3et_C0st.DiceFurnitureMaker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import de.Ste3et_C0st.DiceFurnitureMaker.Commands.create;
import de.Ste3et_C0st.DiceFurnitureMaker.Commands.delete;
import de.Ste3et_C0st.DiceFurnitureMaker.Commands.download;
import de.Ste3et_C0st.DiceFurnitureMaker.Commands.edit;
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
	public HashMap<Project, Long> deleteMap = new HashMap<Project, Long>();
	public static main getInstance(){return instance;}
	public List<ProjektModel> getModelList(){return this.modelList;}
	public void onEnable(){
		if(getServer().getPluginManager().isPluginEnabled("FurnitureLib")==false){getServer().getPluginManager().disablePlugin(this); return;}
		lib = (FurnitureLib) Bukkit.getPluginManager().getPlugin("FurnitureLib");
		instance = this;
		if(lib.getDescription().getVersion().startsWith("1.7") || lib.getDescription().getVersion().startsWith("1.6")){
			Bukkit.getPluginManager().registerEvents(this, this);
			File folder = new File("plugins/FurnitureLib/plugin/DiceEditor/");
			if(folder.exists()){
				try{
				for(File file : folder.listFiles()){
					if(file!=null){
						if(file.exists()){
							if(file.isFile()){
								try {
									YamlConfiguration configuration = new YamlConfiguration();
									configuration.load(file);
									String name = file.getName().replaceAll(".yml", "");
									String nameOLD = name;
									PlaceableSide side = PlaceableSide.TOP;
									if(configuration.isSet(nameOLD + ".PlaceAbleSide")){side = PlaceableSide.valueOf(configuration.getString(nameOLD + ".PlaceAbleSide"));}
									new Project(nameOLD, this, new FileInputStream(file), side, ProjectLoader.class);
								} catch (Exception e) {e.printStackTrace();}
							}
						}
					}
				}}catch(NullPointerException ex){
					return;
				}
			}
			lib.registerPluginFurnitures(this);
			command.addCommand(new SubCommand("create", create.class, "§6You can create new Furnitures\nor clone a exsist furniture", "/furniture create <name> (cloneSource)", "§3/furniture create §e<name>"));
			command.addCommand(new SubCommand("edit", edit.class, "§6You can edit an own createt Model", "/furniture edit <name>", "§3/furniture edit §e<name>"));
			command.addCommand(new SubCommand("upload", upload.class, "§6You can upload the furniture Model", "/furniture upload <name>", "§3/furniture upload §e<name>"));
			command.addCommand(new SubCommand("download", download.class, "§6You can donload an furniture", "/furniture donwnload <id>", "§3/furniture download §e<id> §a(newName)"));
			command.addCommand(new SubCommand("update", update.class, "§6You can upload the Furniture Model", "/furniture update <name> <password> <id>", "§3/furniture update §e<name> <id> <password>"));
			command.addCommand(new SubCommand("delete", delete.class, "§6You can delete a Furniture Model", "/furniture delete <name>", "§3/furniture delete §e<name>"));
		}else{
			lib.send("FurnitureLib Version 1.6.x or 1.7.x not found");
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
		File file = new File("plugins/FurnitureLib/plugin/DiceEditor/", name+".yml");
		InputStream stream = new FileInputStream(file);
		Project pro = new Project(name, this, stream, side, ProjectLoader.class);
		pro.setEditorProject(true);
		pro.setModel(stream);
	}
}