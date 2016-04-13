package de.Ste3et_C0st.DiceFurnitureMaker.Commands;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
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
import de.Ste3et_C0st.FurnitureLib.Utilitis.JsonBuilder;
import de.Ste3et_C0st.FurnitureLib.Utilitis.JsonBuilder.ClickAction;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class update {

	public update(CommandSender sender, Command cmd, String arg2, String[] args){
		if(sender instanceof Player){
			if(args.length!=4){command.sendHelp((Player) sender);return;}
			if(args[0].equalsIgnoreCase("update")){
				try{
					if(!command.noPermissions(sender, "furniture.update")) return;
					final URL url = new URL("http://dicecraft.de/API/Upload/update.php");
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
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setRequestProperty("User-Agent", "FurnitureMaker/" + main.getInstance().getDescription().getVersion());
					connection.setDoOutput(true);
					connection.setDoInput(true);
					connection.setRequestMethod("POST");
					connection.setInstanceFollowRedirects(false);
					
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
					
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					
					stream.checkError();
					stream.flush();
					stream.close();
					
					String line = null;
					while ((line = reader.readLine()) != null) {
						if(line.startsWith("@")){
							String[] split = line.split("#");
							new JsonBuilder(split[0].replaceFirst("@", "") + " ").withColor(ChatColor.GOLD).withText("Click Here")
													 .withClickEvent(ClickAction.OPEN_URL, split[1])
													 .withColor(ChatColor.DARK_GREEN).sendJson((Player) sender);
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
			config.load(new File("plugins/FurnitureLib/plugin/DiceEditor/", pro.getName()+".yml"));
			String header = getHeader(config);
			NBTTagCompound compound = new NBTTagCompound();
			compound.setString("name", config.getString(header + ".name"));
			compound.setString("systemID", header);
			compound.setInt("material", config.getInt(header + ".material"));
			compound.setBoolean("glow", config.getBoolean(header + ".glow"));
			NBTTagCompound lore = new NBTTagCompound();
			int i  = 0;
			for(String str : config.getStringList(header + ".lore")){
				lore.setString(i+"", str);
				i++;
			}
			NBTTagCompound crafting = new NBTTagCompound();
			crafting.setBoolean("disable", config.getBoolean(header+".crafting.disable"));
			crafting.setString("recipe", config.getString(header+".crafting.recipe"));
			
			NBTTagCompound index = new NBTTagCompound();
			for(String str : config.getConfigurationSection(header+".crafting.index").getKeys(false)){
				index.setString(str, config.getString(header+".crafting.index." + str));
			}
			crafting.set("index", index);
			compound.set("crafting", crafting);
			compound.set("lore", lore);
			
			if(config.isConfigurationSection(header+".ProjectModels.ArmorStands")){
				if(config.isSet(header+".ProjectModels.ArmorStands")){
					NBTTagCompound armorStands = new NBTTagCompound();
					for(String str : config.getConfigurationSection(header+".ProjectModels.ArmorStands").getKeys(false)){
						armorStands.setString(str, config.getString(header+".ProjectModels.ArmorStands."+str));
					}
					compound.set("ArmorStands", armorStands);
				}
			}

			
			if(config.isConfigurationSection(header+".ProjectModels.Block")){
				if(config.isSet(header+".ProjectModels.Block")){
					NBTTagCompound blockList = new NBTTagCompound();
					for(String str : config.getConfigurationSection(header+".ProjectModels.Block").getKeys(false)){
						NBTTagCompound block = new NBTTagCompound();
						block.setDouble("X-Offset", config.getDouble(header+".ProjectModels.Block." + str + ".X-Offset"));
						block.setDouble("Y-Offset", config.getDouble(header+".ProjectModels.Block." + str + ".Y-Offset"));
						block.setDouble("Z-Offset", config.getDouble(header+".ProjectModels.Block." + str + ".Z-Offset"));
						block.setString("Type", config.getString(header+".ProjectModels.Block." + str + ".Type"));
						block.setInt("Data", config.getInt(header+".ProjectModels.Block." + str + ".Data"));
						blockList.set(str, block);
					}
					compound.set("Blocks", blockList);
				}
			}
			return Base64.encodeBase64URLSafeString(getByte(compound));
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
