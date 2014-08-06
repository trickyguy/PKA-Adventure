package com.pkadev.pkaadventure.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.pkadev.pkaadventure.Main;
import com.pkadev.pkaadventure.inventories.InventoryMain;
import com.pkadev.pkaadventure.objects.PKAPlayer;
import com.pkadev.pkaadventure.processors.PlayerProcessor;
import com.pkadev.pkaadventure.types.InventoryType;
import com.pkadev.pkaadventure.types.SoundType;
import com.pkadev.pkaadventure.utils.InventoryUtil;
import com.pkadev.pkaadventure.utils.ItemUtil;
import com.pkadev.pkaadventure.utils.ShopUtil;

public class InventoryListener implements Listener {
	private Main plugin = Main.instance;

	private static InventoryListener i; private InventoryListener(){} public static InventoryListener i() {if (i == null)i = new InventoryListener();return i;}

	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent event) {
		if (event.getPlayer().getName().equals(ItemUtil.getDroppedItemOwner(event.getItem()))) {
			ItemUtil.removeDroppedItem(event.getItem());
		} else {
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(player);
		if (pkaPlayer == null)
			return;
		if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
			event.setCancelled(true);
			return;
		}
		else if (event.getClick() == ClickType.DOUBLE_CLICK 
				|| event.getClick() == ClickType.LEFT 
				|| event.getClick() == ClickType.RIGHT) {
			if (event.getSlotType() == SlotType.ARMOR) {
				ItemStack clickedItem = event.getCurrentItem();
				ItemStack cursorItem = 	event.getCursor();
				if (ItemUtil.isArmorItem(cursorItem)) {
					int[] cursorItemAttributes = ItemUtil.getAttributesFromItemStack(cursorItem);
					if (event.getAction() == InventoryAction.DROP_ALL_CURSOR || event.getAction() == InventoryAction.DROP_ONE_CURSOR) {
						pkaPlayer.addAttributes(cursorItemAttributes);
					} else if (event.getAction() == InventoryAction.SWAP_WITH_CURSOR) {
						int[] clickedItemAttributes = ItemUtil.getAttributesFromItemStack(clickedItem);
						pkaPlayer.removeAttributes(clickedItemAttributes);
						pkaPlayer.addAttributes(cursorItemAttributes);
					}
					ItemUtil.updateStatItemMeta(player, pkaPlayer);
				}
			}
		}
		if (ItemUtil.isWeapon(event.getCurrentItem())) {
			pkaPlayer.setWeaponSlot(9);
		} else if (ItemUtil.isWeapon(event.getCursor())) {
			if (event.getSlotType() == SlotType.QUICKBAR) {
				pkaPlayer.setWeaponSlot(event.getSlot());
			} else {
				pkaPlayer.setWeaponSlot(9);
			}
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		if (ItemUtil.isWeapon(event.getItemDrop().getItemStack()))
			event.setCancelled(true);
		else {
			ItemUtil.addDroppedItem(event.getItemDrop(), event.getPlayer().getName());
		}
	}

	//TODO make more efficient
	@EventHandler
	public void onShopClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if(event.getCurrentItem() == null) return;
		Inventory inventory = event.getInventory();

		if(ShopUtil.isShop(inventory.getName())) {
			if(event.getView().getTopInventory().contains(event.getCurrentItem())) {
				InventoryType type = ShopUtil.getInventoryTypeFromString(inventory.getName());
				event.setCancelled(true);
				if(type.equals(InventoryType.FOOD_STORE_BUYING)) {
					if(event.getSlot() == 8) {
						player.closeInventory();
						ShopUtil.playSound(player, SoundType.SWITCH);
						InventoryUtil.openShopInventory(player, InventoryType.FOOD_STORE_SELLING);
						return;
					} if(event.getSlot() == event.getInventory().getSize() - 1) {
						player.sendMessage("§cYou can't move your Piggy Bank.");
						ShopUtil.playSound(player, SoundType.ERROR);
						return;
					}
					ShopUtil.purcase(event.getCurrentItem(), player);
					InventoryMain.updatePiggyBank(event.getInventory(), player.getName());
					return;
				} if(type.equals(InventoryType.FOOD_STORE_SELLING)) {
					if(event.getSlot() == 8) {
						player.closeInventory();
						ShopUtil.playSound(player, SoundType.SWITCH);
						InventoryUtil.openShopInventory(player, InventoryType.FOOD_STORE_BUYING);
						return;
					} if(event.getSlot() == event.getInventory().getSize() - 1) {
						player.sendMessage("§cYou can't move your Piggy Bank.");
						ShopUtil.playSound(player, SoundType.ERROR);
						return;
					}
					ShopUtil.sell(event.getCurrentItem(), player);
					InventoryMain.updatePiggyBank(event.getInventory(), player.getName());
					return;
				}
			} else if(event.getView().getBottomInventory().contains(event.getCurrentItem())) {
				event.setCancelled(true);
			}
		}
	}

}






