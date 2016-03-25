package de.Ste3et_C0st.DiceFurnitureMaker;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.EulerAngle;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers.WorldBorderAction;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import de.Ste3et_C0st.DiceFunitureMaker.Flags.ArmorStandInventory;
import de.Ste3et_C0st.DiceFunitureMaker.Flags.ArmorStandMetadata;
import de.Ste3et_C0st.DiceFunitureMaker.Flags.ArmorStandSelector;
import de.Ste3et_C0st.FurnitureLib.Utilitis.JsonBuilder;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.BodyPart;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.main.entity.Relative;
import de.Ste3et_C0st.FurnitureLib.main.entity.fArmorStand;
import de.Ste3et_C0st.FurnitureLib.main.entity.fInventory;

public class ProjektModel extends ProjectMetadata implements Listener{
	
	private ItemStack stack1,stack2,stack3,stack4, stack5, stack6, stack7, stack8, stack9;
	private Location loc1, loc2, loc3;
	private String projectName;
	private ItemStack[] slots;
	private int i = 4, o = 0, z = 0, t = 0,size = 10;
	private Player p;
	private ObjectID id;
	private fArmorStand stand = null;
	public String getProjectName() {return this.projectName;}
	public void setProjectName(String projectName) {this.projectName = projectName;}
	public Player getPlayer() {return this.p;}
	public ObjectID getObjectID(){return this.id;}
	private FurnitureLib lib = FurnitureLib.getInstance();
	private double[] dList = {2d,1.75,1.5,1.25,1d,.75,.5,.25,.125, .0625, .03125};
	private float[] dlist = {90,50,45,30,15,10,5,1};
	private boolean delete = false;
	public boolean isDelete(){return this.delete;}
	private BodyPart part = BodyPart.HEAD;
	private String[] sy = {"X", "Y", "Z"};
	
	public ProjektModel(String projectName, Player player){
		this.projectName = projectName;
		this.p = player;
		this.loc1 = player.getLocation();
		this.loc2 = lib.getLocationUtil().getRelativ(loc1, BlockFace.EAST, size-1, size);
		this.loc3 = lib.getLocationUtil().getRelativ(loc1, BlockFace.EAST, -size, -size+1);
		this.loc2.setY(1);
		this.loc3.setY(255);
		this.id = new ObjectID(projectName, main.getInstance().getName(),loc1);
	}
	
	public void makeWall(Location loc, int size){
		this.size = size*2;
		Bukkit.getPluginManager().registerEvents(this, main.getInstance());
		PacketContainer border = new PacketContainer(PacketType.Play.Server.WORLD_BORDER);
		border.getWorldBorderActions().write(0, WorldBorderAction.INITIALIZE);
		border.getIntegers()
		.write(0, 29999984)
		.write(1, 0)
		.write(2, 0);
		border.getLongs()
		.write(0, 0L);
		border.getDoubles()
		.write(0, lib.getLocationUtil().getCenter(loc1).getX())
		.write(1, lib.getLocationUtil().getCenter(loc1).getZ())
		.write(2, (double) size*2)
		.write(3, (double) size*2);
		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(getPlayer(), border);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		setBlock();
	}
	
