package com.pkadev.pkaadventure.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.pkadev.pkaadventure.objects.PKAPlayer;
import com.pkadev.pkaadventure.types.MessageType;

public class ShopUtil {

	/**
	 * @param player
	 * @param itemStack
	 * @return false if cannot be sold
	 */
	public static boolean sell(Player player, PKAPlayer pkaPlayer, ItemStack itemStack) {
		if (!ItemUtil.isAttributeItem(itemStack))
			return false;
		int worth = ItemUtil.getIntValueFromLore(itemStack.getItemMeta().getLore(), "Worth");
		if (worth == -1) {
			MessageUtil.sendMessage(player, "Not sellable.", MessageType.SINGLE);
			return false;
		}
		worth = worth * itemStack.getAmount();
		pkaPlayer.addGoldAmount(worth);
		player.setItemOnCursor(new ItemStack(Material.AIR));
		MessageUtil.sendMessage(player, "You've sold something for " + worth + ".", MessageType.SINGLE);
		return false;
	}
	
	/**
	 * @param player
	 * @param itemStack
	 * @return false if cannot be bought
	 */
	public static boolean buy(Player player, PKAPlayer pkaPlayer, ItemStack itemStack) {
		if (!ItemUtil.isAttributeItem(itemStack))
			return false;
		int price = ItemUtil.getIntValueFromLore(itemStack.getItemMeta().getLore(), "Price");
		if (price == -1) {
			MessageUtil.sendMessage(player, "Not up for sale.", MessageType.SINGLE);
			return false;
		}
		price = price * itemStack.getAmount();
		if (!pkaPlayer.removeGoldAmount(price)) {
			MessageUtil.sendMessage(player, "Not enough money.", MessageType.SINGLE);
			return false;
		}
		MessageUtil.sendMessage(player, "You've bought something for " + price + ".", MessageType.SINGLE);
		return true;
	}
	
}
