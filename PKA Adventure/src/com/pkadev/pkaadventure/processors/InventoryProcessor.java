package com.pkadev.pkaadventure.processors;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.pkadev.pkaadventure.types.ClassType;
import com.pkadev.pkaadventure.types.InventoryType;
import com.pkadev.pkaadventure.types.MessageType;
import com.pkadev.pkaadventure.utils.InventoryUtil;
import com.pkadev.pkaadventure.utils.ItemUtil;
import com.pkadev.pkaadventure.utils.MessageUtil;

public class InventoryProcessor {

	/**
	 * @param player
	 * @param inventoryName
	 * @param clickedItem
	 * @param isUpperInventory
	 * @return true if the click has to be cancelled (stop them from picking up item), false if not
	 */
	public static boolean clickInNamedUpperInventory(Player player, String inventoryName, ItemStack clickedItem, boolean isUpperInventory) {
		if (isUpperInventory) {
			InventoryType inventoryType = InventoryUtil.getInventoryTypeFromName(inventoryName);
			if (inventoryType == InventoryType.SELECT) {
				if (!ItemUtil.isAttributeItem(clickedItem))
					return false;
				PlayerProcessor.switchClass(player, ClassType.valueOf(ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName()).toUpperCase()));
				return true;
			}
		} else {
			
		}
		return false;
	}
	
	public static void dropItem(Player player, InventoryType inventoryType, ItemStack heldItem, ItemStack clickedItem, boolean isUpperInventory) {
		
	}
	
}
