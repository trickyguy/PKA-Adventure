package com.pkadev.pkaadventure.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.pkadev.pkaadventure.objects.PKAPlayer;
import com.pkadev.pkaadventure.processors.PlayerProcessor;
import com.pkadev.pkaadventure.types.InventoryType;
import com.pkadev.pkaadventure.types.SoundType;

public class ShopUtil {

	public static boolean isShop(String string) {
		if(string.startsWith("Food Store"))
			return true;
		return false;
	}

	public static InventoryType getInventoryTypeFromString(String string) {
		switch (string) {
		default:
			return null;
		case "Food Store - Buying":
			return InventoryType.SHOP_DYNAMIC;
		case "Food Store - Selling":
			return InventoryType.SHOP_DYNAMIC;
		}
	}

	/* public static void createDuplicate(Player player, final Inventory inventory, final ItemStack... items) {
		final Inventory fakeInventory = Bukkit.createInventory(null, inventory.getSize(), inventory.getName());
		fakeInventory.setContents(inventory.getContents());
		if (addItems(fakeInventory, items).isEmpty()) {
			addItems(inventory, items);
			player.openInventory(fakeInventory);
		}
	} */

	public static void purcase(ItemStack item, Player player) {
		if(item.getItemMeta() == null) return;
		if(item.getItemMeta().getDisplayName() == null) return;

		PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(player);
		if (pkaPlayer == null)
			return;
		int cost = ItemUtil.getIntValueFromLore(item.getItemMeta().getLore(), "Cost");
		int goldAmount = pkaPlayer.getGoldAmount();
		if(cost <= goldAmount) {
			ItemStack newItem = createItem(item);
			if(addAllItems(player.getInventory(), newItem)) {
				player.sendMessage("§c§l-§c" + cost + "§c§lG");
				pkaPlayer.setGoldAmount(pkaPlayer.getGoldAmount() - cost);
				playSound(player, SoundType.SUCCESS);
			} else {
				player.sendMessage("§cYou don't have enough space in your inventory.");
				playSound(player, SoundType.ERROR);
			}
		} else {
			player.sendMessage("§cYou don't have enough Gold.");
			playSound(player, SoundType.ERROR);
		}
	}

	public static void sell(ItemStack item, Player player) {
		if(item.getItemMeta() == null) return;
		if(item.getItemMeta().getDisplayName() == null) return;

		PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(player);
		if (pkaPlayer == null)
			return;
		int worth = ItemUtil.getIntValueFromLore(item.getItemMeta().getLore(), "Worth");

		ItemStack newItem = createItem(item);
		if(ItemUtil.getTotalItems(player.getInventory(), item.getType()) >= item.getAmount()) {
			player.sendMessage("§6§l+§6" + worth + "§6§lG");
			playSound(player, SoundType.SUCCESS);

			pkaPlayer.setGoldAmount(pkaPlayer.getGoldAmount() + worth);
			ItemUtil.removeInventoryItems(player.getInventory(), item.getType(), item.getAmount());
		} else {
			player.sendMessage("§cYou don't have enough " + ChatColor.stripColor(newItem.getItemMeta().getDisplayName()));
			playSound(player, SoundType.ERROR);
		}
	}

	private static ItemStack createItem(ItemStack oldItem) {
		ItemStack item = new ItemStack(oldItem.getType(), oldItem.getAmount());
		item.setAmount(oldItem.getAmount());
		ItemMeta itemMeta = oldItem.getItemMeta();
		List<String> itemLore = itemMeta.getLore();
		itemLore.remove(itemLore.size() - 1);
		itemMeta.setLore(itemLore);
		item.setItemMeta(itemMeta);
		return item;
	}

	public static void playSound(Player player, SoundType type) {
		switch (type) {
		default:
			return;
		case SUCCESS:
			player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.4F, 1.0F);
			player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1.0F, 1.0F);
			break;
		case ERROR:
			player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.4F);
			break;
		case SWITCH:
			player.playSound(player.getLocation(), Sound.BAT_TAKEOFF, 1.0F, 1.4F);
			break;
		}
	}

	private static int firstPartial(final Inventory inventory, final ItemStack item, final int maxAmount) {
		if (item == null) {
			return -1;
		}
		final ItemStack[] stacks = inventory.getContents();
		for (int i = 0; i < stacks.length; i++) {
			final ItemStack cItem = stacks[i];
			if (cItem != null && cItem.getAmount() < maxAmount && cItem.isSimilar(item)) {
				return i;
			}
		} return -1;
	}

	public static boolean addAllItems(final Inventory inventory, final ItemStack... items) {
		final Inventory fakeInventory = Bukkit.getServer().createInventory(null, inventory.getType());
		fakeInventory.setContents(inventory.getContents());
		if (addItems(fakeInventory, items).isEmpty()) {
			addItems(inventory, items);
			return true;
		}
		return false;
	}

	public static Map<Integer, ItemStack> addItems(final Inventory inventory, final ItemStack... items) {
		return addOversizedItems(inventory, 0, items);
	}

	public static Map<Integer, ItemStack> addOversizedItems(final Inventory inventory, final int oversizedStacks, final ItemStack... items) {
		final Map<Integer, ItemStack> leftover = new HashMap<Integer, ItemStack>();

		/*
		 * TODO: some optimization - Create a 'firstPartial' with a 'fromIndex' - Record the lastPartial per Material -
		 * Cache firstEmpty result
		 */

		// combine items

		ItemStack[] combined = new ItemStack[items.length];
		for (int i = 0; i < items.length; i++) {
			if (items[i] == null || items[i].getAmount() < 1) {
				continue;
			} for (int j = 0; j < combined.length; j++) {
				if (combined[j] == null) {
					combined[j] = items[i].clone();
					break;
				} if (combined[j].isSimilar(items[i])) {
					combined[j].setAmount(combined[j].getAmount() + items[i].getAmount());
					break;
				}
			}
		}


		for (int i = 0; i < combined.length; i++) {
			final ItemStack item = combined[i];
			if (item == null) {
				continue;
			}

			while (true) {
				// Do we already have a stack of it?
				final int maxAmount = oversizedStacks > item.getType().getMaxStackSize() ? oversizedStacks : item.getType().getMaxStackSize();
				final int firstPartial = firstPartial(inventory, item, maxAmount);

				// Drat! no partial stack
				if (firstPartial == -1) {
					// Find a free spot!
					final int firstFree = inventory.firstEmpty();

					if (firstFree == -1) {
						// No space at all!
						leftover.put(i, item);
						break;
					} else {
						// More than a single stack!
						if (item.getAmount() > maxAmount) {
							final ItemStack stack = item.clone();
							stack.setAmount(maxAmount);
							inventory.setItem(firstFree, stack);
							item.setAmount(item.getAmount() - maxAmount);
						} else {
							// Just store it
							inventory.setItem(firstFree, item);
							break;
						}
					}
				} else {
					// So, apparently it might only partially fit, well lets do just that
					final ItemStack partialItem = inventory.getItem(firstPartial);

					final int amount = item.getAmount();
					final int partialAmount = partialItem.getAmount();

					// Check if it fully fits
					if (amount + partialAmount <= maxAmount) {
						partialItem.setAmount(amount + partialAmount);
						break;
					}

					// It fits partially
					partialItem.setAmount(maxAmount);
					item.setAmount(amount + partialAmount - maxAmount);
				}
			}
		}
		return leftover;
	}

}
