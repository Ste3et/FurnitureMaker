package de.Ste3et_C0st.DiceFurnitureMaker;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagList;
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
			
			if(s.contains("MinecartCommandBlock,Command:summon ArmorStand")){
				String[] commands = s.split("id:MinecartCommandBlock,Command:");
				int i = 0;
				for(String str : commands){
					if(i!=0){
						if(!str.startsWith("summon ArmorStand ")){i++;continue;}
						runCommand(str, model, startLocation);
					}
					i++;
				}
			}else{
				if(s.startsWith("/")) s = s.replaceFirst("/", "");
				runCommand(s, model, startLocation);
			}
		}catch (Exception e){
			e.printStackTrace();
			return;
		}
	}
	
	
	
	public void runCommand(String str, ProjektModel model, Location StartLocation) throws Exception{
		str = str.replace("summon ArmorStand ", "");
		String[] args = str.split(" ");
		String xOffsetStr = args[0];
		String yOffsetStr = args[1];
		String zOffsetStr = args[2];
		double xOffset = eval(xOffsetStr);
		double yOffset = eval(yOffsetStr);
		double zOffset = eval(zOffsetStr);
		Relative relative = new Relative(StartLocation, xOffset, yOffset, zOffset, BlockFace.NORTH);
		
		str = str.replace(xOffsetStr + " ", "");
		str = str.replace(yOffsetStr + " ", "");
		str = str.replace(zOffsetStr + " ", "");
		if(str.endsWith(",{")){str = str.substring(0,str.length()-3);}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		OutputStream stream = reflectMojangParser(str, out, version);
		if(stream!=null){
			out = (ByteArrayOutputStream) stream;
		}
		ByteArrayInputStream input = new ByteArrayInputStream(out.toByteArray());
		NBTTagCompound compound = NBTCompressedStreamTools.read(input);
		
		String CustomName = "";
		boolean CustomNameVisible = false;
		boolean ShowArms = false;
		boolean Marker = false;
		boolean Glowing = false;
		boolean Small = false;
		boolean Invisible = false;
		boolean NoBasePlate = false;
		
		if(compound.hasKey("CustomNameVisible")){CustomNameVisible = (compound.getInt("CustomNameVisible")==1);}
		if(compound.hasKey("ShowArms")){ShowArms = (compound.getInt("ShowArms")==1);}
		if(compound.hasKey("Small")){Small = (compound.getInt("Small")==1);}
		if(compound.hasKey("Marker")){Marker = (compound.getInt("Marker")==1);}
		if(compound.hasKey("Glowing")){Glowing = (compound.getInt("Glowing")==1);}
		if(compound.hasKey("Invisible")){Invisible = (compound.getInt("Invisible")==1);}
		if(compound.hasKey("NoBasePlate")){NoBasePlate = (compound.getInt("NoBasePlate")==1);}
		if(compound.hasKey("CustomName")){CustomName = ChatColor.translateAlternateColorCodes('&', compound.getString("CustomName"));}
		
		fArmorStand stand = model.createStand(relative);
		stand.setArms(ShowArms).setSmall(Small).setBasePlate(NoBasePlate).setMarker(Marker).setName(CustomName);
		stand.setNameVasibility(CustomNameVisible).setGlowing(Glowing).setInvisible(Invisible);
		
		
		if(compound.hasKey("Pose")){
			NBTTagCompound pose = compound.getCompound("Pose");
			for(BodyPart part : BodyPart.values()){
				EulerAngle angle = part.getDefAngle();
				boolean b = false;
				for(int i = 0; i < pose.getList(part.getName().replace("_", "")).size(); i++){
					switch (i) {
					case 0:angle.setX(pose.getList(part.getName().replace("_", "")).getDouble(i));b=true;break;
					case 1:angle.setX(pose.getList(part.getName().replace("_", "")).getDouble(i));b=true;break;
					case 2:angle.setX(pose.getList(part.getName().replace("_", "")).getDouble(i));b=true;break;
					}
				}
				if(b){angle = util.degresstoRad(angle);}
				stand.setPose(angle, part);
			}
		}
		
		if(compound.hasKey("HandItems")){
			for(int i = 0; i < compound.getList("HandItems").size();i++){
				NBTTagCompound item = compound.getList("HandItems").get(i);
				switch (i) {
				case 0:stand.setItemInMainHand(getStack(item, compound));break;
				case 1:stand.setItemInOffHand(getStack(item, compound));break;
				}
			}
		}
		if(compound.hasKey("ArmorItems")){
			for(int i = 0; i < compound.getList("ArmorItems").size();i++){
				NBTTagCompound item = compound.getList("ArmorItems").get(i);
				switch (i) {
				case 0:stand.setBoots(getStack(item, compound));break;
				case 1:stand.setLeggings(getStack(item, compound));break;
				case 2:stand.setChestPlate(getStack(item, compound));break;
				case 3:stand.setHelmet(getStack(item, compound));break;
				}
			}
		}
		
		if(compound.hasKey("Rotation")){
			System.out.println("Rotation");
			NBTTagList roation = compound.getList("Rotation");
			System.out.println(roation.toString());
		}
		System.out.println(compound.toString());
		stand.update(model.getPlayer());
	}
	
	private ItemStack getStack(NBTTagCompound item, NBTTagCompound compound){
		ItemStack stack = (ItemStack) getCraftItemStack(item.getString("id"));
		int a = 1;if(item.hasKey("Count")) a = compound.getInt("Count");
		short d = 0;if(item.hasKey("Damage")) d = (short) compound.getInt("Damage");
//		ItemStack stack = new ItemStack(rawStack.getType(), a, d);
//		String displayName = "";
//		List<String> loreText = new ArrayList<String>();
//		if(item.hasKey("tag")){
//			NBTTagCompound tags = item.getCompound("tag");
//			if(tags.hasKey("display")){
//				NBTTagCompound display = tags.getCompound("display");
//				if(display.hasKey("Name")){displayName = ChatColor.translateAlternateColorCodes('&', display.getString("Name"));}
//				if(display.hasKey("Lore")){
//					for(int j = 0; j <display.getList("Lore").size();j++){
//						loreText.add(ChatColor.translateAlternateColorCodes('&', display.getList("Lore").getString(j)));
//					}
//				}
//			}
//		}
//		meta.setDisplayName("");
//		meta.setLore(loreText);
//		stack.setItemMeta(meta);
		stack.setAmount(a);
		stack.setDurability(d);
		return stack;
	}
	
	private Object getCraftItemStack(String id){
		try{
			Method m = item.getDeclaredMethod("d", String.class);
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