	public void giveItems(Player p){
		ItemStack[] slots = new ItemStack[10];
		for(int i = 0; i<9;i++){
			if(p.getInventory().getItem(i)!=null){
				slots[i] = p.getInventory().getItem(i);
				p.getInventory().setItem(i, new ItemStack(Material.AIR));
			}
		}
		this.slots = slots;
		stack1 = new ItemStack(Material.ARMOR_STAND);
		ItemMeta meta = stack1.getItemMeta();
		meta.setDisplayName("§9┤§6"+projectName+"§9├ Add ArmorStand");
		stack1.setItemMeta(meta);
		p.getInventory().setItem(0,stack1);
		
		stack2 = new ItemStack(Material.COMPASS);
		meta = stack2.getItemMeta();
		meta.setDisplayName("Move ArmorStand");
		stack2.setItemMeta(meta);
		p.getInventory().setItem(1,stack2);
		
		stack3 = new ItemStack(Material.FEATHER);
		meta = stack3.getItemMeta();
		meta.setDisplayName("Edit ArmorStand");
		stack3.setItemMeta(meta);
		p.getInventory().setItem(2,stack3);
		
		stack4 = new ItemStack(Material.WATCH);
		meta = stack4.getItemMeta();
		meta.setDisplayName("Rotate ArmorStand");
		stack4.setItemMeta(meta);
		p.getInventory().setItem(3,stack4);
		
		stack5 = new ItemStack(Material.CHEST);
		meta = stack5.getItemMeta();
		meta.setDisplayName("Edit Inventory");
		stack5.setItemMeta(meta);
		p.getInventory().setItem(4,stack5);
		
		stack6 = new ItemStack(Material.BLAZE_ROD);
		meta = stack6.getItemMeta();
		meta.setDisplayName("§3" + part.toString().toLowerCase() + " Rotation [§c" + sy[0] + ":" + dlist[0] + "°§3]");
		stack6.setItemMeta(meta);
		p.getInventory().setItem(5,stack6);
		
		stack7 = new ItemStack(Material.END_CRYSTAL);
		meta = stack7.getItemMeta();
		meta.setDisplayName("§3Clone ArmorStand");
		stack7.setItemMeta(meta);
		p.getInventory().setItem(6,stack7);
		
		stack8 = new ItemStack(Material.ENDER_CHEST);
		meta = stack8.getItemMeta();
		meta.setDisplayName("§3Select ArmorStand");
		stack8.setItemMeta(meta);
		p.getInventory().setItem(7,stack8);
		
		stack9 = new ItemStack(Material.DIAMOND);
		meta = stack9.getItemMeta();
		meta.setDisplayName("§6FINISH");
		stack9.setItemMeta(meta);
		p.getInventory().setItem(8,stack9);
		p.updateInventory();
	}
	
