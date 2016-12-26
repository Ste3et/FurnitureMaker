package de.Ste3et_C0st.DiceFurnitureMaker;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTBase;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagList;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagString;
import de.Ste3et_C0st.FurnitureLib.Utilitis.LocationUtil;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.Type.BodyPart;
import de.Ste3et_C0st.FurnitureLib.main.entity.Relative;
import de.Ste3et_C0st.FurnitureLib.main.entity.fArmorStand;

public class ProjektTranslater {
	
	String version = FurnitureLib.getInstance().getBukkitVersion();
	Class<?> mojangParser = null;
	Class<?> nbtCompressedStreamTools = null;
	Class<?> compound = null;
	Class<?> craftItemStack = null;
	Class<?> materialKey = null;
	Class<?> item = null;
	FurnitureLib lib = FurnitureLib.getInstance();
	LocationUtil util = lib.getLocationUtil();
	
	public ProjektTranslater(Location startLocation, ProjektModel model, String s){
		try{
			mojangParser = Class.forName("net.minecraft.server."+version+".MojangsonParser");
			nbtCompressedStreamTools = Class.forName("net.minecraft.server."+version+".NBTCompressedStreamTools");
			compound = Class.forName("net.minecraft.server."+version+".NBTTagCompound");
			craftItemStack = Class.forName("org.bukkit.craftbukkit."+version+".inventory.CraftItemStack");
			materialKey = Class.forName("net.minecraft.server."+version+".MinecraftKey");
			item = Class.forName("net.minecraft.server."+version+".Item");
			s = s.toLowerCase();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			OutputStream stream = reflectMojangParser(s.substring(s.indexOf("{"), s.length()), out, version);
			if(stream!=null){
				out = (ByteArrayOutputStream) stream;
			}
			ByteArrayInputStream input = new ByteArrayInputStream(out.toByteArray());
			NBTTagCompound compound = NBTCompressedStreamTools.read(input);
			getCommands("summon", compound);
			
			if(!commands.isEmpty()){
				for(NBTTagCompound cmd : commands){
					NBTTagString string = (NBTTagString) cmd.get("command");
					String clearCMD = string.toString();
					clearCMD = clearCMD.substring(1, clearCMD.length()-1);
					if(clearCMD.startsWith("summon armor_stand")){
						spawnArmorStand(clearCMD, model, startLocation);
					}else if(clearCMD.startsWith("setblock")){
						setBlock(clearCMD, model, startLocation);
					}
				}
			}else{
				if(s.startsWith("/summon armor_stand")) s = s.replaceFirst("/", "");
				startLocation = startLocation.subtract(0, 3, 0);
				spawnArmorStand(s, model, startLocation);
			}
		}catch (Exception e){
			e.printStackTrace();
			model.getPlayer().sendMessage("§cYou have download a wrong formated string");
			return;
		}
	}
	public List<NBTTagCompound> commands = new ArrayList<NBTTagCompound>();
	
