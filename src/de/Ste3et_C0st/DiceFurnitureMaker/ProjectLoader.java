package de.Ste3et_C0st.DiceFurnitureMaker;
import java.io.ByteArrayInputStream;
import java.io.File;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import de.Ste3et_C0st.FurnitureLib.Events.FurnitureBreakEvent;
import de.Ste3et_C0st.FurnitureLib.Events.FurnitureClickEvent;
import de.Ste3et_C0st.FurnitureLib.NBT.CraftItemStack;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.main.Furniture;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.BodyPart;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.main.entity.Vector3f;
import de.Ste3et_C0st.FurnitureLib.main.entity.fArmorStand;

public class ProjectLoader extends Furniture{
	private Object[] enumItemSlots = new Vector3f().b();
	public ProjectLoader(ObjectID id){
		super(id);
		if(isFinish()){
			Bukkit.getPluginManager().registerEvents(this, main.getInstance());
			return;
		}
		spawn(id.getStartLocation());
		Bukkit.getPluginManager().registerEvents(this, main.getInstance());
	}

	@EventHandler
	public void onFurnitureBreak(FurnitureBreakEvent e) {
		if(getObjID()==null){return;}
		if(getObjID().getSQLAction().equals(SQLAction.REMOVE)){return;}
		if(e.isCancelled()) return;
		if(!e.getID().equals(getObjID())) return;
		if(!e.canBuild()){return;}
		e.remove();
	}

	@Override
	public void onFurnitureClick(FurnitureClickEvent arg0) {}

	@Override
	public void spawn(Location loc) {
		YamlConfiguration config = new YamlConfiguration();
		String name = getObjID().getProject();
		try {
			config.load(new File("plugins/FurnitureLib/plugin/DiceEditor/", name+".yml"));
			for(String s : config.getConfigurationSection(name+".ProjectModels.ArmorStands").getKeys(false)){
				String md5 = config.getString(name+".ProjectModels.ArmorStands."+s);
				byte[] by = Base64.decodeBase64(md5);
				ByteArrayInputStream bin = new ByteArrayInputStream(by);
				NBTTagCompound metadata = NBTCompressedStreamTools.read(bin);
				String customName = metadata.getString("Name");
				NBTTagCompound location = metadata.getCompound("Location");
				double x = location.getDouble("X-Offset");
				double y = location.getDouble("Y-Offset");
				double z = location.getDouble("Z-Offset");
				float yaw = location.getFloat("Yaw");
				Location armorLocation = getRelative(getCenter(), getBlockFace(), -z, -x).add(0, y-.5, 0);
				armorLocation.setYaw(yaw+getYaw()-180);
				boolean n = (metadata.getInt("NameVisible")==1),b = (metadata.getInt("BasePlate")==1),s1 = (metadata.getInt("Small")==1);
				boolean f = (metadata.getInt("Fire")==1),a = (metadata.getInt("Arms")==1),i = (metadata.getInt("Invisible")==1);
				boolean m = (metadata.getInt("Marker")==1),g = (metadata.getInt("Glowing")==1);
				fArmorStand packet = FurnitureLib.getInstance().getFurnitureManager().createArmorStand(getObjID(), armorLocation);
				NBTTagCompound inventory = metadata.getCompound("Inventory");
				for(Object object : enumItemSlots){
					if(!inventory.getString(object.toString()).equalsIgnoreCase("NONE")){
						ItemStack is = new CraftItemStack().getItemStack(inventory.getCompound(object.toString()+""));
						packet.getInventory().setSlot(object.toString(), is);
					}
				}
				NBTTagCompound euler = metadata.getCompound("EulerAngle");
				for(BodyPart part : BodyPart.values()){
					packet.setPose(eulerAngleFetcher(euler.getCompound(part.toString())), part);
				}
				packet.setBasePlate(b).setSmall(s1).setMarker(m).setArms(a).setGlowing(g).setNameVasibility(n).setName(customName).setFire(f).setGlowing(g).setInvisible(i);
			}
			send();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private EulerAngle eulerAngleFetcher(NBTTagCompound eularAngle){
		Double X = eularAngle.getDouble("X");
		Double Y = eularAngle.getDouble("Y");
		Double Z = eularAngle.getDouble("Z");
		return new EulerAngle(X, Y, Z);
	}
}
