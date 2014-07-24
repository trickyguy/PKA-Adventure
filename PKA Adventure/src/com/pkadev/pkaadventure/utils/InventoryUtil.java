package com.pkadev.pkaadventure.utils;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import com.pkadev.pkaadventure.inventories.InventoryMain;
import com.pkadev.pkaadventure.types.InventoryType;

public class InventoryUtil {
	private static Random random = new Random();
	
	private static HashMap<InventoryType, Inventory> inventories = new HashMap<InventoryType, Inventory>();
	
	private static Inventory getInventory(InventoryType inventoryType) {
		return inventories.get(inventoryType);
	}
	
	public static void setInventory(InventoryType inventoryType, Inventory inventory) {
		inventories.put(inventoryType, inventory);
	}
	
	public static void load() {
		//TODO improve upon this code
		Inventory skillInventory = Bukkit.createInventory(null, 9, "Select a class!");
		
		ItemStack woodyItem = 	new ItemStack(Material.WOOD_SWORD);
		ItemStack kyleItem = 	new ItemStack(Material.BOW);
		ItemStack leftyItem = 	new ItemStack(Material.GOLD_AXE);
		ItemStack wingsItem = 	new ItemStack(Material.BOW);
		
		ItemMeta itemMeta = woodyItem.getItemMeta();
		String woodyName = FileUtil.getStringValueFromConfig(FileUtil.config, "Lore.woody_selection_name");
		itemMeta.setDisplayName(woodyName.replace('&', '§'));
		woodyItem.setItemMeta(itemMeta);
		
		itemMeta = kyleItem.getItemMeta();
		String kyleName = FileUtil.getStringValueFromConfig(FileUtil.config, "Lore.kyle_selection_name");
		itemMeta.setDisplayName(kyleName.replace('&', '§'));
		kyleItem.setItemMeta(itemMeta);
		
		itemMeta = leftyItem.getItemMeta();
		String leftyName = FileUtil.getStringValueFromConfig(FileUtil.config, "Lore.lefty_selection_name");
		itemMeta.setDisplayName(leftyName.replace('&', '§'));
		leftyItem.setItemMeta(itemMeta);
		
		itemMeta = wingsItem.getItemMeta();
		String wingsName = FileUtil.getStringValueFromConfig(FileUtil.config, "Lore.wings_selection_name");
		itemMeta.setDisplayName(wingsName.replace('&', '§'));
		wingsItem.setItemMeta(itemMeta);
		
		skillInventory.setItem(2, woodyItem);
		skillInventory.setItem(3, kyleItem);
		skillInventory.setItem(4, leftyItem);
		skillInventory.setItem(6, wingsItem);
		
		setInventory(InventoryType.SKILL, skillInventory);
	}
	
	public static void compressAndSaveInventory(Player player) {
		
	}
	
	/**
	 * @param creature
	 * @return the slot in which a rare item has been put into: -1 if none
	 */
	public static int setMobInventory(Creature creature, int mobLevel) {
		int returnInt = -1;
		ItemStack[] armorContent = new ItemStack[4];
		for (int i = 0; i < 4; i++) {
			if (random.nextBoolean()) {
				//give armorpiece
				boolean willBeEnchanted = random.nextDouble() < MathUtil.getInt("mob_rare_drop_chance");
				ItemStack armorPiece = getMobArmorPiece(mobLevel, i);
				armorContent[i] = armorPiece;
				if (willBeEnchanted) {
					//TODO
					returnInt = i;
				}
			} else {
				//don't
				armorContent[i] = new ItemStack(Material.AIR);
			}
		}
		return returnInt;
	}
	
	/**
	 * @param mobLevel
	 * @return the kind of armor piece he would be getting at this level
	 */
	private static ItemStack getMobArmorPiece(int mobLevel, int slotInt) {
		String piece = "_HELMET";
		String material = "LEATHER";
		switch(slotInt) {
		default:break;
		case 1:piece = "_CHESTPLATE";break;
		case 2:piece = "_LEGGINGS";break;
		case 3:piece = "_BOOTS";break;
		}
		if (mobLevel < 10) {
			
		} else if (mobLevel < 20) {
			material = "CHAINMAIL";
		} else if (mobLevel < 30) {
			material = "IRON";
		} else if (mobLevel < 40) {
			material = "GOLD";
		} else if (mobLevel > 39) {
			material = "DIAMOND";
		}
		return new ItemStack(Material.valueOf(material + piece));
	}
	
	/**
	 * checks only the first 8 slots
	 * @param player
	 * @return
	 */
	public static int getWeaponSlot(Player player) {
		int weaponSlot = 9;
		PlayerInventory playerInventory = player.getInventory();
		for (int i = 0; i < 9; i++) {
			if (ItemUtil.isWeapon(playerInventory.getItem(i)))
				return i;
		}
		return weaponSlot;
	}
	
	public static ItemStack getSkillItem(Player player) {
		return player.getInventory().getItem(17);
	}
	
	public static void setItem(Player player, int slot, ItemStack itemStack) {
		player.getInventory().setItem(slot, itemStack);
	}
	
	public static void addItem(Player player, ItemStack itemStack) {
		player.getInventory().addItem(itemStack);
	}
	
	public static void openStaticInventory(Player player, InventoryType inventoryType) {
		player.openInventory(getInventory(inventoryType));
	}
	
	public static void openDynamicInventory(Player player, InventoryType inventoryType) {
		//TODO (something stored inside inventoryType, ill think of something configurable)
	}
	
	public static void openShopInventory(Player player, InventoryType inventoryType) {
		Inventory inventory = inventories.get(inventoryType);
		inventory.setItem(inventory.getSize() - 1, InventoryMain.setPiggyBank(player.getName()));
		player.openInventory(inventory);
	}
}