	public void getCommands(String str, NBTTagCompound compound){
		for(Object object : compound.c()){
			if(object instanceof String){
				String name = (String) object;
				NBTBase base = compound.get(name);
				if(base instanceof NBTTagList){
					NBTTagList list = (NBTTagList) base;
					for(int i = 0; i<list.size(); i++){
						NBTTagCompound compound2 = list.get(i);
						if(compound2.hasKey("command")){
							commands.add(compound2);
						}else{
							getCommands(str, compound2);
						}
					}
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void setBlock(String str, final ProjektModel model, Location StartLocation) throws Exception{
		str = str.replace("setblock ", "");
		String[] args = str.split(" ");
		String xOffsetStr = args[0];
		String yOffsetStr = args[1];
		String zOffsetStr = args[2];
		double xOffset = eval(xOffsetStr);
		double yOffset = eval(yOffsetStr);
		double zOffset = eval(zOffsetStr);
		final Relative relative = new Relative(StartLocation, xOffset, yOffset, zOffset, BlockFace.EAST);
		str = str.replace(xOffsetStr + " ", "");
		str = str.replace(yOffsetStr + " ", "");
		str = str.replace(zOffsetStr + " ", "");
		
		try{
			final Material m = Bukkit.getUnsafe().getMaterialFromInternalName(args[3]);
			String byteStr = "0";
			
			try{
			if(args[4] != null){
					String stl = args[4].replaceAll("[^\\d.]", "");
					byteStr = Integer.parseInt(stl) + "";
			}}catch(Exception ex){}
			
			final int i = Integer.parseInt(byteStr);
			Bukkit.getScheduler().scheduleSyncDelayedTask(lib, new Runnable() {
				@Override
				public void run() {
					relative.getSecondLocation().getBlock().setType(m);
					relative.getSecondLocation().getBlock().setData((byte) i);
					model.blockList.add(relative.getSecondLocation().getBlock());
				}
			});

		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	
	public void spawnArmorStand(String str, final ProjektModel model, Location StartLocation) throws Exception{
		str = str.replace("summon armorstand ", "");
		str = str.replace("summon armor_stand ", "");
		String[] args = str.split(" ");
		String xOffsetStr = args[0];
		String yOffsetStr = args[1];
		String zOffsetStr = args[2];
		double xOffset = eval(xOffsetStr);
		double yOffset = eval(yOffsetStr);
		double zOffset = eval(zOffsetStr);
		Relative relative = new Relative(StartLocation, xOffset, yOffset, zOffset, BlockFace.EAST);
		str = str.replace(xOffsetStr + " ", "");
		str = str.replace(yOffsetStr + " ", "");
		str = str.replace(zOffsetStr + " ", "");

		String CustomName = "";
		boolean CustomNameVisible = false;
		boolean ShowArms = false;
		boolean Marker = true;
		boolean Glowing = false;
		boolean Small = false;
		boolean Invisible = false;
		boolean NoBasePlate = true;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		OutputStream stream = reflectMojangParser(str.substring(str.indexOf("{"), str.length()), out, version);
		if(stream!=null){
			out = (ByteArrayOutputStream) stream;
		}
		ByteArrayInputStream input = new ByteArrayInputStream(out.toByteArray());
		NBTTagCompound compound = NBTCompressedStreamTools.read(input);
		if(compound.hasKey("customnamevisible")){CustomNameVisible = (compound.getInt("customnamevisible")==1);}
		if(compound.hasKey("showarms")){ShowArms = (compound.getInt("showarms")==1);}
		if(compound.hasKey("small")){Small = (compound.getInt("small")==1);}
		if(compound.hasKey("marker")){Marker = (compound.getInt("marker")==0);}
		if(compound.hasKey("glowing")){Glowing = (compound.getInt("glowing")==1);}
		if(compound.hasKey("invisible")){Invisible = (compound.getInt("invisible")==1);}
		if(compound.hasKey("nobaseplate")){NoBasePlate = (compound.getInt("nobaseplate")==0);}
		if(compound.hasKey("customName")){CustomName = ChatColor.translateAlternateColorCodes('&', compound.getString("customName"));}
		final fArmorStand stand = (fArmorStand) model.createEntity(relative, EntityType.ARMOR_STAND);
		Location loc = stand.getLocation();
		stand.setLocation(loc);
		stand.setArms(ShowArms).setSmall(Small).setBasePlate(NoBasePlate).setMarker(Marker).setName(CustomName);
		stand.setNameVasibility(CustomNameVisible).setGlowing(Glowing).setInvisible(Invisible);
		if(compound.hasKey("pose")){
			NBTTagCompound pose = compound.getCompound("pose");
			for(BodyPart part : BodyPart.values()){
				EulerAngle angle = part.getDefAngle();
				String name = part.getName();
				name = name.replace("_", "");
				boolean b = false;
				if(pose.hasKey(name.toLowerCase())){
					NBTTagList nbtPart = pose.getList(name.toLowerCase());
					if(nbtPart.size()==3){
						for(int i = 0; i<nbtPart.size();i++){
							String floatString = nbtPart.getString(i);
							Float f = Float.valueOf(floatString);
							double d = f;
							switch(i){
								case 0:angle = angle.setX(d);break;
								case 1:angle = angle.setY(d);break;
								case 2:angle = angle.setZ(d);break;
							}
						}
					}
					b = true;
				}
				
				if(b) angle = util.degresstoRad(angle);
				stand.setPose(angle, part);
				model.addEnitty(stand);
			}
		}
		
		if(compound.hasKey("handItems")){
			for(int i = 0; i < compound.getList("handItems").size();i++){
				NBTTagCompound item = compound.getList("handitems").get(i);
				switch (i) {
				case 0:stand.setItemInMainHand(getStack(item));break;
				case 1:stand.setItemInOffHand(getStack(item));break;
				}
			}
		}
		if(compound.hasKey("armoritems")){
			for(int i = 0; i < compound.getList("armoritems").size();i++){
				NBTTagCompound item = compound.getList("armoritems").get(i);
				switch (i) {
					case 0:stand.setBoots(getStack(item));break;
					case 1:stand.setLeggings(getStack(item));break;
					case 2:stand.setChestPlate(getStack(item));break;
					case 3:stand.setHelmet(getStack(item));break;
				}
			}
		}
		
		stand.update(model.getPlayer());
		
		model.teleport(stand,new Relative(stand.getLocation(), 0, 1, 0, BlockFace.DOWN),stand.getLocation().getYaw() + 90);
		Bukkit.getScheduler().runTaskLater(main.getInstance(), new Runnable() {
			@Override
			public void run() {
				model.teleport(stand,new Relative(stand.getLocation(), 0, -1, 0, BlockFace.DOWN),stand.getLocation().getYaw() + 0);
			}
		}, 2);
	}
	
	private ItemStack getStack(NBTTagCompound item){
		ItemStack stack = (ItemStack) getCraftItemStack(item.getString("id"));
		int a = 1;if(item.hasKey("count")) a = item.getInt("count");
		short d = 0;
		if(item.hasKey("damage")){
			d = (short) item.getInt("damage");
		}
		stack.setAmount(a);
		stack.setDurability(d);
		return stack;
	}
	
	private Object getCraftItemStack(String id){
		try{
			Method m = item.getDeclaredMethod("b", String.class);
			Object item = m.invoke(null, id);
			
			m = craftItemStack.getDeclaredMethod("asNewCraftStack", this.item);
			return m.invoke(null, item);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	private OutputStream reflectMojangParser(String str, OutputStream stream, String version){
		try {
			Method m = mojangParser.getDeclaredMethod("parse", String.class);
			Object NBTTagCompound = m.invoke(null, str);
			
			Method method = nbtCompressedStreamTools.getDeclaredMethod("a", compound, OutputStream.class);
			method.invoke(null, NBTTagCompound, stream);
			return stream;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static double eval(String string)
    {
       double d = 0d;
       string = string.replace("~", "");
       if(string.isEmpty()) return d;
       if(string.contains("+")){
    	   string = string.replace("+", "");
    	   d+=Double.parseDouble(string);
    	   return d;
       }
       
       if(string.contains("-")){
    	   string = string.replace("-", "");
    	   d-=Double.parseDouble(string);
    	   return d;
       }
       
       d+=Double.parseDouble(string);
       return d;
    }
	
}
