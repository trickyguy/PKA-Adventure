package com.pkadev.pkaadventure.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import com.pkadev.pkaadventure.objects.PKAPlayer;
import com.pkadev.pkaadventure.processors.PlayerProcessor;
import com.pkadev.pkaadventure.utils.ElementsUtil;
import com.pkadev.pkaadventure.utils.InventoryUtil;
import com.pkadev.pkaadventure.utils.ItemUtil;
import com.pkadev.pkaadventure.utils.MessageUtil;
import com.pkadev.pkaadventure.types.AbilityTriggerType;
import com.pkadev.pkaadventure.types.ClassType;
import com.pkadev.pkaadventure.types.MessageType;
import com.pkadev.pkaadventure.types.SlotType;

public class InventoryListener implements Listener {

	private static InventoryListener i; private InventoryListener(){} public static InventoryListener i() {if (i == null)i = new InventoryListener();return i;}

	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(player);
		if (pkaPlayer == null)
			return;
		ClassType classType = pkaPlayer.getClassType();
		String classTypeString = classType.toString();
		ItemStack itemStack = event.getItem().getItemStack();
		if (player.getName().equals(ItemUtil.getDroppedItemOwner(event.getItem()))) {
			ItemUtil.removeDroppedItem(event.getItem());
			if (ItemUtil.isWeapon(itemStack)) {
				ItemStack weapon = ItemUtil.getInitialItem(classTypeString.toLowerCase() + "_weapon", player.getLevel(), 1);
				ItemUtil.updateWeaponLore(weapon, classType, player.getLevel());
				InventoryUtil.moveItemIntoInventory(player, weapon);
				event.setCancelled(true);
			} else if (ItemUtil.isGold(itemStack)) {
				pkaPlayer.addGoldAmount(ItemUtil.getGoldWorth(itemStack));
				event.getItem().remove();
				event.setCancelled(true);
			}
		} else {
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void onDrag(InventoryDragEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(player);
		if (pkaPlayer == null && (event.getClickedInventory() == null || !event.getClickedInventory().getTitle().equals(ElementsUtil.getSelectionInventoryName())))
			return;
		if (event.isShiftClick() || event.getAction() == InventoryAction.NOTHING) {
			event.setResult(Result.DENY);
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
		
		if (event.getResult() == Result.DENY)
			return;
		
		if (pickup && drop) {
			if (!InventoryUtil.pickupItemFromSlot(player, pkaPlayer, currentItem, slotType, slot, event.getClickedInventory().getName(), true))
				event.setResult(Result.DENY);
			else {
				if (!InventoryUtil.dropItemInSlot(player, pkaPlayer, cursorItem, slotType, slot, event.getClickedInventory().getName()))
					event.setResult(Result.DENY);
			}
		} else if (pickup) {
			if (!InventoryUtil.pickupItemFromSlot(player, pkaPlayer, currentItem, slotType, slot, event.getClickedInventory().getName(), false))
				event.setResult(Result.DENY);
		} else if (drop) {
			if (!InventoryUtil.dropItemInSlot(player, pkaPlayer, cursorItem, slotType, slot, event.getClickedInventory().getName()))
				event.setResult(Result.DENY);
		}
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(player);
		if (pkaPlayer == null)
			return;
		if (player.getInventory().getHeldItemSlot() == pkaPlayer.getWeaponSlot() && !pkaPlayer.isSneaking() && pkaPlayer.getAbiltiySelectionType() == 3) {
			if (pkaPlayer.getClassType() == ClassType.WINGS || pkaPlayer.getClassType() == ClassType.KYLE) {
				if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK)
					return;
			} else if (pkaPlayer.getClassType() == ClassType.WOODY || pkaPlayer.getClassType() == ClassType.LEFTY) {
				if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
					return;
			}
			pkaPlayer.triggerAbility(pkaPlayer.getCurrentlySelectedAbility(), null, AbilityTriggerType.CLICK);
			return;
		}
		if (!pkaPlayer.isSneaking())
			return;
		if (pkaPlayer.getAbiltiySelectionType() != 1)
			return;
		if (!(event.getAction() == Action.RIGHT_CLICK_AIR) && !(event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		
		pkaPlayer.triggerAbility(Integer.valueOf(player.getInventory().getHeldItemSlot()), null, AbilityTriggerType.CLICK);
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
		if (pkaPlayer.getAbiltiySelectionType() != 2)
			return;
		
		pkaPlayer.triggerAbility(Integer.valueOf(event.getNewSlot()), null, AbilityTriggerType.CLICK);
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onSneakChange(PlayerToggleSneakEvent event) {
		Player player = event.getPlayer();
		PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(player);
		if (pkaPlayer == null)
			return;
		if (pkaPlayer.getAbiltiySelectionType() == 1 || pkaPlayer.getAbiltiySelectionType() == 3) {
			int abilitySlot = InventoryUtil.toggleHotbar(player, pkaPlayer);
			if (!event.isSneaking() && pkaPlayer.getAbiltiySelectionType() == 3) {
				String abilityName = pkaPlayer.setCurrentlySelectedAbility(Integer.valueOf(abilitySlot));
				MessageUtil.sendMessage(player, abilityName + " ability selected!", MessageType.SINGLE);
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
			MessageUtil.sendMessage(player, "You've just dropped your weapon!", MessageType.SINGLE);
		ItemUtil.addDroppedItem(event.getItemDrop(), player.getName());
	}
	
	private SlotType getClicksSlotType(InventoryClickEvent event) {
		SlotType slotType = SlotType.NORMAL;
		
		if (event.getSlotType() == org.bukkit.event.inventory.InventoryType.SlotType.QUICKBAR)
			return SlotType.HOTBAR;
		else if (event.getSlotType() == org.bukkit.event.inventory.InventoryType.SlotType.OUTSIDE)
			return SlotType.OUTSIDE;
		else if (event.getSlotType() == org.bukkit.event.inventory.InventoryType.SlotType.CRAFTING ||
				event.getSlotType() == org.bukkit.event.inventory.InventoryType.SlotType.RESULT) {
			return null;
		}
		
		if (event.getView().getType() == InventoryType.CHEST) {
			if (event.getClickedInventory().getType() == InventoryType.CHEST)
				slotType = SlotType.UPPER;
		} else if (event.getView().getType() == InventoryType.CRAFTING) {
			if (event.getSlotType() == org.bukkit.event.inventory.InventoryType.SlotType.ARMOR)
				slotType = SlotType.ARMOR;
		}
		
		return slotType;
	}
	
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		if (event.getInventory().getType() == InventoryType.MERCHANT) {
			event.setCancelled(true);
			event.getPlayer().closeInventory();
		}
	}

}

