package de.Ste3et_C0st.DiceFurnitureMaker.Commands;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.Ste3et_C0st.DiceFurnitureMaker.ProjektModel;
import de.Ste3et_C0st.DiceFurnitureMaker.ProjektTranslater;
import de.Ste3et_C0st.DiceFurnitureMaker.main;
import de.Ste3et_C0st.FurnitureLib.Command.command;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class importer {

	public importer(CommandSender sender, Command cmd, String arg2, String[] args){
		if(args.length!=2){command.sendHelp((Player) sender);return;}
		if(args[0].equalsIgnoreCase("import")){
			String id = args[1];
			if(!isInEditor((Player) sender)){ sender.sendMessage("You are not in a ModelEditor");return;}
			try {
				if(!command.noPermissions(sender, "furniture.import")) return;
				final URL url = new URL("http://api.dicecraft.de/furniture/import.php");
				sender.sendMessage("§7§m+-------------------§7[§2Download§7]§m--------------------+");
				sender.sendMessage("§6Download startet from: " + id);
				downLoadData(id, url, sender);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}

	private void downLoadData(final String name, final URL url, final CommandSender sender){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					boolean b = true;
					URLConnection connection = (URLConnection) url.openConnection();
					connection.setRequestProperty("User-Agent", "FurnitureMaker/" + FurnitureLib.getInstance().getDescription().getVersion());
					connection.setDoOutput(true);
					connection.setDoInput(true);
					
					PrintStream stream = new PrintStream(connection.getOutputStream());
					stream.println("id=" + name);
					
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					
					stream.checkError();
					stream.flush();
					stream.close();
					
					String line = null;
					String text = "";
					
					int i = 0;
					while ((line = reader.readLine()) != null) {
						if(text.equalsIgnoreCase("#NOTEXIST") || text.equalsIgnoreCase("Invalid Page")){
							sender.sendMessage("§cYour inserted id is wrong or the id is not more avaible");
							b = false;
							break;
						}else{
							switch (i) {
							case 0:text = line;
							}
							i++;
						}
					}
					
					if(text.equalsIgnoreCase("#NOTEXIST") || text.equalsIgnoreCase("Invalid Page") || text.equalsIgnoreCase("")){
						sender.sendMessage("§cYour inserted id is wrong or the id is not more avaible");
						b = false;
					}
					
					if(b){
						sender.sendMessage("§2You have downloaded: " + name);
						ProjektModel model = getEditor((Player) sender);
						byte[] by = Base64.decodeBase64(text);
						Location loc = model.getStartLocation().clone();
						loc = loc.getBlock().getLocation();
						loc = loc.add(.5, 3, .5);
						loc.setYaw(0);
						new ProjektTranslater(loc, model, new String(by, "UTF-8") );
					}
					
					connection.getInputStream().close();
					sender.sendMessage("§7§m+------------------------------------------------+");
				}catch(Exception e){
					sender.sendMessage("§cThe FurnitureMaker Downloader have an Exception");
					sender.sendMessage("§cPlease contact the Developer");
					e.printStackTrace();
				}
			}
		}).start();
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
	
	private ProjektModel getEditor(Player player){
		for(ProjektModel model : main.getInstance().getModelList()){
			if(model.getPlayer()!=null){
				if(model.getPlayer().equals(player)){
					return model;
				}
			}
		}
		return null;
	}
	
}
