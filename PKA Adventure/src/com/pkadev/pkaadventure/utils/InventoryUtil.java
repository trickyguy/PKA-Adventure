package com.pkadev.pkaadventure.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import com.pkadev.pkaadventure.inventories.InventoryMain;
import com.pkadev.pkaadventure.objects.PKAPlayer;
import com.pkadev.pkaadventure.processors.PlayerProcessor;
import com.pkadev.pkaadventure.types.InventoryType;
import com.pkadev.pkaadventure.types.MessageType;

public class InventoryUtil {
	private static Random random = new Random();
	
	public static HashMap<String, Inventory> openInventories = new HashMap<String, Inventory>();
	private static HashMap<InventoryType, Inventory> inventories = new HashMap<InventoryType, Inventory>();
	
	private static Inventory getInventory(InventoryType inventoryType) {
		return inventories.get(inventoryType);
	}
	
	public static void setInventory(InventoryType inventoryType, Inventory inventory) {
		inventories.put(inventoryType, inventory);
	}
	
	public static void load() {
		
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
		PlayerInventory playerInventory = player.getInventory();
		for (int i = 0; i < 9; i++) {
			if (ItemUtil.isWeapon(playerInventory.getItem(i)))
				return i;
		}
		return 9;
	}
	
	public static int getActualWeaponSlot(Player player) {
		PlayerInventory playerInventory = player.getInventory();
		for (int i = 0; i < playerInventory.getSize(); i++) {
			if (ItemUtil.isWeapon(playerInventory.getItem(i)))
				return i;
		}
		return 0;
	}
	
	public static ItemStack getStatItem(Player player) {
		//TODO improve upon
		if (ItemUtil.isStatItem(player.getInventory().getItem(17)))
			return player.getInventory().getItem(17);
		else {
			PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(player);
			if (pkaPlayer == null)
				return new ItemStack(Material.AIR);
			player.getInventory().setItem(17, ItemUtil.getInitialStatItem(pkaPlayer));
			ItemUtil.updateStatItemMeta(player, pkaPlayer);
			return player.getInventory().getItem(17);
		}
	}
	
	public static void setItem(Player player, int slot, ItemStack itemStack) {
		player.getInventory().setItem(slot, itemStack);
	}
	
	public static void addItem(Player player, ItemStack itemStack) {
		player.getInventory().addItem(itemStack);
	}
	
	/**
	 * @param player
	 * @param reference: usually the name of the mob he is opening shop from
	 */
	public static void openInventory(Player player, String reference) {
		player.openInventory(ElementsUtil.getInventoryElement(reference, player.getLevel()));
	}
	
	public static void openShopInventory(Player player, InventoryType inventoryType) {
		Inventory inventory = inventories.get(inventoryType);
		ItemStack[] items = inventory.getContents();
		Inventory inventoryd = Bukkit.createInventory(player, inventory.getSize(), inventory.getName());
		inventoryd.setContents(items);
		inventoryd.setItem(inventory.getSize() - 1, InventoryMain.setPiggyBank(player.getName()));
		player.openInventory(inventoryd);
	}
	
