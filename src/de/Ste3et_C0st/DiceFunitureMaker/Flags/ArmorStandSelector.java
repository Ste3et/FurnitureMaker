package de.Ste3et_C0st.DiceFunitureMaker.Flags;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import de.Ste3et_C0st.DiceFurnitureMaker.ProjektModel;
import de.Ste3et_C0st.DiceFurnitureMaker.main;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.entity.fArmorStand;

public class ArmorStandSelector implements Listener{

	private Player p;
	private ObjectID id;
	private boolean enable = true;
	private Inventory inv = null;
	private int perPage = 45, side = 1, maxSide = 0;
	private ItemStack stack1,stack2,stack3,stack4;
	private fArmorStand fstand;
	private ProjektModel model;
	
	public ArmorStandSelector(ProjektModel model){
		this.id = model.getObjectID();
		this.p = model.getPlayer();
		this.fstand = model.getStand();
		this.model = model;
		this.inv = Bukkit.createInventory(null, perPage+9, "Select ArmorStand");
		stack1=new ItemStack(Material.TIPPED_ARROW, 1);
		stack3=new ItemStack(Material.TIPPED_ARROW, 1);
		stack2=new ItemStack(Material.PAPER);
		stack4=new ItemStack(Material.BARRIER);
		PotionMeta meta = (PotionMeta) stack1.getItemMeta();
		meta.setBasePotionData(new PotionData(PotionType.REGEN));
		meta.addCustomEffect(new PotionEffect(PotionEffectType.HEAL, 1, 1), true);
		meta.setDisplayName("§cPrev Page");
		stack1.setItemMeta(meta);
		
		meta = (PotionMeta) stack3.getItemMeta();
		meta.setBasePotionData(new PotionData(PotionType.LUCK));
		meta.addCustomEffect(new PotionEffect(PotionEffectType.LUCK, 1, 1), true);
		meta.setDisplayName("§2Next Page");
		stack3.setItemMeta(meta);
		
		double d =(double) this.id.getPacketList().size();
		double b =(double) perPage;
		double l = Math.ceil(d/b);
		int u = (int) l;
		this.maxSide = u;
		ItemMeta m = stack2.getItemMeta();
		m.setDisplayName("§6Page[§31" + "§6/§3" + u + "§6]");
		stack2.setItemMeta(m);
		
		m = stack4.getItemMeta();
		m.setDisplayName("§cRemove ArmorStand");
		stack4.setItemMeta(m);
		
		inv.setItem(inv.getSize()-6, stack1);
		inv.setItem(inv.getSize()-5, stack2);
		inv.setItem(inv.getSize()-4, stack3);
		inv.setItem(inv.getSize()-9, stack4);
		
		int j = 0;
		for(fArmorStand stand : this.id.getPacketList()){
			if(j<perPage){
				ItemStack stack = new ItemStack(Material.ARMOR_STAND);
				ItemMeta smeta = stack.getItemMeta();
				smeta.setDisplayName("§1ArmorStand: [§4" + stand.getEntityID() + "§1]");
				if(stand.getEntityID() == fstand.getEntityID()){
					smeta.addEnchant(Enchantment.KNOCKBACK, 1, false);
				}
				stack.setItemMeta(smeta);
				inv.setItem(j, stack);
				j++;
			}
		}
		this.p.openInventory(inv);
		Bukkit.getPluginManager().registerEvents(this, main.getInstance());
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e){
		if(e.getClickedInventory()==null)return;
		if(setEnable(false)) return;
		if(!e.getClickedInventory().equals(inv)) return;
		e.setCancelled(true);
		if(e.getCurrentItem()==null) return;
		if(e.getCurrentItem().getType()==null) return;
		switch (e.getCurrentItem().getType()) {
		case TIPPED_ARROW:
			if(e.getSlot()==inv.getSize()-6){
				if(side==1) return;
				int start = perPage*(side-1)-perPage;
				int end = perPage*(side-1);
				int j = 0;
				for(int i = start; i<end; i++){
					if(i<model.getObjectID().getPacketList().size()){
						ItemStack stack = new ItemStack(Material.ARMOR_STAND);
						ItemMeta smeta = stack.getItemMeta();
						smeta.setDisplayName("§1ArmorStand: [§4" + model.getObjectID().getPacketList().get(i).getEntityID() + "§1]");
						if(model.getObjectID().getPacketList().get(i).getEntityID() == fstand.getEntityID()){
							smeta.addEnchant(Enchantment.KNOCKBACK, 1, false);
						}
						stack.setItemMeta(smeta);
						inv.setItem(j, stack);
					}else{
						inv.setItem(j, new ItemStack(Material.AIR));
					}
					j++;
				}
				p.updateInventory();
				this.side-=1;
			}else if(e.getSlot()==inv.getSize()-4){
				if(side+1>maxSide) return;
				int start = perPage*(side+1)-perPage;
				int end = perPage*(side+1);
				int j = 0;
				for(int i = start; i<end; i++){
					if(i<model.getObjectID().getPacketList().size()){
						ItemStack stack = new ItemStack(Material.ARMOR_STAND);
						ItemMeta smeta = stack.getItemMeta();
						smeta.setDisplayName("§1ArmorStand: [§4" + model.getObjectID().getPacketList().get(i).getEntityID() + "§1]");
						if(model.getObjectID().getPacketList().get(i).getEntityID() == fstand.getEntityID()){
							smeta.addEnchant(Enchantment.KNOCKBACK, 1, false);
						}
						stack.setItemMeta(smeta);
						inv.setItem(j, stack);
					}else{
						inv.setItem(j, new ItemStack(Material.AIR));
					}
					j++;
				}
				p.updateInventory();
				this.side+=1;
			}
			break;
		case ARMOR_STAND:
			if(check(e.getCurrentItem())==this.fstand.getEntityID()){return;}
			getStand(check(e.getCurrentItem()));
			break;
		case BARRIER:
			if(fstand==null) return;
			int id = fstand.getEntityID();
			fArmorStand stand2 = null;
			for(fArmorStand stand : model.getObjectID().getPacketList()){
				if(stand.getEntityID()!=id){ model.select(stand); stand2 = stand; break;}
			}
			model.remove(fstand, stand2);
			p.closeInventory();
			this.setEnable(false);
		default:
			break;
		}
	}
	
	public int check(ItemStack stack){
		int i = 0;
		if(!stack.hasItemMeta()) return i;
		if(!stack.getItemMeta().hasDisplayName()) return i;
		String s = stack.getItemMeta().getDisplayName();
		s = ChatColor.stripColor(s);
		s = s.replace("ArmorStand: [", "");
		s = s.replace("]", "");
		i = Integer.parseInt(s);
		return i;
	}
	
	public void getStand(int i){
		this.p.closeInventory();
		setEnable(false);
		fArmorStand stand = null;
		for(fArmorStand s : this.model.getObjectID().getPacketList()){
			if(s!=null){
				if(s.getEntityID()==i){
					stand = s;
					break;
				}
			}
		}
		this.model.select(stand);
	}

	public boolean isEnable() {
		return enable;
	}

	public boolean setEnable(boolean enable) {
		this.enable = enable;
		return enable;
	}
}
