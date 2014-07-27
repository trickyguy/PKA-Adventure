package com.pkadev.pkaadventure.inventories;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.pkadev.pkaadventure.inventories.shops.Food;
import com.pkadev.pkaadventure.objects.PKAPlayer;
import com.pkadev.pkaadventure.processors.PlayerProcessor;

public class InventoryMain {
	public static void loadShops(){
		Food.load();
	}

	/**
	 * This method is used to set which slot the
	 * items from the items ArrayList goes in.
	 * @param i Starting slot
	 * @param inventory Inventory
	 * @param items ArrayList
	 */
	public static void setItems(int i, Inventory inventory, ArrayList<ItemStack> items) {
		for(ItemStack item : items) {
			if(i <= items.size() + 1) {
				inventory.setItem(i, item);
				i++;
				if (i % 7 == 0)
					i += 2;
			}
		}
	}

	public static ItemStack createToggle(boolean buying) {
		if(buying) {
			ItemStack toggle = new ItemStack(Material.PAPER, 1);
			ItemMeta toggleMeta = toggle.getItemMeta();
			toggleMeta.setDisplayName("§fBuying");
			ArrayList<String> toggleLore = new ArrayList<String>();
			toggleLore.add("§7Click to toggle");
			toggleMeta.setLore(toggleLore);
			toggle.setItemMeta(toggleMeta);
			return toggle;
		} else {
			ItemStack toggle = new ItemStack(Material.EMPTY_MAP, 1);
			ItemMeta toggleMeta = toggle.getItemMeta();
			toggleMeta.setDisplayName("§eSelling");
			ArrayList<String> toggleLore = new ArrayList<String>();
			toggleLore.add("§7Click to toggle");
			toggleMeta.setLore(toggleLore);
			toggle.setItemMeta(toggleMeta);
			return toggle;
		}
	}
	
	public static ItemStack setPiggyBank(String playerName) {
		PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(playerName);
		ItemStack gold = new ItemStack(Material.CHEST, 1);
		ItemMeta goldMeta = gold.getItemMeta();
		goldMeta.setDisplayName("§6Piggy Bank");
		ArrayList<String> goldLore = new ArrayList<String>();
		goldLore.add("§7Gold: §f" + pkaPlayer.getGoldAmount());
		goldMeta.setLore(goldLore);
		gold.setItemMeta(goldMeta);
		return gold;
	}
	
	public static void updatePiggyBank(Inventory inventory, String playerName) {
		PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(playerName);
		ItemStack gold = inventory.getItem(inventory.getSize() - 1);
		ItemMeta goldMeta = gold.getItemMeta();
		List<String> goldLore = gold.getItemMeta().getLore();
		goldLore.set(0, "§7Gold: §f" + pkaPlayer.getGoldAmount());
		goldMeta.setLore(goldLore);
		gold.setItemMeta(goldMeta);
		inventory.setItem(inventory.getSize() - 1, gold);
	}
}