	private void addArmorStand(){
		if(stand!=null){
			stand.setGlowing(false); 
			stand.update(getPlayer());
		}
		stand = lib.getFurnitureManager().createArmorStand(getObjectID(), lib.getLocationUtil().getCenter(loc1).subtract(0, .5, 0));
		stand.setGlowing(true);
		stand.send(getPlayer());
		getObjectID().setSQLAction(SQLAction.NOTHING);
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent e){
		if(!e.getPlayer().equals(getPlayer())) return;
		if(e.getItem()==null) return;
		if(e.getItem().equals(stack1)){
			e.setCancelled(true);
			addArmorStand();
		}else if(e.getItem().equals(stack2)){
			e.setCancelled(true);
			if(stand==null) return;
			switch(e.getAction()){
				case LEFT_CLICK_AIR:if(i>0) i-=1;sendMessage(new JsonBuilder("§bMove size changed to:§e" + dList[i]));return;
				case LEFT_CLICK_BLOCK:if(i>0) i-=1;sendMessage(new JsonBuilder("§bMove size changed to:§e" + dList[i]));return;
				case RIGHT_CLICK_AIR:if(i<dList.length-1) i+=1;sendMessage(new JsonBuilder("§bMove size changed to:§e" + dList[i]));return;
				case RIGHT_CLICK_BLOCK:if(i<dList.length-1) i+=1;sendMessage(new JsonBuilder("§bMove size changed to:§e" + dList[i]));return;
				default: return;
			}
			
		}else if(e.getItem().equals(stack4)){
			e.setCancelled(true);
			if(stand==null) return;
		switch(e.getAction()){
		case LEFT_CLICK_AIR:if(o>0) o-=1;sendMessage(new JsonBuilder("§bRotate size changed to:§e" + dlist[o]));return;
		case LEFT_CLICK_BLOCK:if(o>0) o-=1;sendMessage(new JsonBuilder("§bRotate size changed to:§e" + dlist[o]));return;
		case RIGHT_CLICK_AIR:if(o<dlist.length-1) o+=1;sendMessage(new JsonBuilder("§bRotate size changed to:§e" + dlist[o]));return;
		case RIGHT_CLICK_BLOCK:if(o<dlist.length-1) o+=1;sendMessage(new JsonBuilder("§bRotate size changed to:§e" + dlist[o]));return;
		default: return;
		}}else if(e.getItem().equals(stack3)){
			e.setCancelled(true);
			if(stand==null) return;
			new ArmorStandMetadata(stand, getPlayer());
		}else if(e.getItem().equals(stack5)){
			e.setCancelled(true);
			if(stand==null) return;
			new ArmorStandInventory(stand, getPlayer());
		}else if(e.getItem().equals(stack6)){
			e.setCancelled(true);
			if(stand==null) return;
			ItemMeta m = stack6.getItemMeta();
			if(e.getPlayer().isSneaking()){
				switch(e.getAction()){
					case LEFT_CLICK_AIR:
						if(z>0){
						z-=1;
						m.setDisplayName("§3" + BodyPart.values()[z].name().toLowerCase() + " Rotation [§c" + sy[t] + ":" + dlist[o] + "°§3]");
						break;
						}
					case LEFT_CLICK_BLOCK:if(z>0){
						z-=1;
						m.setDisplayName("§3" + BodyPart.values()[z].name().toLowerCase() + " Rotation [§c" + sy[t] + ":" + dlist[o] + "°§3]");
						break;
					}
					case RIGHT_CLICK_AIR:
						if(z<BodyPart.values().length-1){
						z+=1;
						m.setDisplayName("§3" + BodyPart.values()[z].name().toLowerCase() + " Rotation [§c" + sy[t] + ":" + dlist[o] + "°§3]");
						break;
						}
					case RIGHT_CLICK_BLOCK:if(z<BodyPart.values().length-1){
						z+=1;
						m.setDisplayName("§3" + BodyPart.values()[z].name().toLowerCase() + " Rotation [§c" + sy[t] + ":" + dlist[o] + "°§3]");
						break;
						}
				default:break;
				}
				this.stack6.setItemMeta(m);
				this.part = BodyPart.values()[z];
				e.getItem().setItemMeta(m);
				this.p.updateInventory();
			}else{
				switch(e.getAction()){
					case LEFT_CLICK_AIR:
						if(t>0){
						t-=1;
						m.setDisplayName("§3" + BodyPart.values()[z].name().toLowerCase() + " Rotation [§c" + sy[t] + ":" + dlist[o] + "°§3]");
						break;
						}
					case LEFT_CLICK_BLOCK:if(t>0){
						t-=1;
						m.setDisplayName("§3" + BodyPart.values()[z].name().toLowerCase() + " Rotation [§c" + sy[t] + ":" + dlist[o] + "°§3]");
						break;
					}
					case RIGHT_CLICK_AIR:
						if(t<sy.length-1){
						t+=1;
						m.setDisplayName("§3" + BodyPart.values()[z].name().toLowerCase() + " Rotation [§c" + sy[t] + ":" + dlist[o] + "°§3]");
						break;
						}
					case RIGHT_CLICK_BLOCK:if(t<sy.length-1){
						t+=1;
						m.setDisplayName("§3" + BodyPart.values()[z].name().toLowerCase() + " Rotation [§c" + sy[t] + ":" + dlist[o] + "°§3]");
						break;
						}
				default:break;
				}
				this.stack6.setItemMeta(m);
				e.getItem().setItemMeta(m);
				this.p.updateInventory();
			}
		}else if(e.getItem().equals(stack7)){
			e.setCancelled(true);
			if(stand==null) return;
			Location loc = stand.getLocation();
			stand.setGlowing(false); 
			stand.update(getPlayer());
			fArmorStand stand = this.stand.clone(new Relative(loc, 0, 0, 0, lib.getLocationUtil().yawToFace(loc.getYaw())));
			stand.setGlowing(true); 
			stand.setInventory(new fInventory(stand.getEntityID()));
			stand.setBasePlate(this.stand.hasBasePlate());
			stand.send(getPlayer());
			this.stand = stand;
		}else if(e.getItem().equals(stack8)){
			e.setCancelled(true);
			if(stand==null) return;
			new ArmorStandSelector(this);
		}else if(e.getItem().equals(stack9)){
			e.setCancelled(true);
			if(stand==null) return;
			config c = new config();
			FileConfiguration file = c.getConfig(projectName, "");
			file.set(projectName + ".name", "&c" + projectName);
			file.set(projectName + ".material", 383);
			file.set(projectName + ".glow", false);
			file.set(projectName + ".lore", "");
			file.set(projectName + ".crafting.disable", true);
			file.set(projectName + ".crafting.recipe", "xxx,xxx,xxx");
			file.set(projectName + ".crafting.index.x", "7");
			int i = 0;
			for(fArmorStand stand : getObjectID().getPacketList()){file.set(projectName + ".ProjectModels.ArmorStands." + i, toString(stand, this.lib.getLocationUtil().getCenter(loc1).subtract(0, .5, 0)));i++;}
			c.saveConfig(projectName, file, "");
			try {main.getInstance().registerProeject(projectName);}catch(FileNotFoundException fileE){fileE.printStackTrace();}
			remove();
		}
	}
	
