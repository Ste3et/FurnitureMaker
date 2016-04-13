package de.Ste3et_C0st.DiceFurnitureMaker.Commands;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import de.Ste3et_C0st.DiceFurnitureMaker.ProjektModel;
import de.Ste3et_C0st.DiceFurnitureMaker.config;
import de.Ste3et_C0st.DiceFurnitureMaker.main;
import de.Ste3et_C0st.FurnitureLib.Command.command;
import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.main.Type.PlaceableSide;

public class download {

	public download(CommandSender sender, Command cmd, String arg2, String[] args){
		if(sender instanceof Player){
			if(args.length!=2){command.sendHelp((Player) sender);return;}
			if(args[0].equalsIgnoreCase("download")){
				try{
					if(!command.noPermissions(sender, "furniture.download")) return;
					String name = args[1];
					final URL url = new URL("http://dicecraft.de/API/Upload/download.php");
					sender.sendMessage("§7§m+-------------------§7[§2Download§7]§m--------------------+");
					sender.sendMessage("§6Download startet from: " + name);
					downLoadData(name, url, sender);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	private void downLoadData(final String name, final URL url, final CommandSender sender){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					boolean b = true;
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setRequestProperty("User-Agent", "FurnitureMaker/" + main.getInstance().getDescription().getVersion());
					connection.setDoOutput(true);
					connection.setDoInput(true);
					connection.setRequestMethod("POST");
					connection.setInstanceFollowRedirects(false);
					
					PrintStream stream = new PrintStream(connection.getOutputStream());
					stream.println("id=" + name);
					
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					
					stream.checkError();
					stream.flush();
					stream.close();
					
					String line = null;
					String config = "";
					String playerName = "";
					String projectName = "";
					
					int i = 0;
					while ((line = reader.readLine()) != null) {
						if(line.equalsIgnoreCase("#NOTEXIST") || line.equalsIgnoreCase("Invalid Page")){
							sender.sendMessage("§cProject Not Found");
						}else{
							switch (i) {
							case 0:config = line;
							case 1:playerName = line;
							case 2:projectName = line;
							}
							i++;
						}
					}
					
					if(config.equals("")){b=false;}
					if(playerName.equals("")){b=false;}
					if(projectName.equals("")){b=false;}
					
					if(b){
						sender.sendMessage("§6You have downloaded: " + projectName);
						sender.sendMessage("§6With the ID: " + name);
						sender.sendMessage("§6Createt from: " + playerName);
						add(config, playerName, projectName, sender);
					}
					
					connection.getInputStream().close();
					sender.sendMessage("§7§m+------------------------------------------------+");
				}catch(Exception e){
					
				}
			}
		}).start();
	}
	
	
	private void add(String config, String playerName, String project, CommandSender sender){
		try{
			config c = new config();
			FileConfiguration file = c.getConfig(project, "");
			byte[] by = Base64.decodeBase64(config);
			ByteArrayInputStream bin = new ByteArrayInputStream(by);
			NBTTagCompound compound = NBTCompressedStreamTools.read(bin);
			NBTTagCompound crafting = compound.getCompound("crafting");
			NBTTagCompound index = crafting.getCompound("index");
			NBTTagCompound lore = compound.getCompound("lore");
			String systemID = project;
			if(compound.hasKey("systemID")) systemID = compound.getString("systemID");
			file.set(project + ".name", compound.getString("name"));
			file.set(project + ".system-ID", systemID);
			file.set(project + ".material", compound.getInt("material"));
			file.set(project + ".glow", compound.getBoolean("glow"));
			List<String> loreText = new ArrayList<String>();
			for(Object s : lore.c()){
				loreText.add(lore.getString((String) s));
			}
			file.set(project + ".lore", loreText);
			
			file.set(project + ".crafting.disable", crafting.getBoolean("disable"));
			file.set(project + ".crafting.recipe", crafting.getString("recipe"));
			for(Object s : index.c()){
				file.set(project + ".crafting.index." + ((String) s), index.getString((String) s));
			}
			if(compound.hasKey("ArmorStands")){
				NBTTagCompound armorStands = compound.getCompound("ArmorStands");
				for(Object s : armorStands.c()){
					file.set(project+".ProjectModels.ArmorStands."+ ((String) s), armorStands.getString((String) s) + "");
				}
			}
			
			PlaceableSide side = PlaceableSide.TOP;
			if(compound.hasKey("PlaceAbleSide")){
				side = PlaceableSide.valueOf(compound.getString("PlaceAbleSide"));
			}
			
			file.set(project+".PlaceAbleSide", side.toString());
			
			if(compound.hasKey("Blocks")){
				NBTTagCompound blocks = compound.getCompound("Blocks");
				for(Object s : blocks.c()){
					String str = (String) s;
					if(blocks.hasKey(str)){
						NBTTagCompound block = blocks.getCompound(str);
						file.set(project+".ProjectModels.Block." + str + ".X-Offset", block.getDouble("X-Offset"));
						file.set(project+".ProjectModels.Block." + str + ".Y-Offset", block.getDouble("Y-Offset"));
						file.set(project+".ProjectModels.Block." + str + ".Z-Offset", block.getDouble("Z-Offset"));
						file.set(project+".ProjectModels.Block." + str + ".Type", block.getString("Type"));
						file.set(project+".ProjectModels.Block." + str + ".Data", block.getInt("Data"));
					}
				}
			}
			
			c.saveConfig(project, file, "");
			main.getInstance().registerProeject(project, side);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	private boolean isExist(String str){
		for(Project pro : main.getInstance().getFurnitureLib().getFurnitureManager().getProjects()){if(pro.getName().equalsIgnoreCase(str)){return true;}}
		for(ProjektModel model : main.getInstance().getModelList()){if(!model.isDelete()){if(model.getProjectName().equalsIgnoreCase(str)){return true;}}}
		return false;
	}
}
