package com.pkadev.pkaadventure.processors;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.pkadev.pkaadventure.types.ClassType;
import com.pkadev.pkaadventure.types.InventoryType;
import com.pkadev.pkaadventure.utils.ItemUtil;

public class InventoryProcessor {

	public static void clickItem(Player player, InventoryType inventoryType, ItemStack clickedItem, boolean isUpperInventory) {
		if (isUpperInventory) {
			if (inventoryType == InventoryType.SELECT) {
				if (!ItemUtil.isAttributeItem(clickedItem))
					return;
				PlayerProcessor.switchClass(player, ClassType.valueOf(clickedItem.getItemMeta().getDisplayName()));
			}
		} else {
			
		}
	}
	
	public static void dropItem(Player player, InventoryType inventoryType, ItemStack heldItem, ItemStack clickedItem, boolean isUpperInventory) {
		
	}
	
}