	private void setBlock(){
		PacketContainer con = new PacketContainer(PacketType.Play.Server.BLOCK_CHANGE);
		BlockPosition pos = new BlockPosition(loc1.clone().subtract(0, 1, 0).toVector());
		WrappedBlockData blockData = WrappedBlockData.createData(Material.WOOL, 14);
		con.getBlockPositionModifier().write(0, pos);
		con.getBlockData().write(0, blockData);
		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(getPlayer(), con);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	public void remove(){
		if(this.p==null) return;
		PacketContainer con = new PacketContainer(PacketType.Play.Server.BLOCK_CHANGE);
		BlockPosition pos = new BlockPosition(loc1.clone().subtract(0, 1, 0).toVector());
		WrappedBlockData blockData = WrappedBlockData.createData(loc1.clone().subtract(0, 1, 0).getBlock().getType(), 
																 loc1.clone().subtract(0, 1, 0).getBlock().getData());
		con.getBlockPositionModifier().write(0, pos);
		con.getBlockData().write(0, blockData);
		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(getPlayer(), con);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		if(p.getWorld().getWorldBorder()!=null){
			WorldBorder wb = p.getWorld().getWorldBorder();
			PacketContainer border = new PacketContainer(PacketType.Play.Server.WORLD_BORDER);
			border.getWorldBorderActions().write(0, WorldBorderAction.INITIALIZE);
			border.getIntegers()
			.write(0, 29999984)
			.write(1, wb.getWarningTime())
			.write(2, wb.getWarningDistance());
			border.getLongs()
			.write(0, 0L);
			border.getDoubles()
			.write(0, wb.getCenter().getX())
			.write(1, wb.getCenter().getZ())
			.write(2, wb.getSize())
			.write(3, wb.getSize());
			try {
				ProtocolLibrary.getProtocolManager().sendServerPacket(getPlayer(), border);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}else{
			PacketContainer border = new PacketContainer(PacketType.Play.Server.WORLD_BORDER);
			border.getWorldBorderActions().write(0, WorldBorderAction.INITIALIZE);
			border.getIntegers()
			.write(0, 29999984)
			.write(1, 0)
			.write(2, 0);
			border.getLongs()
			.write(0, 0L);
			border.getDoubles()
			.write(0, this.p.getWorld().getSpawnLocation().getX())
			.write(1, this.p.getWorld().getSpawnLocation().getZ())
			.write(2, Double.MAX_VALUE)
			.write(3, Double.MAX_VALUE);
			try {
				ProtocolLibrary.getProtocolManager().sendServerPacket(getPlayer(), border);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		getObjectID().remove(getPlayer(), false, false);
		getObjectID().setSQLAction(SQLAction.NOTHING);
		
		if(this.slots==null) return;
		for(int i = 0; i<9;i++){
			ItemStack stack = this.slots[i];
			if(stack!=null){
				this.p.getInventory().setItem(i, stack);
			}
		}
		this.p.updateInventory();
	}
	
	public void remove(fArmorStand stand, fArmorStand stand2){
		this.stand = null;
		getObjectID().getPacketList().remove(stand);
		stand.setGlowing(false);
		stand.kill(getPlayer(), true);
		if(stand2!=null) this.stand = stand2;
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		if(this.p==null) return;
		if(!this.p.equals(this.p)) return;
		remove();
		this.p = null;
		delete = true;
	}
	
	@EventHandler
	public void onQuit(PlayerKickEvent e){
		if(this.p==null) return;
		if(!this.p.equals(this.p)) return;
		remove();
		this.p = null;
		delete = true;
	}
	
	@EventHandler
	public void onTelePort(PlayerTeleportEvent e){
		if(this.p==null) return;
		if(!this.p.equals(this.p)) return;
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onScorll(PlayerItemHeldEvent e){
		if(!e.getPlayer().equals(getPlayer())) return;
		ItemStack is1 = getPlayer().getInventory().getItem(e.getPreviousSlot());
		if(is1==null) return;
		if(is1.equals(stack2)){
			if(getPlayer().isSneaking()){
				boolean forward = false;
				e.setCancelled(true);
				int i = e.getNewSlot();
				if(i==1){return;}
				e.getPlayer().getInventory().setHeldItemSlot(e.getPreviousSlot());
				if(e.getNewSlot()>e.getPreviousSlot()){forward=true;}
				BlockFace face = main.getInstance().getFurnitureLib().getLocationUtil().yawToFace(getPlayer().getLocation().getYaw());
				if(!forward)face = face.getOppositeFace();
				if(stand==null) return;
				Relative relative = null;
				if(getPlayer().getLocation().getPitch()<-70){
					if(forward){
						relative = new Relative(stand.getLocation(), 0, -dList[this.i], 0, face);
					}else{
						relative = new Relative(stand.getLocation(), 0, dList[this.i], 0, face);
					}
				}else if(getPlayer().getLocation().getPitch()>70){
					if(forward){
						relative = new Relative(stand.getLocation(), 0, dList[this.i], 0, face);
					}else{
						relative = new Relative(stand.getLocation(), 0, -dList[this.i], 0, face);
					}
				}else{
					relative = new Relative(stand.getLocation(), dList[this.i], 0, 0, face);
				}
				teleport(relative);
			}
		}else if(is1.equals(stack4)){
			if(getPlayer().isSneaking()){
				boolean forward = false;
				e.setCancelled(true);
				int i = e.getNewSlot();
				if(i==3){return;}
				e.getPlayer().getInventory().setHeldItemSlot(e.getPreviousSlot());
				if(e.getNewSlot()>e.getPreviousSlot()){forward=true;}
				if(stand==null) return;
				if(forward){
					teleport(new Relative(stand.getLocation(), 0, 1, 0, BlockFace.DOWN),stand.getLocation().getYaw()+dlist[o]);
					Bukkit.getScheduler().runTaskLater(main.getInstance(), new Runnable() {
						@Override
						public void run() {
							teleport(new Relative(stand.getLocation(), 0, -1, 0, BlockFace.DOWN),stand.getLocation().getYaw()+dlist[o]);
						}
					}, 2);
					return;
				}else{
					teleport(new Relative(stand.getLocation(), 0, 1, 0, BlockFace.DOWN),stand.getLocation().getYaw()-dlist[o]);
					Bukkit.getScheduler().runTaskLater(main.getInstance(), new Runnable() {
						@Override
						public void run() {
							teleport(new Relative(stand.getLocation(), 0, -1, 0, BlockFace.DOWN),stand.getLocation().getYaw()-dlist[o]);
						}
					}, 2);
					return;
				}
			}
		}else if(is1.equals(stack6)){
			if(getPlayer().isSneaking()){
				boolean forward = false;
				e.setCancelled(true);
				int i = e.getNewSlot();
				if(i==5){return;}
				e.getPlayer().getInventory().setHeldItemSlot(e.getPreviousSlot());
				if(e.getNewSlot()>e.getPreviousSlot()){forward=true;}
				if(stand==null) return;
				BodyPart part = this.part;
				String rotType = sy[t];
				double degres = (double) dlist[o];
				EulerAngle angle = this.stand.getPose(part);
				angle = lib.getLocationUtil().Radtodegress(angle);
				if(forward){
					double x=angle.getX();
					double y=angle.getY();
					double z=angle.getZ();
					switch (rotType) {
					case "X":x+=degres;break;
					case "Y":y+=degres;break;
					case "Z":z+=degres;break;
					}
					angle = angle.setX(x).setY(y).setZ(z);
				}else{
					double x=angle.getX();
					double y=angle.getY();
					double z=angle.getZ();
					switch (rotType) {
					case "X":x-=degres;break;
					case "Y":y-=degres;break;
					case "Z":z-=degres;break;
					}
					angle = angle.setX(x).setY(y).setZ(z);
				}
				this.stand.setPose(lib.getLocationUtil().degresstoRad(angle), part);
				this.stand.update(this.p);
				this.stand.getObjID().setSQLAction(SQLAction.NOTHING);
			}
		}
	}
	
	public void select(fArmorStand stand){
		this.stand.setGlowing(false);
		this.stand.update(getPlayer());
		this.stand = stand;
		this.stand.setGlowing(true);
		this.stand.update(getPlayer());
		getObjectID().setSQLAction(SQLAction.NOTHING);
	}
	
	private void sendMessage(JsonBuilder builder){
		PacketContainer con = new PacketContainer(PacketType.Play.Server.CHAT);
		con.getChatComponents().write(0, WrappedChatComponent.fromJson(builder.toString()));
		con.getBytes().write(0, (byte) 2);
		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(getPlayer(), con);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	
	private void teleport(Relative relative){
		if(!isInside(relative.getSecondLocation(), loc2, loc3)) return;
		Location loc = relative.getSecondLocation();
		loc.setYaw(stand.getLocation().getYaw());
		stand.teleport(loc);
		stand.update(getPlayer());
		id.setSQLAction(SQLAction.NOTHING);
	}
	
	public boolean isInside(Location loc, Location l1, Location l2) {
        int x1 = Math.min(l1.getBlockX(), l2.getBlockX());
        int z1 = Math.min(l1.getBlockZ(), l2.getBlockZ());
        int x2 = Math.max(l1.getBlockX(), l2.getBlockX());
        int z2 = Math.max(l1.getBlockZ(), l2.getBlockZ());
        return loc.getX() >= x1 && loc.getX() <= x2 && loc.getZ() >= z1 && loc.getZ() <= z2;
	}
	
	private void teleport(Relative relative, float yaw){
		Location loc = relative.getSecondLocation();
		loc.setYaw(yaw);
		stand.teleport(loc);
		stand.update(getPlayer());
		id.setSQLAction(SQLAction.NOTHING);
	}
	public fArmorStand getStand() {
		return this.stand;
	}
}
