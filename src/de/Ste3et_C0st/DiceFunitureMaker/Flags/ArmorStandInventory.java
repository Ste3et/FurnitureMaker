package de.Ste3et_C0st.DiceFunitureMaker.Flags;

import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.Ste3et_C0st.DiceFurnitureMaker.main;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.main.entity.fArmorStand;

public class ArmorStandInventory implements Listener{

	private Inventory inv = null;
	private ItemStack stack, stack2;
	private boolean enable = true;
	private fArmorStand stand;
	private Player player;
	
	InventoryView view = null;
	public ArmorStandInventory(fArmorStand stand, Player player){
		if(stand == null) return;
		if(player == null) return;
		if(!enable) return;
		this.stand = stand;
		this.player = player;
		HumanEntity entity = this.player;
		this.inv = Bukkit.createInventory(null, 6*9, "Edit: " + stand.getEntityID() + " Inventory");
		stack = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
		this.view = entity.openInventory(inv);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName("§c");
		stack.setItemMeta(meta);
		player.getPlayer().openInventory(inv);
		getNameTag();
		for(int i = 0; i<inv.getSize(); i++){
			switch (i) {
			case 12:makeNull(i, stand.getHelmet());break;
			case 20:makeNull(i, stand.getItemInMainHand());break;
			case 21:makeNull(i, stand.getChestPlate());break;
			case 22:makeNull(i, stand.getItemInOffHand());break;
			case 24:makeNull(i, getNameTag());break;
			case 30:makeNull(i, stand.getLeggings());break;
			case 39:makeNull(i, stand.getBoots());break;
			default:view.getTopInventory().setItem(i, stack);break;
			}
		}
		Bukkit.getPluginManager().registerEvents(this, main.getInstance());
	}
	
	public void makeNull(int i, ItemStack stack){
		if(stack==null) return;
		stack.setAmount(1);
		this.view.getTopInventory().setItem(i, stack.clone());
	}
	
	public ItemStack getNameTag(){
		stack2 = new ItemStack(Material.AIR);
		if(stand.getName().equalsIgnoreCase("")){return stack2;}
		if(stand.getName().startsWith("#Mount:") || stand.getName().startsWith("#Light:") || stand.getName().startsWith("#Inventory:")){
			stack2 = new ItemStack(Material.RED_STAINED_GLASS_PANE);
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName("§cDISABLED");
			meta.setLore(Arrays.asList("§4Reason: Event is enable!"));
			stack2.setItemMeta(meta);
			return stack2;
		}
		stack2 = new ItemStack(Material.NAME_TAG, 1, (short) 0);
		ItemMeta meta = stack2.getItemMeta();
		meta.setDisplayName(stand.getCustomName());
		stack2.setItemMeta(meta);
		if(meta.hasDisplayName()){
			if(stand.getName().startsWith("#Mount:")){
				stack2 = new ItemStack(Material.RED_STAINED_GLASS_PANE);
				meta = stack2.getItemMeta();
				meta.setDisplayName("§cYou cannot set the Name for this ArmorStand");
				stack2.setItemMeta(meta);
			}
		}
		return stack2;
	}
	
	public void setName(ItemStack stack){
		if(stack==null||stack.getType()==null||stack.getType().equals(Material.AIR)){
			
			if(!stand.getCustomName().equalsIgnoreCase("")){
				stand.setName("");
				stand.setNameVasibility(false);
			return;}
			return;
		}
		if(stack.hasItemMeta()){
			if(stack.getItemMeta().hasDisplayName()){
				if(stack.getType().equals(Material.NAME_TAG)){
					if(!stack.getItemMeta().getDisplayName().startsWith("#Mount:")){
						stand.setName(ChatColor.translateAlternateColorCodes('&', stack.getItemMeta().getDisplayName()));
						if(!stack.getItemMeta().getDisplayName().startsWith("/") && !stack.getItemMeta().getDisplayName().equalsIgnoreCase("%CAR_MIDDLE%")){
							stand.setNameVasibility(true);
						}
						return;
					}
				}
			}
		}else{
			if(stack.getType().equals(Material.NAME_TAG)){
				stand.setName("");
				stand.setNameVasibility(false);
				stand.update();
				return;
			}
		}
	}
	
	@EventHandler
	public void closeWindow(InventoryCloseEvent e){
		if(e.getInventory()==null)return;
		if(enable=false) return;
		if(e.getInventory().equals(inv)){
			enable = false;
			for(int i = 0; i<inv.getSize();i++){
				switch (i) {
				case 12:updateArmorStand(12, 5);
				case 20:updateArmorStand(20, 0);
				case 21:updateArmorStand(21, 4);
				case 22:updateArmorStand(22, 1);
				case 24:setName(inv.getItem(24));
				case 30:updateArmorStand(30, 3);
				case 39:updateArmorStand(39, 2);
				default:break;
				}
			}
			stand.update(player);
			stand.getObjID().setSQLAction(SQLAction.NOTHING);
		}
	}
	
	public void updateArmorStand(int pos, int slot){
		if(inv.getItem(pos)==null){stand.getInventory().setSlot(slot, new ItemStack(Material.AIR));}
		else if(inv.getItem(pos).getType()==null){stand.getInventory().setSlot(slot, new ItemStack(Material.AIR));}
		else if(inv.getItem(pos).getType().equals(Material.AIR)){stand.getInventory().setSlot(slot, new ItemStack(Material.AIR));}
		else if(inv.getItem(pos).equals(stack)){return;}
		else{stand.getInventory().setSlot(slot, inv.getItem(pos));}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void clickWindow(InventoryClickEvent e){
		if(e.getClickedInventory()==null)return;
		if(enable=false) return;
		if(!e.getClickedInventory().equals(inv)) return;
		if(e.getCurrentItem()!=null&&e.getCurrentItem().equals(this.stack)){e.setCancelled(true); return;}
		if(e.getCurrentItem()!=null&&e.getCurrentItem().equals(this.stack2)){
			this.inv.setItem(e.getSlot(), new ItemStack(Material.AIR));
			e.setCancelled(true); return;
		}
		if(e.getAction()==null){e.setCancelled(true); return;}
		ItemStack stack = e.getCursor().clone();
		stack.setAmount(1);
		e.setCancelled(true);
		switch (e.getAction()) {
		case PLACE_ALL:this.inv.setItem(e.getSlot(), stack);return;
		case PLACE_ONE:this.inv.setItem(e.getSlot(), stack);return;
		case PLACE_SOME:this.inv.setItem(e.getSlot(), stack);return;
		case COLLECT_TO_CURSOR:this.inv.setItem(e.getSlot(), stack);return;
		case PICKUP_ALL:this.inv.setItem(e.getSlot(), new ItemStack(Material.AIR));return;
		case PICKUP_HALF:this.inv.setItem(e.getSlot(), new ItemStack(Material.AIR));return;
		case PICKUP_ONE:this.inv.setItem(e.getSlot(), new ItemStack(Material.AIR));return;
		case PICKUP_SOME:this.inv.setItem(e.getSlot(), new ItemStack(Material.AIR));return;
		case SWAP_WITH_CURSOR:this.inv.setItem(e.getSlot(), stack);return;
		default: return;
		}
	}
}