	public static InventoryType getInventoryTypeFromName(String nameReference) {
		return ElementsUtil.getInventoryTypeElement(nameReference);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static Inventory getInitialInventory(String reference, InventoryType inventoryType, int level) {
		MessageUtil.sendMessage(null, "creating new inventory", MessageType.SERVER_DEBUG);
		String configFileReference = "inventories.yml";
		YamlConfiguration inventoriesConfig = FileUtil.getInventoryConfig();
		
		int size = 						FileUtil.getIntValueFromConfig(inventoriesConfig, reference + ".size", configFileReference);
		String title = 					FileUtil.getStringValueFromConfig(inventoriesConfig, reference + ".name", configFileReference);
		List<String> elements = 		FileUtil.getStringArrayFromConfig(inventoriesConfig, reference + ".elements", configFileReference);
		
		Inventory element = Bukkit.createInventory(null, size, title);
		
		if (inventoryType == InventoryType.SHOP_STATIC || inventoryType == InventoryType.SELECT) {
			InventoryUtil.fillStaticInventory(element, level, elements);
		} else if (inventoryType == InventoryType.SHOP_DYNAMIC) {
			List<String> endElements = 	FileUtil.getStringArrayFromConfig(inventoriesConfig, reference + ".endelements", configFileReference);
			InventoryUtil.fillDynamicInventory(element, level, endElements);
		} else if (inventoryType == InventoryType.SHOP_MIXED) {
			List<String> endElements = 	FileUtil.getStringArrayFromConfig(inventoriesConfig, reference + ".endelements", configFileReference);
			InventoryUtil.fillMixedInventory(element, level, elements, endElements);
		}
		
		return element;
	}
	
	//element: 		reference$amount$rarity$slotnumber
	//endElement: 	reference$amount$rarity$amount (second amount = how often will this be placed into inventory)
	
	public static void fillStaticInventory(Inventory inventory, int level, List<String> elements) {
		for (String element : elements) {
			int primaryDivider = 	getInventoryElementDivider(element, -1);
			int secondaryDivider = 	getInventoryElementDivider(element, primaryDivider);
			int tertiaryDivider = 	getInventoryElementDivider(element, secondaryDivider);
			String reference = 		getInventoryElement(element, primaryDivider);
			int amount = 			getInventoryElementAmount(element, primaryDivider, secondaryDivider);
			int rarity = 			getInventoryElementRarity(element, secondaryDivider, tertiaryDivider);
			int slot = 				getInventoryElementSlot(element, tertiaryDivider);
			ItemStack itemStack = 	ItemUtil.getInitialItem(reference, level, rarity);
			itemStack.setAmount(amount);
			inventory.setItem(slot, itemStack);
		}
	}
	
	public static void fillDynamicInventory(Inventory inventory, int level, List<String> endElements) {
		HashMap<ItemStack, Integer> items = 	convertInventoryEndElements(level, endElements);
		List<ItemStack> itemList =			 	new ArrayList<ItemStack>();
		for (ItemStack itemStack : items.keySet()) {
			int placeAmount = 					items.get(itemStack);
			for (int i = 0; i < placeAmount; i++) {
				itemList.add(itemStack);
			}
		}
		Collections.shuffle(itemList);
		int slot = 0;
		for (int i = 0; i < inventory.getSize(); i++) {
			if (itemList.isEmpty())
				return;
			ItemStack itemStack = 				itemList.get(0);
			int placeAmount = 					items.get(itemStack);
			if (random.nextBoolean()) {
				if (hasItemInSlot(inventory, i)) {
					inventory.setItem(i, itemStack);
					if (placeAmount == 1) {
						itemList.remove(itemStack);
					}
				}
			}
			slot++;
		}
	}
	
	public static void fillMixedInventory(Inventory inventory, int level, List<String> elements, List<String> endElements) {
		fillStaticInventory(inventory, level, elements);
		fillDynamicInventory(inventory, level, endElements);
	}
	
	/**
	 *  Integer value is how many of them will be randomly placed in inventory
	 * @param elements
	 * @return
	 */
	private static HashMap<ItemStack, Integer> convertInventoryEndElements(int level, List<String> endElements) {
		HashMap<ItemStack, Integer> convertedElements = new HashMap<ItemStack, Integer>();
		
		for (String endElement : endElements) {
			int primaryDivider = 	getInventoryElementDivider(endElement, -1);
			int secondaryDivider = 	getInventoryElementDivider(endElement, primaryDivider);
			int tertiaryDivider = 	getInventoryElementDivider(endElement, secondaryDivider);
			String reference = 		getInventoryElement(endElement, primaryDivider);
			int amount = 			getInventoryElementAmount(endElement, primaryDivider, secondaryDivider);
			int rarity = 			getInventoryElementRarity(endElement, secondaryDivider, tertiaryDivider);
			int placeAmount = 		getInventoryElementSlot(endElement, tertiaryDivider);
			ItemStack itemStack = 	ItemUtil.getInitialItem(reference, level, rarity);
			itemStack.setAmount(amount);
			convertedElements.put(itemStack, placeAmount);
		}
		
		return convertedElements;
	}
	
	private static int getInventoryElementDivider(String element, int previousDivider) {
		return element.indexOf("$", previousDivider + 1);
	}
	
	private static String getInventoryElement(String element, int divider) {
		return element.substring(0, divider);
	}
	
	private static int getInventoryElementAmount(String element, int primaryDivider, int secondaryDivider) {
		return Integer.parseInt(element.substring(primaryDivider + 1, secondaryDivider));
	}

	private static int getInventoryElementRarity(String element, int secondaryDivider, int tertiaryDivider) {
		if (tertiaryDivider == -1) {
			return Integer.parseInt(element.substring(secondaryDivider + 1));
		} else {
			return Integer.parseInt(element.substring(secondaryDivider + 1, tertiaryDivider));
		}
	}
	
	private static int getInventoryElementSlot(String element, int tertiaryDivider) {
		return Integer.parseInt(element.substring(tertiaryDivider + 1));
	}

	private static boolean hasItemInSlot(Inventory inventory, int slot) {
		if (inventory.getItem(slot).getType() == Material.AIR)
			return false;
		return true;
	}
}



