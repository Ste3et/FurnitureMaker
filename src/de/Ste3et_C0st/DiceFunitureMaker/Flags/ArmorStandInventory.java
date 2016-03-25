package de.Ste3et_C0st.DiceFunitureMaker.Flags;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.main.entity.fArmorStand;

public class ArmorStandInventory implements Listener{

	private Inventory inv = null;
	private ItemStack stack;
	private boolean enable = true;
	private fArmorStand stand;
	private Player player;
	
	public ArmorStandInventory(fArmorStand stand, Player player){
		if(stand == null) return;
		if(player == null) return;
		if(!enable) return;
		this.stand = stand;
		this.player = player;
		this.inv = Bukkit.createInventory(null, 6*9, "Edit: " + stand.getEntityID() + " Inventory");
		stack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName("§c");
		stack.setItemMeta(meta);
		for(int i = 0; i<inv.getSize(); i++){
			switch (i) {
			case 12:inv.setItem(i, stand.getHelmet());break;
			case 20:inv.setItem(i, stand.getItemInMainHand());break;
			case 21:inv.setItem(i, stand.getChestPlate());break;
			case 22:inv.setItem(i, stand.getItemInOffHand());break;
			case 24:inv.setItem(i, getNameTag());break;
			case 30:inv.setItem(i, stand.getLeggings());break;
			case 39:inv.setItem(i, stand.getBoots());break;
			default:inv.setItem(i, stack);break;
			}
		}

		player.getPlayer().openInventory(inv);
		Bukkit.getPluginManager().registerEvents(this, main.getInstance());
	}
	
	public ItemStack getNameTag(){
		ItemStack stack = new ItemStack(Material.AIR);
		if(stand.getName().equalsIgnoreCase("")){return stack;}
		if(!stand.isCustomNameVisible()){return stack;}
		stack = new ItemStack(Material.NAME_TAG, 1, (short) 0);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(stand.getCustomName());
		stack.setItemMeta(meta);
		return stack;
	}
	
	public void setName(ItemStack stack){
		if(stack==null){
			if(!stand.getCustomName().equalsIgnoreCase("")){stand.setName("");stand.setNameVasibility(false);return;}
			return;
		}
		if(stack.getType()==null){
			if(!stand.getCustomName().equalsIgnoreCase("")){stand.setName("");stand.setNameVasibility(false);return;}
			return;
		}
		if(stack.hasItemMeta()){
			if(stack.getItemMeta().hasDisplayName()){
				if(stack.getType().equals(Material.NAME_TAG)){
					stand.setName(ChatColor.translateAlternateColorCodes('&', stack.getItemMeta().getDisplayName()));
					stand.setNameVasibility(true);
					return;
				}
			}
		}
		stand.getWorld().dropItemNaturally(stand.getLocation(), stack);
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
	
	@EventHandler
	public void clickWindow(InventoryClickEvent e){
		if(e.getClickedInventory()==null)return;
		if(enable=false) return;
		if(!e.getClickedInventory().equals(inv)) return;
		if(e.getCurrentItem().equals(stack)){e.setCancelled(true);return;}
	}
}
