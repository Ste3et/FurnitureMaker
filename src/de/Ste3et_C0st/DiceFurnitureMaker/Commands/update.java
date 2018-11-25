package de.Ste3et_C0st.DiceFurnitureMaker.Commands;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.Ste3et_C0st.DiceFurnitureMaker.main;
import de.Ste3et_C0st.FurnitureLib.Command.command;
import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.Type.PlaceableSide;

public class update {

	public update(CommandSender sender, Command cmd, String arg2, String[] args){
		if(sender instanceof Player){
			if(args.length!=4){command.sendHelp((Player) sender);return;}
			if(args[0].equalsIgnoreCase("update")){
				try{
					if(!command.noPermissions(sender, "furniture.update")) return;
					final URL url = new URL("http://api.dicecraft.de/furniture/update.php");
					String name = args[1];
					Project project = isExist(name);
					sender.sendMessage("§7§m+--------------------§7[§2Update§7]§m---------------------+");
					sender.sendMessage("§6Update startet from: " + name);
					uploadData(project, sender, url, (Player) sender, args[2], args[3]);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	private void uploadData(final Project project, final CommandSender sender, final URL url, final Player p, final String password, final String id){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					URLConnection connection = (URLConnection) url.openConnection();
					connection.setRequestProperty("User-Agent", "FurnitureMaker/" + main.getInstance().getDescription().getVersion());
					connection.setDoOutput(true);
					connection.setDoInput(true);
					
					PrintStream stream = new PrintStream(connection.getOutputStream());
					String user = sender.getName();
					String config = getMetadata(project);
					if(config==null){
						sender.sendMessage("§cA internal error has been generated");
						sender.sendMessage("§7§m+------------------------------------------------+");
						return;
					}
					String projectString = project.getName();
					stream.println("user=" + user);
					stream.println("&config=" + config);
					stream.println("&projectString=" + projectString);
					stream.println("&uuid=" + p.getUniqueId().toString());
					stream.println("&password=" + password);
					stream.println("&id=" + id);
					stream.println("&spigot=1.13");
					
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					
					stream.checkError();
					stream.flush();
					stream.close();
					
					String line = null;
					while ((line = reader.readLine()) != null) {
						if(line.startsWith("@")){
							String[] split = line.split("#");
							((Player) sender).spigot().sendMessage(
							new ComponentBuilder(split[0].replaceFirst("@", "") + " ").append("§6Click Here")
													.event(new ClickEvent(ClickEvent.Action.OPEN_URL, split[1])).color(net.md_5.bungee.api.ChatColor.DARK_GREEN).create());
						}else{
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
						}
					}
					connection.getInputStream().close();
					sender.sendMessage("§7§m+------------------------------------------------+");
				}catch(Exception e){
					sender.sendMessage("§7§m+----------------------------------------------+");
					e.printStackTrace();
					return;
				}
			}
		}).start();
	}
	
	public String getHeader(YamlConfiguration file){
		return (String) file.getConfigurationSection("").getKeys(false).toArray()[0];
	}
	
	public String getMetadata(Project pro){
		try{
			YamlConfiguration config = new YamlConfiguration();
			config.load(new File("plugins/FurnitureLib/models/", pro.getName()+".dModel"));
			String header = getHeader(config);
			NBTTagCompound compound = new NBTTagCompound();
			compound.setString("displayName", config.getString(header + ".displayName"));
			compound.setString("system-ID", header);
			compound.setString("spawnMaterial", config.getString(header + ".spawnMaterial"));
			compound.setBoolean("itemGlowEffect", config.getBoolean(header + ".itemGlowEffect"));
			NBTTagCompound lore = new NBTTagCompound();
			int i  = 0;
			for(String str : config.getStringList(header + ".itemLore")){
				lore.setString(i+"", str);
				i++;
			}
			NBTTagCompound crafting = new NBTTagCompound();
			crafting.setBoolean("disable", config.getBoolean(header+".crafting.disable"));
			crafting.setString("recipe", config.getString(header+".crafting.recipe"));
			
			NBTTagCompound index = new NBTTagCompound();
			config.getConfigurationSection(header+".crafting.index").getKeys(false).forEach(letter -> {
				index.setString(letter, config.getString(header+".crafting.index." + letter));
			});

			crafting.set("index", index);
			compound.set("crafting", crafting);
			compound.set("itemLore", lore);
			
			if(config.isConfigurationSection(header+".projectData.entitys")){
				if(config.isSet(header+".projectData.entitys")){
					NBTTagCompound armorStands = new NBTTagCompound();
					config.getConfigurationSection(header+".projectData.entitys").getKeys(false).forEach(letter -> {
						armorStands.setString(letter, config.getString(header+".projectData.entitys."+letter));
					});
					compound.set("entitys", armorStands);
				}
			}
			
			if(config.isSet("placeAbleSide")){
				compound.setString("placeAbleSide", PlaceableSide.valueOf(config.getString("placeAbleSide")).toString());
			}else{
				compound.setString("placeAbleSide", PlaceableSide.TOP.toString());
			}
			
			if(config.isConfigurationSection(header+".projectData.blockList")){
				if(config.isSet(header+".projectData.blockList")){
					NBTTagCompound blockList = new NBTTagCompound();
					config.getConfigurationSection(header+".projectData.blockList").getKeys(false).stream().forEach(letter -> {
						NBTTagCompound block = new NBTTagCompound();
						block.setDouble("xOffset", config.getDouble(header+".projectData.blockList." + letter + ".xOffset"));
						block.setDouble("yOffset", config.getDouble(header+".projectData.blockList." + letter + ".yOffset"));
						block.setDouble("zOffset", config.getDouble(header+".projectData.blockList." + letter + ".zOffset"));
						block.setString("material", config.getString(header+".projectData.blockList." + letter + ".material"));
						blockList.set(letter, block);
					});
					compound.set("blockList", blockList);
				}
			}
			return Base64.getUrlEncoder().encodeToString(getByte(compound));
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	 public byte[] getByte(NBTTagCompound compound)
	  {
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    try
	    {
	      NBTCompressedStreamTools.write(compound, out);
	      out.close();
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	      return new byte[0];
	    }
	    return out.toByteArray();
	  }
	
	private Project isExist(String s){
		for(Project project : FurnitureLib.getInstance().getFurnitureManager().getProjects()){
			if(project.getName().equalsIgnoreCase(s)){
				return project;
			}
		}
		return null;
	}
	
}
