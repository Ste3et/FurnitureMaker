 package de.Ste3et_C0st.DiceFurnitureMaker;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.EulerAngle;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers.ChatType;
import com.comphenix.protocol.wrappers.EnumWrappers.WorldBorderAction;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import de.Ste3et_C0st.DiceFunitureMaker.Flags.ArmorStandInventory;
import de.Ste3et_C0st.DiceFunitureMaker.Flags.ArmorStandMetadata;
import de.Ste3et_C0st.DiceFunitureMaker.Flags.ArmorStandSelector;
import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.ShematicLoader.ProjectMetadata;
import de.Ste3et_C0st.FurnitureLib.Utilitis.Relative;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.BodyPart;
import de.Ste3et_C0st.FurnitureLib.main.Type.PlaceableSide;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.main.entity.fArmorStand;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public class ProjektModel extends ProjectMetadata implements Listener{
	
	private ItemStack stack1,stack2,stack3,stack4, stack5, stack6, stack7, stack8, stack9,stack10, stack11, stack12, stack14, stack15, stack16;
	private List<ItemStack> stackList = new ArrayList<ItemStack>();
	private Location loc1, loc2, loc3;
	private String projectName;
	private ItemStack[] slots = null;
	private int i = 4, o = 0, z = 0, t = 0,size = 10;
	private Player p;
	private ObjectID id;
	private List<fEntity> entityList = new ArrayList<fEntity>();
	private PlaceableSide side = PlaceableSide.TOP;
	public String getProjectName() {return this.projectName;}
	public void setProjectName(String projectName) {this.projectName = projectName;}
	public Player getPlayer() {return this.p;}
	public ObjectID getObjectID(){return this.id;}
	private FurnitureLib lib = FurnitureLib.getInstance();
	private double[] dList = {2d,1.75,1.5,1.25,1d,.75,.5,.25,.125, .0625, .03125};
	private float[] dlist = {90,50,45,30,15,10,5,1};
	private boolean delete = false;
	public boolean isDelete(){return this.delete;}
	public Location getStartLocation(){return this.loc1;}
	private BodyPart part = BodyPart.HEAD;
	private String[] sy = {"X", "Y", "Z"};
	public List<Block> blockList = new ArrayList<Block>();
	public void setObjectID(ObjectID id){this.id = id;}
	public Block commandBlock = null;
	public Location getLoc1(){return this.loc1;}
	public Location getLoc2(){return this.loc2;}
	
	public ProjektModel(String projectName, Player player){
		this.projectName = projectName;
		this.p = player;
		this.loc1 = player.getLocation();
		this.loc2 = lib.getLocationUtil().getRelativ(loc1, BlockFace.EAST, size-1, size);
		this.loc3 = lib.getLocationUtil().getRelativ(loc1, BlockFace.EAST, -size, -size+1);
		this.loc2.setY(1);
		this.loc3.setY(255);
		this.id = new ObjectID(projectName, main.getInstance().getName(),loc1);
		this.id.setPrivate(true);
		getPlayer().sendMessage("§n§2Short Description:");
		getPlayer().sendMessage("§cRed Wool Block: " + "§7is the start position");
		getPlayer().sendMessage("§2Green Wool Block: " + "§7is the North Direction");
		getPlayer().sendMessage("§9Blue Wool Block: " + "§7is the East Direction");
		getPlayer().sendMessage("§fWhite Wool Block: " + "§7is the perfect working area");
		player.spigot().sendMessage(new ComponentBuilder("§6- if you need help visit this ").append("§n§2side").event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://dicecraft.de/furniture/tutorial.php")).create()); 
		this.id.getPlayerList().add(player);
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
		

		for(int x = 0; x <=10 ; x++){
			for(int y = 0; y <=10 ; y++){
				setBlock(new Relative(loc1, -x, 0, y, BlockFace.NORTH).getSecondLocation(), Material.WHITE_WOOL);
			}
		}
		
		setBlock(this.loc1, Material.RED_WOOL);
		setBlock(new Relative(loc1, -1, 0, 0, BlockFace.NORTH).getSecondLocation(), Material.GREEN_WOOL);
		setBlock(new Relative(loc1, 0, 0, 1, BlockFace.NORTH).getSecondLocation(), Material.BLUE_WOOL);
	}
	
	public void giveItems(Player p){
		ItemStack[] slots = new ItemStack[10];
		for(int i = 0; i<9;i++){
			if(p.getInventory().getItem(i)!=null){
				slots[i] = p.getInventory().getItem(i);
				p.getInventory().setItem(i, new ItemStack(Material.AIR));
			}
		}
		
		String header = "§eInformations";
		String leftClick = "§6Left-Click: ";
		String rightClick = "§6Right-Click: ";
		String leftShiftClick = "§6Shift-Left-Click: ";
		String rightShiftClick = "§6Shift-Right-Click: ";
		String scrollLeft = "§6Scroll-Left: ";
		String scrollRight = "§6Scroll-Right: ";
		
		List<String> move = Arrays.asList(header,leftClick+ "§eSize up", rightClick + "§eSize down",leftShiftClick+ "§eSize up",rightShiftClick+ "§eSize down",scrollLeft+"§eMove Away",scrollRight+"§eMove back");
		List<String> rotate = Arrays.asList(header,leftClick+ "§eSize up", rightClick + "§eSize down",leftShiftClick+ "§eSize up",rightShiftClick+ "§eSize down",scrollLeft+"§eRotate left",scrollRight+"§eRotate right","§cOn Size change it change the size from the","§cBody Pose Item (Blaze-Rod)");
		List<String> bodyPose = Arrays.asList(header,leftClick+ "§eChange the Axe up", rightClick + "§eChange the Axe down",leftShiftClick+ "§eChange the BodyPart up",rightShiftClick+ "§eChange the BodyPart down",scrollLeft+"§eRotate away",scrollRight+"§eRotate to you");
		
		this.slots = slots;
		stack1 = new ItemStack(Material.ARMOR_STAND);
		ItemMeta meta = stack1.getItemMeta();
		meta.setDisplayName("§9┤§6"+projectName+"§9├ Add ArmorStand");
		stack1.setItemMeta(meta);
		stackList.add(stack1);
		p.getInventory().setItem(0,stack1);
		
		stack2 = new ItemStack(Material.PAPER);
		meta = stack2.getItemMeta();
		meta.setLore(move);
		meta.setDisplayName("Move ArmorStand");
		stackList.add(stack2);
		stack2.setItemMeta(meta);
		
		stack3 = new ItemStack(Material.FEATHER);
		meta = stack3.getItemMeta();
		meta.setDisplayName("Edit ArmorStand");
		stackList.add(stack3);
		stack3.setItemMeta(meta);
		
		stack4 = new ItemStack(Material.CLOCK);
		meta = stack4.getItemMeta();
		meta.setLore(rotate);
		meta.setDisplayName("Rotate ArmorStand");
		stackList.add(stack4);
		stack4.setItemMeta(meta);
		
		stack5 = new ItemStack(Material.CHEST);
		meta = stack5.getItemMeta();
		meta.setDisplayName("Edit Inventory");
		stackList.add(stack5);
		stack5.setItemMeta(meta);
		
		stack6 = new ItemStack(Material.BLAZE_ROD);
		meta = stack6.getItemMeta();
		meta.setLore(bodyPose);
		meta.setDisplayName("§3" + part.toString().toLowerCase() + " Rotation [§c" + sy[0] + ":" + dlist[0] + "°§3]");
		stackList.add(stack6);
		stack6.setItemMeta(meta);
		
		stack7 = new ItemStack(Material.END_CRYSTAL);
		meta = stack7.getItemMeta();
		meta.setDisplayName("§3Clone ArmorStand");
		stackList.add(stack7);
		stack7.setItemMeta(meta);
		
		stack8 = new ItemStack(Material.ENDER_CHEST);
		meta = stack8.getItemMeta();
		meta.setDisplayName("§3Select ArmorStand");
		stackList.add(stack8);
		stack8.setItemMeta(meta);
		
		stack9 = new ItemStack(Material.DIAMOND);
		meta = stack9.getItemMeta();
		meta.setDisplayName("§6FINISH");
		stackList.add(stack9);
		stack9.setItemMeta(meta);
		
		stack10 = new ItemStack(Material.BARRIER);
		meta = stack10.getItemMeta();
		meta.setDisplayName("§cAbort");
		stack10.setItemMeta(meta);
		stackList.add(stack10);
		p.getInventory().setItem(8,stack10);
		
		stack11 = new ItemStack(Material.STICK);
		meta = stack11.getItemMeta();
		meta.setDisplayName("§6◄ Back");
		stackList.add(stack11);
		stack11.setItemMeta(meta);
		
		stack12 = new ItemStack(Material.STICK);
		meta = stack12.getItemMeta();
		meta.setDisplayName("§6► Editor");
		stackList.add(stack12);
		stack12.setItemMeta(meta);
		
		stack14 = new ItemStack(Material.BLAZE_POWDER);
		meta = stack14.getItemMeta();
		meta.setDisplayName("Rotate all selected armorstands");
		meta.setLore(rotate);
		stack14.setItemMeta(meta);
		stackList.add(stack14);
		
		stack15 = new ItemStack(Material.GREEN_BANNER);
		BannerMeta bannermeta = (BannerMeta) stack15.getItemMeta();
		bannermeta.setDisplayName(getPlaceAbleSide());
		stack15.setItemMeta(bannermeta);
		stackList.add(stack15);
		
		p.updateInventory();
	}
	
	private void addArmorStand(EntityType type){
		if(!entityList.isEmpty()){
			for(fEntity entity : entityList){
				entity.setGlowing(false);
				entity.update(getPlayer());
			}
		}
		entityList.clear();
		fEntity entity = null;
		switch (type) {
		case ARMOR_STAND:
			entity = lib.getFurnitureManager().createArmorStand(getObjectID(), lib.getLocationUtil().getCenter(loc1).subtract(0, .5, 0));
			break;
		case CREEPER:
			entity = lib.getFurnitureManager().createCreeper(getObjectID(), lib.getLocationUtil().getCenter(loc1).subtract(0, .5, 0));
		default:
			break;
		}
		if(entity == null) return;
		entity.setGlowing(true);
		entity.send(getPlayer());
		entityList.add(entity);
		getObjectID().setSQLAction(SQLAction.NOTHING);
	}
	
	public fEntity createEntity(Relative relative, EntityType type){
		fEntity entity = null;
		switch (type) {
		case ARMOR_STAND:
			entity = lib.getFurnitureManager().createArmorStand(getObjectID(), relative.getSecondLocation());
			entity.send(getPlayer());
			getObjectID().setSQLAction(SQLAction.NOTHING);
			return entity;
		case CREEPER:
			entity = lib.getFurnitureManager().createCreeper(getObjectID(), relative.getSecondLocation());
			entity.send(getPlayer());
			getObjectID().setSQLAction(SQLAction.NOTHING);
			return entity;
		default:return null;
		}

	}
	
	private void addItemPage2(){
		p.getInventory().setItem(0, stack1);
		p.getInventory().setItem(1, stack2);
		p.getInventory().setItem(2, stack3);
		p.getInventory().setItem(3, stack4);
		p.getInventory().setItem(4, stack5);
		p.getInventory().setItem(5, stack6);
		p.getInventory().setItem(6, stack7);
		p.getInventory().setItem(7, stack8);
		p.getInventory().setItem(8, stack11);
		p.updateInventory();
		if(entityList.isEmpty()) return;
		getObjectID().setSQLAction(SQLAction.NOTHING);
	}
	
	public void addItemPage1(){
		if(!entityList.isEmpty()){
			p.getInventory().setItem(0, stack12);
			p.getInventory().setItem(1, stack10);
			p.getInventory().setItem(2, stack14);
			p.getInventory().setItem(3, stack15);
			p.getInventory().setItem(4, new ItemStack(Material.AIR));
			p.getInventory().setItem(5, new ItemStack(Material.AIR));
			p.getInventory().setItem(6, new ItemStack(Material.AIR));
			p.getInventory().setItem(7, stack16);
			p.getInventory().setItem(8, stack9);
			p.updateInventory();
			getObjectID().setSQLAction(SQLAction.NOTHING);
		}else{
			p.getInventory().setItem(0, stack1);
			p.getInventory().setItem(1, new ItemStack(Material.AIR));
			p.getInventory().setItem(2, new ItemStack(Material.AIR));
			p.getInventory().setItem(3, new ItemStack(Material.AIR));
			p.getInventory().setItem(4, new ItemStack(Material.AIR));
			p.getInventory().setItem(5, new ItemStack(Material.AIR));
			p.getInventory().setItem(6, new ItemStack(Material.AIR));
			p.getInventory().setItem(7, new ItemStack(Material.AIR));
			p.getInventory().setItem(8, stack10);
			p.updateInventory();
		}

	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent e) throws IOException{
		if(getPlayer()==null) return;
		if(!e.getPlayer().equals(getPlayer())) return;
		if(e.getItem()==null) return;
		if(e.getItem().equals(stack1)){
			e.setCancelled(true);
			addItemPage2();
			addArmorStand(EntityType.ARMOR_STAND);
			this.p.playSound(this.loc1, Sound.ENTITY_ARMOR_STAND_PLACE, 1, 1);
		}else if ((e.getItem().equals(stack2))){
	        e.setCancelled(true);
	        if (entityList.isEmpty()) {return;}
	        switch (e.getAction())
	        {
	        case PHYSICAL: 
	          if (this.i > 0) {this.i -= 1;}
	          sendMessage("§bMove size changed to:§e" + dList[this.i]);p.playSound(loc1, Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1.0F, (float)dList[this.i]);return;
	        case LEFT_CLICK_AIR: 
	          if (this.i > 0) {this.i -= 1; }
	          sendMessage("§bMove size changed to:§e" + dList[this.i]);p.playSound(loc1, Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1.0F, (float)dList[this.i]);return;
	        case RIGHT_CLICK_AIR: 
	          if (this.i < dList.length - 1) {this.i += 1;}
	          sendMessage("§bMove size changed to:§e" + dList[this.i]);p.playSound(loc1, Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 1.0F, (float)dList[this.i]);return;
	        case LEFT_CLICK_BLOCK: 
	          if (this.i < dList.length - 1) {this.i += 1;}
	          sendMessage("§bMove size changed to:§e" + dList[this.i]);p.playSound(loc1, Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 1.0F, (float)dList[this.i]);return;
			default:break;}
	        return;
	      }else if(e.getItem().equals(stack4) || e.getItem().equals(stack14)){
			e.setCancelled(true);
			if(entityList.isEmpty()) return;
		switch(e.getAction()){
		case LEFT_CLICK_AIR:if(o>0) o-=1;sendMessage("§bRotate size changed to:§e" + dlist[o]);this.p.playSound(this.loc1, Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, (float) dList[o]);return;
		case LEFT_CLICK_BLOCK:if(o>0) o-=1;sendMessage("§bRotate size changed to:§e" + dlist[o]);this.p.playSound(this.loc1, Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, (float) dList[o]);return;
		case RIGHT_CLICK_AIR:if(o<dlist.length-1) o+=1;sendMessage("§bRotate size changed to:§e" + dlist[o]);this.p.playSound(this.loc1, Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 1, (float) dList[o]);return;
		case RIGHT_CLICK_BLOCK:if(o<dlist.length-1) o+=1;sendMessage("§bRotate size changed to:§e" + dlist[o]);this.p.playSound(this.loc1, Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 1, (float) dList[o]);return;
		default: return;
		}}else if(e.getItem().equals(stack3)){
			e.setCancelled(true);
			if(entityList.isEmpty()) return;
			new ArmorStandMetadata((fArmorStand) entityList.get(0), getPlayer(), this.id);
		}else if(e.getItem().equals(stack5)){
			e.setCancelled(true);
			if(entityList.isEmpty()) return;
			new ArmorStandInventory((fArmorStand) entityList.get(0), getPlayer());
		}else if(e.getItem().equals(stack6)){
			e.setCancelled(true);
			if(entityList.isEmpty()) return;
			ItemMeta m = stack6.getItemMeta();
			if(e.getPlayer().isSneaking()){
				switch(e.getAction()){
					case LEFT_CLICK_AIR:
						if(z>0){
						z-=1;
						m.setDisplayName("§3" + BodyPart.values()[z].name().toLowerCase() + " Rotation [§c" + sy[t] + ":" + dlist[o] + "°§3]");
					    sendMessage("§bBodyPart for rotate: " + BodyPart.values()[z].name().toLowerCase());
						this.p.playSound(this.loc1, Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, (float) dList[z]);
						break;
						}
					case LEFT_CLICK_BLOCK:if(z>0){
						z-=1;
						m.setDisplayName("§3" + BodyPart.values()[z].name().toLowerCase() + " Rotation [§c" + sy[t] + ":" + dlist[o] + "°§3]");
						sendMessage("§bBodyPart for rotate: " + BodyPart.values()[z].name().toLowerCase());
						this.p.playSound(this.loc1, Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, (float) dList[z]);
						break;
					}
					case RIGHT_CLICK_AIR:
						if(z<BodyPart.values().length-1){
						z+=1;
						m.setDisplayName("§3" + BodyPart.values()[z].name().toLowerCase() + " Rotation [§c" + sy[t] + ":" + dlist[o] + "°§3]");
						sendMessage("§bBodyPart for rotate: " + BodyPart.values()[z].name().toLowerCase());
						this.p.playSound(this.loc1, Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, (float) dList[z]);
						break;
						}
					case RIGHT_CLICK_BLOCK:if(z<BodyPart.values().length-1){
						z+=1;
						m.setDisplayName("§3" + BodyPart.values()[z].name().toLowerCase() + " Rotation [§c" + sy[t] + ":" + dlist[o] + "°§3]");
						sendMessage("§bBodyPart for rotate: " + BodyPart.values()[z].name().toLowerCase());
						this.p.playSound(this.loc1, Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, (float) dList[z]);
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
						sendMessage("§bBodypart: " + BodyPart.values()[z].name().toLowerCase() + " axis:"  + sy[t]);
						this.p.playSound(this.loc1, Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, (float) dList[z]);
						break;
						}
					case LEFT_CLICK_BLOCK:if(t>0){
						t-=1;
						m.setDisplayName("§3" + BodyPart.values()[z].name().toLowerCase() + " Rotation [§c" + sy[t] + ":" + dlist[o] + "°§3]");
						sendMessage("§bBodypart: " + BodyPart.values()[z].name().toLowerCase() + " axis:"  + sy[t]);
						this.p.playSound(this.loc1, Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, (float) dList[z]);
						break;
					}
					case RIGHT_CLICK_AIR:
						if(t<sy.length-1){
						t+=1;
						m.setDisplayName("§3" + BodyPart.values()[z].name().toLowerCase() + " Rotation [§c" + sy[t] + ":" + dlist[o] + "°§3]");
						sendMessage("§bBodypart: " + BodyPart.values()[z].name().toLowerCase() + " axis:"  + sy[t]);
						this.p.playSound(this.loc1, Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, (float) dList[z]);
						break;
						}
					case RIGHT_CLICK_BLOCK:if(t<sy.length-1){
						t+=1;
						m.setDisplayName("§3" + BodyPart.values()[z].name().toLowerCase() + " Rotation [§c" + sy[t] + ":" + dlist[o] + "°§3]");
						sendMessage("§bBodypart: " + BodyPart.values()[z].name().toLowerCase() + " axis:"  + sy[t]);
						this.p.playSound(this.loc1, Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, (float) dList[z]);
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
			if(entityList.isEmpty()) return;
			
			List<fEntity> entityList = new ArrayList<fEntity>();
			for(fEntity entity : this.entityList){
				fArmorStand stand = (fArmorStand) entity;
				Location loc = stand.getLocation();
				stand.setGlowing(false); 
				stand.update(getPlayer());
				fArmorStand clone = stand.clone(loc);
				clone.setGlowing(true);
				clone.send(getPlayer());
				entityList.add(clone);
			} 
			this.entityList = entityList;
		}else if(e.getItem().equals(stack8)){
			e.setCancelled(true);
			if(entityList.isEmpty()) return;
			new ArmorStandSelector(this);
		}else if(e.getItem().equals(stack9)){
			e.setCancelled(true);
			if(entityList.isEmpty()) return;
			File filePath = new File("plugins/" + FurnitureLib.getInstance().getName() + "/models/" + projectName + ".dModel");
			if(!filePath.getParentFile().exists()) filePath.getParentFile().mkdirs();
			if(!filePath.exists()) filePath.createNewFile();
			
			FileConfiguration file = YamlConfiguration.loadConfiguration(filePath);
			
			file.options().header("------------------------------------  #\n"
					+ "                                      #\n"
					+ "      never touch the system-ID !     #\n"
					+ "                                      #\n"
					+ "------------------------------------  #\n");
			file.options().copyHeader(true);
			file.set(projectName + ".displayName", "&c" + projectName);
			file.set(projectName + ".system-ID", projectName);
			file.set(projectName + ".creator", this.p.getUniqueId().toString());
			file.set(projectName + ".spawnMaterial", FurnitureLib.getInstance().getDefaultSpawnMaterial().name());
			file.set(projectName + ".itemGlowEffect", false);
			file.set(projectName + ".itemLore", "");
			file.set(projectName + ".placeAbleSide", side.toString());
			file.set(projectName + ".crafting.disable", true);
			file.set(projectName + ".crafting.recipe", "xxx,xxx,xxx");
			file.set(projectName + ".crafting.index.x", Material.BEDROCK.name());
			file.set(projectName + ".projectData.entitys", "");
			file.set(projectName + ".projectData.blockList", "");
			
			int i = 0;
			for(fEntity stand : getObjectID().getPacketList()){file.set(projectName + ".projectData.entitys." + i, toString(stand, this.lib.getLocationUtil().getCenter(loc1).subtract(0, .5, 0)));i++;}
			i=0;
			for(Block b: blockList){
				Relative relative = new Relative(b.getLocation(), loc1.getBlock().getLocation());
				file.set(projectName + ".projectData.blockList." + i + ".xOffset", relative.getOffsetX());
				file.set(projectName + ".projectData.blockList." + i + ".yOffset", relative.getOffsetY());
				file.set(projectName + ".projectData.blockList." + i + ".zOffset", relative.getOffsetZ());
				file.set(projectName + ".projectData.blockList." + i + ".blockData", b.getBlockData().getAsString());
				i++;
			}
			
			file.save(filePath);
//			
			setPlaceableSide();
			try {main.getInstance().registerProeject(projectName, side);}catch(Exception fileE){fileE.printStackTrace();}
			remove();
			this.p = null;
		}else if(e.getItem().equals(stack10)){
			remove();
			this.p = null;
			delete = true;
		}else if(e.getItem().equals(stack11)){
			addItemPage1();
		}else if(e.getItem().equals(stack12)){
			addItemPage2();
		}else if(e.getItem().equals(stack16)){
			for(fEntity stand : getObjectID().getPacketList()){
				stand.setGlowing(false);
				((fArmorStand) stand).toRealEntity();
			}
			remove();
			this.p = null;
			delete = true;
		}else if(e.getItem().getType().equals(Material.GREEN_BANNER)){
			ItemMeta meta = stack15.getItemMeta();
			if(meta.getDisplayName().equalsIgnoreCase("§cBuild-Block Position:§e Top Of Block")){
				side = PlaceableSide.BOTTOM;
				meta.setDisplayName("§cBuild-Block Position:§e Bottom Of Block");
			}else if(meta.getDisplayName().equalsIgnoreCase("§cBuild-Block Position:§e Bottom Of Block")){
				side = PlaceableSide.SIDE;
				meta.setDisplayName("§cBuild-Block Position:§e Side Of Block");
			}else if(meta.getDisplayName().equalsIgnoreCase("§cBuild-Block Position:§e Side Of Block")){
				side = PlaceableSide.TOP;
				meta.setDisplayName("§cBuild-Block Position:§e Top Of Block");
			}
			e.getItem().setItemMeta(meta);
			stack15.setItemMeta(meta);
			p.updateInventory();
			p.playSound(p.getLocation(), Sound.ENTITY_ITEM_FRAME_ADD_ITEM, 1, 1);
			this.stackList.set(14, stack15);
		}
	}
	
	private void setBlock(Location loc, Material m){
		PacketContainer con = new PacketContainer(PacketType.Play.Server.BLOCK_CHANGE);
		BlockPosition pos = new BlockPosition(loc.clone().subtract(0, 1, 0).toVector());
		WrappedBlockData blockData = WrappedBlockData.createData(m);
		con.getBlockPositionModifier().write(0, pos);
		con.getBlockData().write(0, blockData);
		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(getPlayer(), con);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public void remBlock(Block b){
	 	PacketContainer con = new PacketContainer(PacketType.Play.Server.BLOCK_CHANGE);
	    BlockPosition pos = new BlockPosition(b.getLocation().toVector());
	    WrappedBlockData blockData = WrappedBlockData.createData(b.getType());
	    con.getBlockPositionModifier().write(0, pos);
	    con.getBlockData().write(0, blockData);
	    try
	    {
	      ProtocolLibrary.getProtocolManager().sendServerPacket(getPlayer(), con);
	    }
	    catch (InvocationTargetException e)
	    {
	      e.printStackTrace();
	    }
	}

	public void remove(){
		if(this.p==null) return;
		for(int x = 0; x <=10 ; x++){
			for(int y = 0; y <=10 ; y++){
				remBlock(new Relative(loc1, -x, -1, y, BlockFace.NORTH).getSecondLocation().getBlock());
			}
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
		getObjectID().setSQLAction(SQLAction.REMOVE);
		
		for(Block b : blockList){b.setType(Material.AIR);}
		if(this.commandBlock!=null){
			this.commandBlock.setType(Material.AIR);
			this.commandBlock = null;
		}

		
		for(int i = 0; i<9;i++){
			ItemStack stack = this.p.getInventory().getItem(i);
			if(stack==null) continue;
			if(stackList.contains(stack)){
				stack.setType(Material.AIR);
				this.p.getInventory().setItem(i, stack);
			}else{
				if(stack.getType().equals(Material.GREEN_BANNER)){
					if(stack.hasItemMeta()){
						if(stack.getItemMeta().hasDisplayName()){
							if(stack.getItemMeta().getDisplayName().startsWith("§cBuild-Block Position")){
								stack.setType(Material.AIR);
								this.p.getInventory().setItem(i, stack);
							}
						}
					}
				}
			}
		}
		this.p.updateInventory();
		if(this.slots==null){return;}
		for(int i = 0; i<9;i++){
			ItemStack stack = this.slots[i];
			if(stack!=null){
				this.p.getInventory().addItem(stack);
			}
		}
		this.p.updateInventory();
	}
	
	public void remove(List<fEntity> fstand){
		List<fEntity> intList = new ArrayList<fEntity>();
		for(fEntity stand : fstand){
			intList.add(stand);
			getObjectID().getPacketList().remove(stand);
			stand.setGlowing(false);
			stand.kill(getPlayer(), true);
		}
		
		for(fEntity stand : intList){
			this.entityList.remove(stand);
		}
		
		if(getObjectID().getPacketList().size()>0){
			selectSingle(getObjectID().getPacketList().get(0));
			this.entityList.add(getObjectID().getPacketList().get(0));
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		if(this.p==null){return;}
		if(!e.getPlayer().equals(this.p)){return;}
		remove();
		this.p = null;
		delete = true;
	}
	
	@EventHandler
	public void onBlockClick(BlockPlaceEvent e){
		if(this.p==null){return;}
		if(!e.getPlayer().equals(this.p)){return;}
		if(e.getBlock()==null) return;
		if(e.getBlock().getType().equals(Material.COMMAND_BLOCK)){
			if(this.commandBlock!=null) this.commandBlock.setType(Material.AIR);
			this.commandBlock = e.getBlock();
		}
		if(!e.getBlock().getType().equals(Material.OAK_BUTTON)){
			this.blockList.add(e.getBlock());
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e){
		if(this.p==null){return;}
		if(!e.getPlayer().equals(this.p)){return;}
		if(e.getBlock()==null){return;}
		if(this.commandBlock!=null && e.getBlock().equals(this.commandBlock)) this.commandBlock = null; 
		if(!e.getBlock().getType().equals(Material.OAK_BUTTON)){
		if(!blockList.contains(e.getBlock())){e.setCancelled(true); return;}
			blockList.remove(e.getBlock());
		}
	}

	@EventHandler
	public void onQuit(PlayerKickEvent e){
		if(this.p==null) return;
		if(e.getPlayer().equals(this.p)) return;
		remove();
		this.p = null;
		delete = true;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e){
		if(this.p==null) return;
		if(!e.getWhoClicked().equals(this.p)) return;
		if(e.getCursor()!=null){if(stackList.contains(e.getCursor())){e.setCancelled(true);return;}}
		if(e.getCurrentItem()!=null){if(stackList.contains(e.getCurrentItem())){e.setCancelled(true);return;}}
	}
	
	@EventHandler
	public void onTelePort(PlayerTeleportEvent e){
		if(this.p==null){return;}
		if(!e.getPlayer().equals(this.p)){return;}
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onScorll(PlayerItemHeldEvent e){
		if(getPlayer()==null) return;
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
				if(forward)face = face.getOppositeFace();
				if(entityList.isEmpty()) return;
				for(fEntity stand : entityList){
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
					teleport(stand,relative);
				}
			}
		}else if(is1.equals(stack4)){
			if(getPlayer().isSneaking()){
				boolean forward = false;
				e.setCancelled(true);
				int i = e.getNewSlot();
				if(i==3){return;}
				e.getPlayer().getInventory().setHeldItemSlot(e.getPreviousSlot());
				if(e.getNewSlot()>e.getPreviousSlot()){forward=true;}
				if(entityList.isEmpty()) return;
				if(forward){
					for(final fEntity stand : entityList){
						teleport(stand,new Relative(stand.getLocation(), 0, 1, 0, BlockFace.DOWN),stand.getLocation().getYaw()+(dlist[o]/2));
						Bukkit.getScheduler().runTaskLater(main.getInstance(), new Runnable() {
							@Override
							public void run() {
								teleport(stand,new Relative(stand.getLocation(), 0, -1, 0, BlockFace.DOWN),stand.getLocation().getYaw()+(dlist[o]/2));
							}
						}, 2);
					}
					return;
				}else{
					for(final fEntity stand : entityList){
						teleport(stand,new Relative(stand.getLocation(), 0, 1, 0, BlockFace.DOWN),stand.getLocation().getYaw()-(dlist[o]/2));
						Bukkit.getScheduler().runTaskLater(main.getInstance(), new Runnable() {
							@Override
							public void run() {
								teleport(stand,new Relative(stand.getLocation(), 0, -1, 0, BlockFace.DOWN),stand.getLocation().getYaw()-(dlist[o]/2));
							}
						}, 2);
					}
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
				if(entityList.isEmpty()) return;
				BodyPart part = this.part;
				String rotType = sy[t];
				double degres = (double) dlist[o];
				
				for(fEntity entity : entityList){
					fArmorStand stand = (fArmorStand) entity;
					
					EulerAngle angle = stand.getPose(part);
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
					stand.setPose(lib.getLocationUtil().degresstoRad(angle), part);
					stand.update(this.p);
					stand.getObjID().setSQLAction(SQLAction.NOTHING);
				}
			}
		}else if(is1.equals(stack14)){
			if(getPlayer().isSneaking()){
				boolean forward = false;
				e.setCancelled(true);
				int i = e.getNewSlot();
				if(i==2){return;}
				e.getPlayer().getInventory().setHeldItemSlot(e.getPreviousSlot());
				if(e.getNewSlot()>e.getPreviousSlot()){forward=true;}
				if(entityList.isEmpty()) return;
				if(forward){
					Location center = this.lib.getLocationUtil().getCenter(loc1).subtract(0, .5, 0);
					BlockFace face = BlockFace.WEST;
				    for(final fEntity stand : entityList){
				    	Relative relative = new Relative(stand.getLocation(), center);
				    	Location armorLocation = lib.getLocationUtil().getRelativ(center, face, -relative.getOffsetZ(), -relative.getOffsetX());
				    	armorLocation.add(0, relative.getOffsetY(), 0);
				    	armorLocation.setYaw(lib.getLocationUtil().FaceToYaw(relative.getFace())+90);
				    	stand.teleport(armorLocation);
				    	stand.update(getPlayer());
					}
					getObjectID().setSQLAction(SQLAction.NOTHING);
					return;
				}else{
					Location center = this.lib.getLocationUtil().getCenter(loc1).subtract(0, .5, 0);
					BlockFace face = BlockFace.WEST.getOppositeFace();
				    for(final fEntity stand : entityList){
				    	Relative relative = new Relative(stand.getLocation(), center);
				    	Location armorLocation = lib.getLocationUtil().getRelativ(center, face, -relative.getOffsetZ(), -relative.getOffsetX());
				    	armorLocation.add(0, relative.getOffsetY(), 0);
				    	armorLocation.setYaw(lib.getLocationUtil().FaceToYaw(relative.getFace())-90);
				    	stand.teleport(armorLocation);
				    	stand.update(getPlayer());
					}
					getObjectID().setSQLAction(SQLAction.NOTHING);
					return;
				}
			}
		}
	}
	
	public void selectSingle(fEntity stand){
		for(fEntity entity : entityList){
			entity.setGlowing(false);
			entity.update(getPlayer());
		}
		if(stand == null) return;
		entityList.clear();
		stand.setGlowing(true);
		stand.update(getPlayer());
		getObjectID().setSQLAction(SQLAction.NOTHING);
		entityList.add(stand);
	}
	
	private void sendMessage(String builder){
		PacketContainer con = new PacketContainer(PacketType.Play.Server.CHAT);
		con.getChatComponents().write(0, WrappedChatComponent.fromText(builder));
		con.getChatTypes().write(0, ChatType.GAME_INFO);
		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(getPlayer(), con);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	
	private void teleport(fEntity entity,Relative relative){
		if(!isInside(relative.getSecondLocation(), loc2, loc3)) return;
		Location loc = relative.getSecondLocation();
		loc.setYaw(entity.getLocation().getYaw());
		entity.teleport(loc);
		entity.update(getPlayer());
		id.setSQLAction(SQLAction.NOTHING);
	}
	
	public boolean isInside(Location loc, Location l1, Location l2) {
        int x1 = Math.min(l1.getBlockX(), l2.getBlockX());
        int z1 = Math.min(l1.getBlockZ(), l2.getBlockZ());
        int x2 = Math.max(l1.getBlockX(), l2.getBlockX());
        int z2 = Math.max(l1.getBlockZ(), l2.getBlockZ());
        return loc.getX() >= x1 && loc.getX() <= x2 && loc.getZ() >= z1 && loc.getZ() <= z2;
	}
	
	public void teleport(fEntity entity, Relative relative, float yaw){
		Location loc = relative.getSecondLocation();
		loc.setYaw(yaw);
		entity.teleport(loc);
		entity.update(getPlayer());
		id.setSQLAction(SQLAction.NOTHING);
	}
	public List<fEntity> getStand() {return entityList;}
	public void addEnitty(fEntity entity){this.entityList.add(entity);}
	
	public void setBlocks(List<Location> blockList2) {
		for(Location loc : blockList2){
			this.blockList.add(loc.getBlock());
		}
	}
	
	public void setPlaceableSide(){
		for(Project project : FurnitureLib.getInstance().getFurnitureManager().getProjects()){
			if(project.getName().toLowerCase().equalsIgnoreCase(projectName)){
				project.setPlaceableSide(side);
			}
		}
	}
	
	public String getPlaceAbleSide(){
		String returnStr = "§cBuild-Block Position:";
		String side = "§e Top Of Block";
		for(Project project : FurnitureLib.getInstance().getFurnitureManager().getProjects()){
			if(project.getName().toLowerCase().equalsIgnoreCase(projectName)){
				switch (project.getPlaceableSide()) {
				case BOTTOM:side = "§e Bottom Of Block";break;
				case TOP:break;
				case SIDE:side = "§e Side Of Block";break;
				default:break;
				}
			}
		}
		return returnStr + side;
	}
}
