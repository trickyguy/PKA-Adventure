package com.pkadev.pkaadventure.inventories.shops;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.pkadev.pkaadventure.inventories.InventoryMain;
import com.pkadev.pkaadventure.types.InventoryType;
import com.pkadev.pkaadventure.utils.FileUtil;
import com.pkadev.pkaadventure.utils.InventoryUtil;

public class Food {

	public static void load() {
		loadBuying();
		loadSelling();
	}
	
	private static void loadBuying() {
		int slots = 9 * FileUtil.getIntValueFromConfig(FileUtil.inventoryConfig, "Inventories.Food.Size");
		Inventory foodShop = Bukkit.createInventory(null, slots + 9, FileUtil.getStringValueFromConfig(FileUtil.inventoryConfig, "Inventories.Food.Name") + " - Buying");
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		int totalItems = FileUtil.getIntValueFromConfig(FileUtil.inventoryConfig, "Inventories.Food.Contents.ItemTotal");
		for(int i = 1; i <= totalItems; i++) {
			ItemStack item = new ItemStack(Material.getMaterial(FileUtil.getStringValueFromConfig(FileUtil.inventoryConfig, "Inventories.Food.Contents." + i + ".ID")));
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.setDisplayName("§f" + FileUtil.getStringValueFromConfig(FileUtil.inventoryConfig, "Inventories.Food.Contents." + i + ".Name"));
			ArrayList<String> itemLore = new ArrayList<String>();
			List<String> loreList = Arrays.asList(FileUtil.getStringArrayFromConfig(FileUtil.inventoryConfig, "Inventories.Food.Contents." + i + ".Lore"));
			for (String string : loreList) {  
				itemLore.add("§7" + string);
			}
			
			itemLore.add("§6Cost: " + FileUtil.getIntValueFromConfig(FileUtil.inventoryConfig, "Inventories.Food.Contents." + i + ".Cost"));
			itemMeta.setLore(itemLore);
			item.setAmount(FileUtil.getIntValueFromConfig(FileUtil.inventoryConfig, "Inventories.Food.Contents." + i + ".Amount"));
			item.setItemMeta(itemMeta);
			items.add(item);
		}

		InventoryMain.setItems(0, foodShop, items);
		foodShop.setItem(8, InventoryMain.createToggle(true));
		items.clear();
		InventoryUtil.setInventory(InventoryType.FOOD_STORE_BUYING, foodShop);
	}
	
	private static void loadSelling() {
		int slots = 9 * FileUtil.getIntValueFromConfig(FileUtil.inventoryConfig, "Inventories.Food.Size");
		Inventory foodShop = Bukkit.createInventory(null, slots + 9, FileUtil.getStringValueFromConfig(FileUtil.inventoryConfig, "Inventories.Food.Name") + " - Selling");
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		int totalItems = FileUtil.getIntValueFromConfig(FileUtil.inventoryConfig, "Inventories.Food.Contents.ItemTotal");
		for(int i = 1; i <= totalItems; i++) {
			ItemStack item = new ItemStack(Material.getMaterial(FileUtil.getStringValueFromConfig(FileUtil.inventoryConfig, "Inventories.Food.Contents." + i + ".ID")));
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.setDisplayName("§f" + FileUtil.getStringValueFromConfig(FileUtil.inventoryConfig, "Inventories.Food.Contents." + i + ".Name"));
			ArrayList<String> itemLore = new ArrayList<String>();
			List<String> loreList = Arrays.asList(FileUtil.getStringArrayFromConfig(FileUtil.inventoryConfig, "Inventories.Food.Contents." + i + ".Lore"));
			for (String string : loreList) {  
				itemLore.add("§7" + string);
			}

			itemLore.add("§6Worth: " + FileUtil.getIntValueFromConfig(FileUtil.inventoryConfig, "Inventories.Food.Contents." + i + ".Worth"));
			itemMeta.setLore(itemLore);
			item.setAmount(FileUtil.getIntValueFromConfig(FileUtil.inventoryConfig, "Inventories.Food.Contents." + i + ".Amount"));
			item.setItemMeta(itemMeta);
			items.add(item);
		}
		
		InventoryMain.setItems(0, foodShop, items);
		foodShop.setItem(8, InventoryMain.createToggle(false));
		items.clear();
		InventoryUtil.setInventory(InventoryType.FOOD_STORE_SELLING, foodShop);
	}
}
