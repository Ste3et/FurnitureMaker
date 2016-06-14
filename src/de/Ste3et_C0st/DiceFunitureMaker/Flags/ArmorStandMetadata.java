package de.Ste3et_C0st.DiceFunitureMaker.Flags;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.Ste3et_C0st.DiceFurnitureMaker.main;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.main.entity.fArmorStand;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public class ArmorStandMetadata implements Listener{
	
	private Inventory inv = null;
	private Player player = null;
	private fArmorStand stand = null;
	private boolean enable = true;
	private ObjectID id;
	public ArmorStandMetadata(fArmorStand stand, Player player, ObjectID id){
		if(stand == null || player == null || !enable) return;
		this.player = player;
		this.stand = stand;
		this.id = id;
		this.inv = Bukkit.createInventory(null, 18, "Edit: " + stand.getEntityID());
		this.inv.setItem(0, getStack(Material.RAW_FISH, (byte) 3, 1, Arrays.asList(
				"§2Change the size",
				"§2from the ArmorStand",
				"§2and make it smaller/bigger"
				), "§aSize"));
		this.inv.setItem(1, getStack(Material.POTION, (byte) 0, 1, Arrays.asList(
				"§2Make the ArmorStand",
				"§2Visible/§cInVisible"
				), "§aVisibility"));
		this.inv.setItem(2, getStack(Material.STICK, (byte) 0, 1, Arrays.asList(
				"§2Give/Remove Arms",
				"§2from ArmorStand"
				), "§aArms"));
		this.inv.setItem(3, getStack(Material.IRON_PLATE, (byte) 0, 1, Arrays.asList(
				"§2Add/Remove BasePlate from",
				"§2the ArmorStand"
				), "§aBasePlate"));
		this.inv.setItem(4, getStack(Material.BARRIER, (byte) 0, 1, Arrays.asList(
				"§2Enable/Diable the marker from",
				"§2the ArmorStand"
				), "§aMarker"));
		this.inv.setItem(5, getStack(Material.FLINT_AND_STEEL, (byte) 0, 1, Arrays.asList(
				"§2Turn on/off the",
				"§2fire on the ArmorStand"
				), "§aFire"));
		this.inv.setItem(6, getStack(Material.SADDLE, (byte) 0, 1, Arrays.asList(
				"§2Turn on/off the Mount",
				"§2for this ArmorStand"
				), "§aMount"));
		this.inv.setItem(7, getStack(Material.REDSTONE_TORCH_ON, (byte) 0, 1, Arrays.asList(
				"§2Turn on/off the light",
				"§2From the ArmorStand"), "§aLight"));
		this.inv.setItem(8, getStack(Material.CHEST, (byte) 0, 1, Arrays.asList(
				"§2Open a inventory"), "§aInventory " + getInvSize()));
		
		this.inv.setItem(9, isEnable("Size"));
		this.inv.setItem(10, isEnable("Visibility"));
		this.inv.setItem(11, isEnable("Arms"));
		this.inv.setItem(12, isEnable("BasePlate"));
		this.inv.setItem(13, isEnable("Marker"));
		this.inv.setItem(14, isEnable("Fire"));
		this.inv.setItem(15, isEnable("Mount"));
		this.inv.setItem(16, isEnable("Light"));
		this.inv.setItem(17, isEnable("Inventory"));
		Bukkit.getPluginManager().registerEvents(this, main.getInstance());
		player.openInventory(this.inv);
	}
	
	private int getInvSize(){
		int i = 9;
		for(fEntity stand : this.id.getPacketList()){
			if(stand.getName().startsWith("#Inventory:")){
				String[] str = stand.getName().split(":");
				i = Integer.parseInt(str[2].replace("#", ""));
				break;
			}
		}
		return i;
	}
	
	public ItemStack isEnable(String arg){
		ItemStack on = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 13);
		ItemStack off = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
		ItemMeta meta = on.getItemMeta();
		meta.setDisplayName("§c");
		on.setItemMeta(meta);off.setItemMeta(meta);
		switch (arg) {
		case "Size":
			if(stand.isSmall()) return on;
			return off;
		case "Visibility":
			if(stand.isInvisible()) return off;
			return on;
		case "Arms":
			if(stand.hasArms()) return on;
			return off;
		case "BasePlate":
			if(stand.hasBasePlate()) return on;
			return off;
		case "Marker":
			if(!stand.isMarker()) return on;
			return off;
		case "Fire":
			if(stand.isFire()) return on;
			return off;
		case "Mount":
			if(stand.getName().startsWith("#Mount:")) return on;
			return off;
		case "Light":
			if(stand.getName().startsWith("#Light:")) return on;
			return off;
		case "Inventory":
			if(stand.getName().startsWith("#Inventory:")) return on;
			return off;
		default:return null;
		}
	}
	
	
	public ItemStack getStack(Material m, byte d,int i, List<String> args, String n){
		ItemStack stack = new ItemStack(m, i, d);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(n);
		meta.setLore(args);
		stack.setItemMeta(meta);
		return stack;
	}
	
	@EventHandler
	public void closeWindow(InventoryCloseEvent e){
		if(e.getInventory()==null)return;
		if(enable=false) return;
		if(e.getInventory().equals(inv)) enable = false;
	}
	
	@EventHandler
	public void clickWindow(InventoryClickEvent e){
		if(e.getClickedInventory()==null)return;
		if(enable=false) return;
		if(!e.getClickedInventory().equals(inv)) return;
		e.setCancelled(true);
		if(e.getCurrentItem()==null) return;
		if(e.getCurrentItem().getType()==null) return;
		if(!e.getCurrentItem().getType().equals(Material.STAINED_GLASS_PANE)
		 &&!e.getCurrentItem().getType().equals(Material.CHEST)) return;
		switch (e.getSlot()) {
		case 9:
			if(e.getCurrentItem().getDurability()==13){
				e.getCurrentItem().setDurability((short) 14);
				this.stand.setSmall(false);
				break;
			}else{
				e.getCurrentItem().setDurability((short) 13);
				this.stand.setSmall(true);
				break;
			}
		case 10:
			if(e.getCurrentItem().getDurability()==13){
				e.getCurrentItem().setDurability((short) 14);
				this.stand.setInvisible(true);
				break;
			}else{
				e.getCurrentItem().setDurability((short) 13);
				this.stand.setInvisible(false);
				break;
			}			
		case 11:
			if(e.getCurrentItem().getDurability()==13){
				e.getCurrentItem().setDurability((short) 14);
				this.stand.setArms(false);
				break;
			}else{
				e.getCurrentItem().setDurability((short) 13);
				this.stand.setArms(true);
				break;
			}
		case 12:
			if(e.getCurrentItem().getDurability()==13){
				e.getCurrentItem().setDurability((short) 14);
				this.stand.setBasePlate(false);
				break;
			}else{
				e.getCurrentItem().setDurability((short) 13);
				this.stand.setBasePlate(true);
				break;
			}
		case 13:
			if(e.getCurrentItem().getDurability()==13){
				e.getCurrentItem().setDurability((short) 14);
				this.stand.setMarker(true);
				break;
			}else{
				e.getCurrentItem().setDurability((short) 13);
				this.stand.setMarker(false);
				break;
			}
		case 14:
			if(e.getCurrentItem().getDurability()==13){
				e.getCurrentItem().setDurability((short) 14);
				this.stand.setFire(false);
				break;
			}else{
				e.getCurrentItem().setDurability((short) 13);
				this.stand.setFire(true);
				break;
			}
		case 15:
			if(!canBeSat("#Mount")) return;
			if(e.getCurrentItem().getDurability()==13){
				e.getCurrentItem().setDurability((short) 14);
				this.stand.setName("§c");
				break;
			}else{
				e.getCurrentItem().setDurability((short) 13);
				this.stand.setName("#Mount:"+UUID.randomUUID()+"#");
				break;
			}
		case 16:
			if(!canBeSat("#Light:")) return;
			if(e.getCurrentItem().getDurability()==13){
				e.getCurrentItem().setDurability((short) 14);
				this.stand.setName("§c");
				break;
			}else{
				e.getCurrentItem().setDurability((short) 13);
				this.stand.setName("#Light:"+UUID.randomUUID()+":off#");
				break;
			}
		case 8:
			ItemMeta meta = e.getCurrentItem().getItemMeta();
			int slots = Integer.parseInt(meta.getDisplayName().split(" ")[1]);
			int slotsNew = 0;
			if(slots==9){
				slotsNew = 18;
			}else if(slots==18){
				slotsNew = 27;
			}else if(slots==27){
				slotsNew = 36;
			}else if(slots==36){
				slotsNew = 45;
			}else if(slots==45){
				slotsNew = 54;
			}else if(slots==54){
				slotsNew = 9;
			}
			meta.setDisplayName(meta.getDisplayName().replace(slots+"", slotsNew+""));
			this.stand.setName("#Inventory:"+UUID.randomUUID()+":"+slotsNew+"#");
			e.getCurrentItem().setItemMeta(meta);
		case 17:
			if(!canBeSat("#Inventory")) return;
			int slot = Integer.parseInt(e.getInventory().getItem(8).getItemMeta().getDisplayName().split(" ")[1]);
			if(e.getCurrentItem().getDurability()==13){
				e.getCurrentItem().setDurability((short) 14);
				this.stand.setName("§c");
				break;
			}else{
				boolean b = true;
				for(fEntity stand : this.id.getPacketList()){
					if(stand.getName().startsWith("#Inventory:")){
						b=false;
					}
				}
				if(b){
					e.getCurrentItem().setDurability((short) 13);
					this.stand.setName("#Inventory:"+UUID.randomUUID()+":"+slot+"#");
				}
				break;
			}
		default:
			break;
		}
		this.player.updateInventory();
		this.stand.update(this.player);
		this.stand.getObjID().setSQLAction(SQLAction.NOTHING);
	}
	
	public boolean canBeSat(String s){
		if(this.stand.getName().equalsIgnoreCase("")){return true;}
		if(this.stand.getName().startsWith(s)){return true;}
		if(this.stand.getName().equalsIgnoreCase("§c")){return true;}
		return false;
	}
}