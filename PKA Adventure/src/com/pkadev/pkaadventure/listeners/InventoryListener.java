package com.pkadev.pkaadventure.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import com.pkadev.pkaadventure.Main;
import com.pkadev.pkaadventure.objects.PKAPlayer;
import com.pkadev.pkaadventure.processors.PlayerProcessor;
import com.pkadev.pkaadventure.utils.ElementsUtil;
import com.pkadev.pkaadventure.utils.InventoryUtil;
import com.pkadev.pkaadventure.utils.ItemUtil;
import com.pkadev.pkaadventure.utils.MessageUtil;
import com.pkadev.pkaadventure.types.MessageType;
import com.pkadev.pkaadventure.types.SlotType;

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
		if (pkaPlayer == null && (event.getClickedInventory() == null || !event.getClickedInventory().getTitle().equals(ElementsUtil.getSelectionInventoryName())))
			return;
		if (event.isShiftClick()) {
			event.setCancelled(true);
			return;
		}
		
		//ill use my own slotType
		SlotType slotType = getClicksSlotType(event);
		if (slotType == null)
			return;
		int slot = event.getSlot();
		ItemStack currentItem = event.getCurrentItem();
		ItemStack cursorItem = event.getCursor();
		boolean drop = false;
		boolean pickup = false;
		
		if (event.getAction() == InventoryAction.SWAP_WITH_CURSOR) {
			drop = true;
			pickup = true;
		} else if (event.getAction() == InventoryAction.PICKUP_ALL || ((event.getAction() == InventoryAction.PICKUP_HALF || event.getAction() == InventoryAction.PICKUP_ONE) && event.getCurrentItem().getAmount() == 1)) {
			pickup = true;
		} else if (event.getAction() == InventoryAction.PLACE_ALL || event.getAction() == InventoryAction.PLACE_SOME || event.getAction() == InventoryAction.PLACE_ONE) {
			drop = true;
		}
		
		if (pickup && drop) {
			if (!InventoryUtil.pickupItemFromSlot(player, pkaPlayer, currentItem, slotType, slot, event.getView().getTitle(), true))
				event.setCancelled(true);
			else {
				if (!InventoryUtil.dropItemInSlot(player, pkaPlayer, cursorItem, slotType, slot, event.getView().getTitle()))
					event.setCancelled(true);
			}
		} else if (pickup) {
			if (!InventoryUtil.pickupItemFromSlot(player, pkaPlayer, currentItem, slotType, slot, event.getView().getTitle(), true))
				event.setCancelled(true);
		} else if (drop) {
			if (!InventoryUtil.dropItemInSlot(player, pkaPlayer, cursorItem, slotType, slot, event.getView().getTitle()))
				event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(player);
		if (pkaPlayer == null)
			return;
		if (player.getInventory().getHeldItemSlot() == pkaPlayer.getWeaponSlot() && !pkaPlayer.isSneaking() && pkaPlayer.getAbiltiyTriggerType() == 3) {
			pkaPlayer.triggerAbility(pkaPlayer.getCurrentlySelectedAbility());
			return;
		}
		if (!pkaPlayer.isSneaking())
			return;
		if (pkaPlayer.getAbiltiyTriggerType() != 1)
			return;
		if (!(event.getAction() == Action.RIGHT_CLICK_AIR) && !(event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		
		pkaPlayer.triggerAbility(Integer.valueOf(player.getInventory().getHeldItemSlot()));
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onChangeSlot(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(player);
		if (pkaPlayer == null)
			return;
		if (!pkaPlayer.isSneaking())
			return;
		if (pkaPlayer.getAbiltiyTriggerType() != 2)
			return;
		
		pkaPlayer.triggerAbility(Integer.valueOf(event.getNewSlot()));
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onSneakChange(PlayerToggleSneakEvent event) {
		Player player = event.getPlayer();
		PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(player);
		if (pkaPlayer == null)
			return;
		if (pkaPlayer.getAbiltiyTriggerType() == 1 || pkaPlayer.getAbiltiyTriggerType() == 3) {
			int abilitySlot = InventoryUtil.toggleHotbar(player, pkaPlayer);
			if (!event.isSneaking()) {
				String abilityName = pkaPlayer.setCurrentlySelectedAbility(Integer.valueOf(abilitySlot));
				if (abilityName == null)
					MessageUtil.sendMessage(player, "No ability selected!", MessageType.SINGLE);
				else {
					MessageUtil.sendMessage(player, abilityName + " ability selected!", MessageType.SINGLE);
				}
			}
		}
		pkaPlayer.setIsSneaking(event.isSneaking());
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(player);
		if (pkaPlayer == null)
			return;
		if (ItemUtil.isWeapon(event.getItemDrop().getItemStack()))
			event.setCancelled(true);
		else {
			ItemUtil.addDroppedItem(event.getItemDrop(), player.getName());
		}
	}
	
	private SlotType getClicksSlotType(InventoryClickEvent event) {
		SlotType slotType = SlotType.NORMAL;
		
		if (event.getView().getType() == InventoryType.PLAYER) {
			if (event.getSlotType() == org.bukkit.event.inventory.InventoryType.SlotType.ARMOR) {
				slotType = SlotType.ARMOR;
			} else if (event.getSlotType() == 
					org.bukkit.event.inventory.InventoryType.SlotType.QUICKBAR) {
				slotType = SlotType.HOTBAR;
			} else if (event.getSlotType() ==
					org.bukkit.event.inventory.InventoryType.SlotType.OUTSIDE) {
				slotType = SlotType.OUTSIDE;
			} else if (event.getSlotType() ==
					org.bukkit.event.inventory.InventoryType.SlotType.CONTAINER) {
				
			} else {
				event.setCancelled(true);
				return null;
			}
		} else if (event.getView().getType() == InventoryType.CHEST) {
			if (event.getView().getTopInventory().getTitle().equals(ElementsUtil.getAbilityInventoryName()))
				slotType = SlotType.ABILITY;
			else {
				slotType = SlotType.UPPER;
			}
		} else {
			event.setCancelled(true);
			return null;
		}
		
		return slotType;
	}

}






