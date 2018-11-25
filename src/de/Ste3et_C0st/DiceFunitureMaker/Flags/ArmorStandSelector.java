package de.Ste3et_C0st.DiceFunitureMaker.Flags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
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
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public class ArmorStandSelector implements Listener{

	private Player p;
	private ObjectID id;
	private boolean enable = true, multiSelect = false;
	private Inventory inv = null;
	private int perPage = 45, side = 1, maxSide = 0;
	private ItemStack stack1,stack2,stack3,stack4,stack5,stack6;
	private List<fEntity> fstand = new ArrayList<fEntity>();
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
		stack5=new ItemStack(Material.STONE_SLAB);
		stack6=new ItemStack(Material.EMERALD);
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
		
		m = stack5.getItemMeta();
		m.setDisplayName("§7Select Mode: §2Single Select");
		stack5.setDurability((short) 4);
		stack5.setItemMeta(m);
		
		m = stack6.getItemMeta();
		m.setDisplayName("§6Multiselect Tool");
		m.setLore(Arrays.asList("§eLeftclick: §6Select All Armorstands", "§eRightclick: §cDeselect All Armorstands"));
		stack6.setItemMeta(m);
		
		inv.setItem(inv.getSize()-6, stack1);
		inv.setItem(inv.getSize()-5, stack2);
		inv.setItem(inv.getSize()-4, stack3);
		inv.setItem(inv.getSize()-9, stack4);
		inv.setItem(inv.getSize()-1, stack5);
		
		int j = 0;
		for(fEntity stand : this.id.getPacketList()){
			if(j<perPage){
				ItemStack stack = new ItemStack(Material.ARMOR_STAND);
				ItemMeta smeta = stack.getItemMeta();
				smeta.setDisplayName("§1ArmorStand: [§4" + stand.getEntityID() + "§1]");
				if(fstand.contains(stand)){smeta.addEnchant(Enchantment.KNOCKBACK, 1, false);}
				
				List<String> lore = new ArrayList<String>();
				
				lore.add("§6Metadata:");
				lore.add(getInfo("§7- Invisible",stand.isInvisible()));
				lore.add(getInfo("§7- Fire",stand.isFire()));
				if(stand instanceof fArmorStand){
					fArmorStand as = (fArmorStand) stand;
					lore.add(getInfo("§7- Small",as.isSmall()));
					lore.add(getInfo("§7- Arms",as.hasArms()));
					lore.add(getInfo("§7- BasePlate",as.hasBasePlate()));
					lore.add(getInfo("§7- Marker",as.isMarker()));
				}
				lore.add(getInfo("§7- Name", stand.getName()));
				lore.add("§6Inventory:");
				lore.add(getInfo("§7- Main Hand", stand.getItemInMainHand()));
				lore.add(getInfo("§7- Off Hand", stand.getItemInOffHand()));
				lore.add(getInfo("§7- Helmet", stand.getHelmet()));
				smeta.setLore(lore);
				stack.setItemMeta(smeta);
				inv.setItem(j, stack);
				j++;
			}
		}
		this.p.openInventory(inv);
		Bukkit.getPluginManager().registerEvents(this, main.getInstance());
	}
	
	public String getInfo(String s, ItemStack stack){
		if(stack==null||stack.getType()==null||stack.getType().equals(Material.AIR)) return s + ":§c " + Material.AIR.name();
		return s + ":§a " + stack.getType().name() + ":" + stack.getDurability();
	}
	
	public String getInfo(String s, boolean b){
		if(b) return s + ":§a true";
		return s + ":§c false";
	}
	public String getInfo(String s, String l){
		try{
			if( l == null || l.isEmpty() || l.equalsIgnoreCase("") || l.startsWith("#Mount:") || l.startsWith("#Light:") || l.startsWith("#Inventory:")) return s + ":§c NA";
			
			return s + ": " + ChatColor.translateAlternateColorCodes('&', l);
		}catch(Exception e){
			 return s + ":§c NA";
		}
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e){
		if(e.getClickedInventory()==null)return;
		if(setEnable(false)) return;
		if(!e.getClickedInventory().equals(inv)) return;
		e.setCancelled(true);
		if(fstand==null||fstand.isEmpty()) return;
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
						if(fstand.contains(model.getObjectID().getPacketList().get(i))){
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
						if(fstand.contains(model.getObjectID().getPacketList().get(i))){
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
			int i = check(e.getCurrentItem());
			int j = e.getRawSlot();
			if(!multiSelect){if(getIntegerList().contains(i)){return;}}
			ItemStack iS = e.getCurrentItem();
			ItemMeta meta = iS.getItemMeta();
			if(meta.hasEnchants()){
				if(fstand.size()<=1){return;}
				for(Enchantment enchant : meta.getEnchants().keySet()){meta.removeEnchant(enchant);}
				iS.setItemMeta(meta);
				deselect(i);
				inv.setItem(j, iS);
				p.updateInventory();
				break;
			}else{
				select(i);
				meta.addEnchant(Enchantment.KNOCKBACK, 1, false);
				iS.setItemMeta(meta);
				inv.setItem(j, iS);
				if(!multiSelect){p.closeInventory();}else{p.updateInventory();}
				break;
			}
		case BARRIER:
			model.remove(fstand);
			p.closeInventory();
			this.setEnable(false);
		case STONE_SLAB:
			if(!multiSelect){
				ItemStack stack = new ItemStack(Material.BRICK);
				ItemMeta im = stack.getItemMeta();
				im.setDisplayName("§7Select Mode: §6Multi Select");
				stack.setItemMeta(im);
				inv.setItem(inv.getSize()-1, stack);
				inv.setItem(inv.getSize()-2, stack6);
				p.updateInventory();
				multiSelect=true;
			}
			break;
		case BRICK:
			if(multiSelect){
				ItemStack stack = new ItemStack(Material.STONE_SLAB);
				ItemMeta im = stack.getItemMeta();
				im.setDisplayName("§7Select Mode: §2Single Select");
				stack.setItemMeta(im);
				stack.setDurability((short) 4);
				inv.setItem(inv.getSize()-1, stack);
				inv.setItem(inv.getSize()-2, new ItemStack(Material.AIR));
				p.updateInventory();
				multiSelect=false;
			}
			break;
		case EMERALD:
			if(multiSelect){
				if(e.getClick().equals(ClickType.RIGHT)){
					for(fEntity entity : model.getObjectID().getPacketList()){
						deselect(entity.getEntityID());
						if(fstand.contains(entity)) this.fstand.remove(entity);
						if(model.getStand().contains(entity)) model.getStand().remove(entity);
					}
					
					int l = 0;
					for(ItemStack stack : inv.getContents()){
						if(stack!=null){
							if(stack.getType()!=null){
								if(stack.getType().equals(Material.ARMOR_STAND)){
									ItemMeta itemmeta = stack.getItemMeta();
									itemmeta.removeEnchant(Enchantment.KNOCKBACK);
									stack.setItemMeta(itemmeta);
									inv.setItem(l, stack);
									l++;
								}
							}
						}
					}
					int s = check(this.inv.getItem(0));
					ItemStack item = this.inv.getItem(0);
					ItemMeta imeta = item.getItemMeta();
					select(s);
					imeta.addEnchant(Enchantment.KNOCKBACK, 1, false);
					item.setItemMeta(imeta);
					inv.setItem(0, item);
					if(!multiSelect){p.closeInventory();}else{p.updateInventory();}
				}else{
					for(fEntity entity : model.getObjectID().getPacketList()){
						select(entity.getEntityID());
						if(!fstand.contains(entity)) this.fstand.add(entity);
						if(!model.getStand().contains(entity)) model.getStand().add(entity);
					}
						
						
						int l = 0;
						for(ItemStack stack : inv.getContents()){
							if(stack!=null){
								if(stack.getType()!=null){
									if(stack.getType().equals(Material.ARMOR_STAND)){
										ItemMeta itemmeta = stack.getItemMeta();
										itemmeta.addEnchant(Enchantment.KNOCKBACK, 1, false);
										stack.setItemMeta(itemmeta);
										inv.setItem(l, stack);
										l++;
									}
								}
							}
						}
				}
			}
		default:
			break;
		}
	}
	
	private void deselect(int i) {
		fEntity stand = null;
		for(fEntity entity : model.getStand()){if(entity.getEntityID()==i){stand = entity;break;}}
		if(stand!=null){
			stand.setGlowing(false);
			stand.update(p);
			model.getStand().remove(stand);
		}
	}
	
	private void select(int i){
		fEntity stand = null;
		for(fEntity entity : model.getObjectID().getPacketList()){if(entity.getEntityID()==i){stand = entity;break;}}
		if(stand!=null){
			if(!multiSelect){model.selectSingle(stand);return;}else{
				stand.setGlowing(true);
				stand.update(p);
				if(!model.getStand().contains(stand)) model.getStand().add(stand);
			}
		}
	}

	private List<Integer> getIntegerList(){
		List<Integer> integerList = new ArrayList<Integer>();
		for(fEntity entity : fstand){integerList.add(entity.getEntityID());}
		return integerList;
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
		fEntity stand = null;
		for(fEntity s : this.model.getObjectID().getPacketList()){
			if(s!=null){
				if(s.getEntityID()==i){
					stand = s;
					break;
				}
			}
		}
		this.model.selectSingle(stand);
	}

	public boolean isEnable() {
		return enable;
	}

	public boolean setEnable(boolean enable) {
		this.enable = enable;
		return enable;
	}
}
