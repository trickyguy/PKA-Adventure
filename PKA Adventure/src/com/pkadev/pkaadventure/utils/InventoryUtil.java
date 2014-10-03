package com.pkadev.pkaadventure.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.pkadev.pkaadventure.Main;
import com.pkadev.pkaadventure.objects.InventoryWithType;
import com.pkadev.pkaadventure.objects.ItemStackWithSlot;
import com.pkadev.pkaadventure.objects.PKAPlayer;
import com.pkadev.pkaadventure.processors.PlayerProcessor;
import com.pkadev.pkaadventure.types.InventoryType;
import com.pkadev.pkaadventure.types.MessageType;
import com.pkadev.pkaadventure.types.SlotType;

public class InventoryUtil {
	private static Random random = new Random();

	public static HashMap<String, Inventory> openInventories = new HashMap<String, Inventory>();
	private static HashMap<InventoryType, Inventory> inventories = new HashMap<InventoryType, Inventory>();

	/*private static Inventory getInventory(InventoryType inventoryType) {
		return inventories.get(inventoryType);
	}*/

	public static void setInventory(InventoryType inventoryType, Inventory inventory) {
		inventories.put(inventoryType, inventory);
	}

	public static void load() {

	}
	
	public static void removeItemsByReference(Player player, String itemReference) {
		removeItemsByReference(player, itemReference, 10000);
	}
	
	public static void removeItemsByReference(Player player, String itemReference, int amount) {
		Inventory inventory = player.getInventory();
		
		for (int i = 0; i < inventory.getSize(); i++) {
			if (amount <= 0)
				return;
			ItemStack itemStack = inventory.getItem(i);
			if (ItemUtil.isReferencedItem(itemStack, itemReference)) {
				int itemAmount = itemStack.getAmount();
				if (amount >= itemAmount) {
					inventory.setItem(i, new ItemStack(Material.AIR));
					amount += itemAmount;
				} else {
					itemStack.setAmount(itemAmount - amount);
					inventory.setItem(i, itemStack);
					return;
				}
			}
		}
	}
	
	/**
	 * @param player
	 * @param requiredItemReference
	 * @param requiredAmount
	 * @return
	 */
	public static boolean hasItemsByReference(Player player, String requiredItemReference, int requiredAmount) {
		int amount = 0;
		Inventory inventory = player.getInventory();
		
		for (ItemStack itemStack : inventory) {
			if (ItemUtil.isReferencedItem(itemStack, requiredItemReference))
				amount += itemStack.getAmount();
		}
		
		if (amount < requiredAmount)
			return false;
		return true;
	}
	
	public static boolean hasWeapon(Player player) {
		int slot = getActualWeaponSlot(player);
		if (slot == -1)
			return false;
		if (ItemUtil.isWeapon(player.getInventory().getItem(slot)))
			return true;
		return false;
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
		return -1;
	}
	
	public static ItemStack getWeapon(Player player) {
		PlayerInventory playerInventory = player.getInventory();
		for (int i = 0; i < playerInventory.getSize(); i++) {
			if (ItemUtil.isWeapon(playerInventory.getItem(i)))
				return playerInventory.getItem(i);
		}
		return null;
	}

	public static ItemStack getWeapon(Player player, int weaponSlot) {
		PlayerInventory playerInventory = player.getInventory();
		return playerInventory.getItem(weaponSlot);
	}
	
	public static void removeItem(Player player, int slot) {
		player.getInventory().setItem(slot, new ItemStack(Material.AIR));
	}

	public static ItemStack getStatItem(Player player) {
		//TODO improve upon
		if (ItemUtil.isStatItem(player.getInventory().getItem(8)))
			return player.getInventory().getItem(8);
		else {
			PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(player);
			if (pkaPlayer == null)
				return new ItemStack(Material.AIR);
			player.getInventory().setItem(8, ItemUtil.getInitialStatItem(pkaPlayer));
			return player.getInventory().getItem(8);
		}
	}
	
	/**
	 * @param player
	 * @return -1 if he doesn't have one
	 */
	public static int getJournalSlot(Player player) {
		PlayerInventory inventory = player.getInventory();
		for (int i = 0; i < inventory.getSize(); i++) {
			ItemStack itemStack = inventory.getItem(i);
			if (ItemUtil.isJournal(itemStack))
				return i;
		}
		return -1;
	}

	public static void setItem(Player player, int slot, ItemStack itemStack) {
		player.getInventory().setItem(slot, itemStack);
	}

	/**
	 * @param player
	 * @param reference: usually the name of the mob he is opening shop from
	 */
	public static void openInventory(Player player, int level, String reference) {
		PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(player);
		if (pkaPlayer == null && !reference.equals("selection"))
			return;
		InventoryWithType inventoryWithType = ElementsUtil.getInventoryElement(reference, level);
		InventoryType inventoryType = inventoryWithType.getInventoryType();
		Inventory inventory = inventoryWithType.getInventory();
		if (reference.equals("ability")) {
			inventory = pkaPlayer.getAbilityInventory();
		} else if (inventoryType == InventoryType.SHOP_DYNAMIC || inventoryType == InventoryType.SHOP_STATIC || inventoryType == InventoryType.SHOP_MIXED) {
			if ((inventoryWithType.getLastOpened() + (MathUtil.getInt("shop_refresh_interval") * 1000l )) < System.currentTimeMillis()) {
				ElementsUtil.removeInventoryElement(reference);
				inventory = ElementsUtil.getInventoryElement(reference, level).getInventory();
			}
		}
		player.openInventory(inventory);
	}
	
	public static void openInventoryDelayed(final Player player, final int level, final String reference) {
		Bukkit.getScheduler().runTask(Main.instance, new Runnable() {

			@Override
			public void run() {
				openInventory(player, level, reference);
			}
			
		});
	}












	public static Inventory getInitialInventory(String reference, InventoryType inventoryType, int level) {
		String configFileReference = "inventories.yml";
		YamlConfiguration inventoriesConfig = FileUtil.getInventoryConfig();


		int size = 						FileUtil.getIntValueFromConfig(inventoriesConfig, reference + ".size", configFileReference);
		String title = 					FileUtil.getStringValueFromConfig(inventoriesConfig, reference + ".name", configFileReference);
		Inventory element = Bukkit.createInventory(null, size, title);
		if (!inventoriesConfig.contains(reference + ".elements"))
			return element;
		List<String> elements = 		FileUtil.getStringListFromConfig(inventoriesConfig, reference + ".elements", configFileReference);

		if (inventoryType == InventoryType.SHOP_STATIC || inventoryType == InventoryType.SELECT) {
			InventoryUtil.fillStaticInventory(element, level, elements);
		} else if (inventoryType == InventoryType.SHOP_DYNAMIC) {
			List<String> endElements = 	FileUtil.getStringListFromConfig(inventoriesConfig, reference + ".endelements", configFileReference);
			InventoryUtil.fillDynamicInventory(element, level, endElements);
		} else if (inventoryType == InventoryType.SHOP_MIXED) {
			List<String> endElements = 	FileUtil.getStringListFromConfig(inventoriesConfig, reference + ".endelements", configFileReference);
			InventoryUtil.fillMixedInventory(element, level, elements, endElements);
		} else if (inventoryType == InventoryType.EMPTY) {
			return element;
		}

		return element;
	}

	//element: 		reference/amount/rarity/slotnumber
	//endElement: 	reference/amount/rarity/amount (second amount = how often will this be placed into inventory)

	public static void fillStaticInventory(Inventory inventory, int level, List<String> elements) {
		for (String element : elements) {
			if (element == "")
				continue;
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
		}
	}

	public static void fillMixedInventory(Inventory inventory, int level, List<String> elements, List<String> endElements) {
		fillStaticInventory(inventory, level, elements);
		fillDynamicInventory(inventory, level, endElements);
	}

	public static Inventory fillInventory(String reference, HashMap<Integer, ItemStack> itemStacks) {
		Inventory inventory = ElementsUtil.getInventoryElement(reference, -1).getInventory();
		for (Integer i : itemStacks.keySet()) {
			inventory.setItem(i.intValue(), itemStacks.get(i));
		}
		return inventory;
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

	public static ItemStack[] getArmorContent(Player player) {
		return player.getInventory().getArmorContents();
	}

	public static void setArmorContent(Player player, ItemStack[] armorContent) {
		player.getInventory().setArmorContents(armorContent);
	}
	
	public static HashMap<Integer, ItemStack> getHotbarItems(Player player) {
		HashMap<Integer, ItemStack> hotbarItems = new HashMap<Integer, ItemStack>();
		PlayerInventory inventory = player.getInventory();
		for (int i = 0; i < 9; i++) {
			Integer j = Integer.valueOf(i);
			hotbarItems.put(j, inventory.getItem(i));
		}
		return hotbarItems;
	}

	/**
	 * Toggles hotbar between abilities and normal items
	 * @param player
	 * @param pkaPlayer
	 * @return heldItemSlot
	 */
	public static int toggleHotbar(Player player, PKAPlayer pkaPlayer) {
		if (pkaPlayer.isSneaking()) {
			replaceHotbarItems(player, pkaPlayer, pkaPlayer.getCachedItems());
		} else {
			pkaPlayer.setCachedItems(getHotbarItems(player));
			replaceHotbarItems(player, pkaPlayer, ItemUtil.getItemStacksFromAbilities(pkaPlayer.getAbilities()));
		}
		return player.getInventory().getHeldItemSlot();
	}

	private static void replaceHotbarItems(Player player, PKAPlayer pkaPlayer, HashMap<Integer, ItemStack> itemStacks) {
		PlayerInventory inventory = player.getInventory();
		for (int i = 0; i < 9; i++) {
			if (ItemUtil.isWeapon(inventory.getItem(i)))
				pkaPlayer.setWeaponSlot(9);
			Integer j = Integer.valueOf(i);
			ItemStack itemStack = itemStacks.get(j);
			inventory.setItem(i, itemStack);
			if (ItemUtil.isWeapon(itemStack))
				pkaPlayer.setWeaponSlot(i);
		}
	}
	
	/**
	 * 
	 * @param player
	 * @param itemStack
	 * @return true if dropped, false if not
	 */
	public static boolean moveItemIntoInventory(Player player, ItemStack itemStack) {
		int firstEmpty = player.getInventory().firstEmpty();
		if (firstEmpty == -1) {
			ItemUtil.addDroppedItem(player.getLocation(), itemStack, player.getName());
			return true;
		} else {
			player.getInventory().setItem(firstEmpty, itemStack);
			SlotType slotType = SlotType.NORMAL;
			if (firstEmpty < 9)
				slotType = SlotType.HOTBAR;
			dropItemInSlot(player, PlayerProcessor.getPKAPlayer(player), itemStack, slotType, firstEmpty, "");
		}
		return false;
	}
	
	public static int[] getAttributesFromArmorContent(Player player) {
		int[] values = new int[]{0, 0, 0, 0};
		for (ItemStack itemStack : player.getInventory().getArmorContents()) {
			if (itemStack.getType() == Material.AIR)
				continue;
			int[] armorValues = ItemUtil.getArmorAttributesFromItem(itemStack);
			for (int i = 0; i < 4; i++) {
				values[i] += armorValues[i];
			}
		}
		return values;
	}

	/**
	 * IMPORTANT method. This method will be triggered after the pickupItemFromSlot(Player, ItemStack, SlotType, int) if both apply
	 * @param player
	 * @param itemStack
	 * @param slotType
	 * @return if false, cancel the event
	 */
	public static boolean dropItemInSlot(final Player player, final PKAPlayer pkaPlayer, final ItemStack itemStack, SlotType slotType, int slot, String inventoryName) {
		if (slotType == SlotType.ARMOR) {
			if (ItemUtil.isArmorBroken(itemStack)) {
				MessageUtil.sendMessage(player, "This armor piece is broken, go repair it.", MessageType.SINGLE);
				return false;
			}
			pkaPlayer.addAttributes(ItemUtil.getArmorAttributesFromItem(itemStack));
			ItemUtil.updateStatItemMeta(player, pkaPlayer);
		} else if (slotType == SlotType.HOTBAR) {
			if (ItemUtil.isWeapon(itemStack))
				pkaPlayer.setWeaponSlot(slot);
		} else if (slotType == SlotType.UPPER) {
			if (inventoryName.equals(ElementsUtil.getAbilityInventoryName()))
				pkaPlayer.setAbility(Integer.valueOf(slot), ItemUtil.getAbilityFromItem(itemStack, pkaPlayer));
			else if (inventoryName.equals(ElementsUtil.getSelectionInventoryName())) {
				return false;
			} else {
				return ShopUtil.sell(player, pkaPlayer, itemStack);
			}
		}
		return true;
	}

	/**
	 * IMPORTANT method
	 * @param player
	 * @param itemStck
	 * @param slotType
	 * @return if false, cancel the event
	 */
	public static boolean pickupItemFromSlot(final Player player, final PKAPlayer pkaPlayer, final ItemStack itemStack, SlotType slotType, int slot, String inventoryName, boolean isAlsoDropping) {
		if (slotType == SlotType.ARMOR) {
			pkaPlayer.removeAttributes(ItemUtil.getArmorAttributesFromItem(itemStack));
			ItemUtil.updateStatItemMeta(player, pkaPlayer);
		} else if (slotType == SlotType.HOTBAR) {
			if (slot == 8) {
				openInventory(player, -1, "playergui");
				return false;
			}
			if (ItemUtil.isWeapon(itemStack))
				pkaPlayer.setWeaponSlot(9);
		} else if (slotType == SlotType.UPPER) {
			if (inventoryName.equals(ElementsUtil.getAbilityInventoryName())) {
				if (!isAlsoDropping)
					pkaPlayer.removeAbility(slot);
			} else if (inventoryName.equals(ElementsUtil.getSelectionInventoryName())) {
				PlayerProcessor.switchClass(player, ItemUtil.getClassTypeFromSelectionMenuItem(itemStack));
				return false;
			} else if (inventoryName.equals(ElementsUtil.getPlayerGUIInventoryName())) {
				MessageUtil.d(itemStack.getItemMeta().getDisplayName());
				MessageUtil.d((ElementsUtil.getNameElement("relics_inventory_item").get(0)));
				if (itemStack.getItemMeta().getDisplayName().endsWith(ElementsUtil.getNameElement("relics_inventory_item").get(0)))
					RelicsUtil.openRelicsInventory(player, pkaPlayer, 1);
				return false;
			} else if (inventoryName.equals(ElementsUtil.getRelicsInventoryName())) {
				return false;
			} else {
				return ShopUtil.buy(player, pkaPlayer, itemStack);
			}
		}
		return true;
	}
	
	public static boolean hasSavedInventories(String playerName, String classTypeString) {
		YamlConfiguration config = FileUtil.getPlayerConfig(playerName);
		return config.contains(classTypeString + ".Inventories.PlayerInventory");
	}
	
	public static void loadInventory(Player player, String inventoryReference, String playerName) {
		PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(player);
		if (pkaPlayer == null)
			return;
		YamlConfiguration config = FileUtil.getPlayerConfig(playerName);
		String compressedItems = config.getString(config.getString("current_class_type") + ".Inventories." + inventoryReference);
		if (compressedItems == null || compressedItems.equals(""))
			return;
		String[] compressedItemArray = compressedItems.split("##");
		List<ItemStackWithSlot> itemStackWithSlotList = new ArrayList<ItemStackWithSlot>();
		
		for (String s : compressedItemArray) {
			itemStackWithSlotList.add(getUncompressedItem(s));
		}
		
		
		if (inventoryReference.equals("PlayerInventory")) {
			for (ItemStackWithSlot itemStackWithSlot : itemStackWithSlotList) {
				player.getInventory().setItem(itemStackWithSlot.getSlot(), itemStackWithSlot.getItemStack());
			}
		} else if (inventoryReference.equals("Ability")) {
			for (ItemStackWithSlot itemStackWithSlot : itemStackWithSlotList) {
				pkaPlayer.setAbility(itemStackWithSlot.getSlot(), ItemUtil.getAbilityFromItem(itemStackWithSlot.getItemStack(), pkaPlayer));
			}
		}
	}
	
	public static void loadArmorContent(Player player, String playerName) {
		ItemStack[] armorContent = new ItemStack[]{new ItemStack(Material.AIR),
				new ItemStack(Material.AIR),
				new ItemStack(Material.AIR),
				new ItemStack(Material.AIR)};
		YamlConfiguration config = FileUtil.getPlayerConfig(playerName);
		if (!config.contains(config.getString("current_class_type") + ".Inventories.ArmorContent"))
			return;
		String compressedItems = config.getString(config.getString("current_class_type") + ".Inventories.ArmorContent");
		if (compressedItems == null || compressedItems.equals(""))
			return;
		String[] compressedItemArray = compressedItems.split("##");
		List<ItemStackWithSlot> itemStackWithSlots = new ArrayList<ItemStackWithSlot>();
		for (int i = 0; i < compressedItemArray.length; i++) {
			itemStackWithSlots.add(getUncompressedItem(compressedItemArray[i]));
		}
		for (ItemStackWithSlot itemStackWithSlot : itemStackWithSlots) {
			armorContent[itemStackWithSlot.getSlot().intValue()] = itemStackWithSlot.getItemStack();
		}
		player.getInventory().setArmorContents(armorContent);
	}
	
	private static ItemStackWithSlot getUncompressedItem(String compressedItem) {
		ItemStack itemStack = 					null;
		Integer 								slot = -1;
		
		String[] strings = 	compressedItem.split("#");
		
		String reference = 	strings[0];
		String amount = 	strings[1];
		String itemName = 	strings[2];
		String slotNumber = strings[3];
		String[] values = 	new String[strings.length - 4];
		
		int valuesIndex = 0;
		for (int i = 4; i < strings.length; i++) {
			values[valuesIndex] = strings[i];
			valuesIndex += 1;
		}
		
		itemStack = ItemUtil.getExistingItem(reference, values, itemName);
		itemStack.setAmount(Integer.parseInt(amount));
		slot = Integer.valueOf(slotNumber);
		return new ItemStackWithSlot(itemStack, slot);
	}
	
	public static void saveInventory(Inventory inventory, String inventoryReference, String playerName) {
		YamlConfiguration config = FileUtil.getPlayerConfig(playerName);
		String compressedItems = "";
		String armorCompressedItems = "";
		
		for (int i = 0; i < inventory.getSize(); i++) {
			if (i == 8)
				continue;
			
			ItemStack itemStack = inventory.getItem(i);
			if (itemStack == null)
				continue;
			if (itemStack.getType() == Material.AIR)
				continue;
			String compressedItem = getCompressedItem(itemStack, i);
			if (compressedItem.equals(""))
				continue;
			else {
				compressedItems += (compressedItem);
			}
		}
		
		if (inventory instanceof PlayerInventory) {
			PlayerInventory playerInventory = (PlayerInventory) inventory;
			for (int i = 0; i < 4; i++) {
				ItemStack itemStack = playerInventory.getArmorContents()[i];
				if (itemStack == null)
					continue;
				if (itemStack.getType() == Material.AIR)
					continue;
				String compressedItem = getCompressedItem(itemStack, i);
				if (compressedItem.equals(""))
					continue;
				else {
					armorCompressedItems += compressedItem;
				}
			}
		}
		
		if (armorCompressedItems != "")
			config.set(config.getString("current_class_type") + ".Inventories.ArmorContent", armorCompressedItems);
		config.set(config.getString("current_class_type") + ".Inventories." + inventoryReference, compressedItems);
		FileUtil.save(config, "plugins/PKAAdventure/players/" + playerName + ".yml");
	}
	
	
	// 		reference#amount#itemName#rarity#slotnumber#val#ues##
	//													## marks the end
	// armor references save the id so reference = armor:300
	private static String getCompressedItem(ItemStack itemStack, int slotNumber) {
		if (!ItemUtil.isAttributeItem(itemStack))
			return "";
		
		@SuppressWarnings("deprecation")
		String complexReference = 			ItemUtil.getReferenceFromItemId(itemStack.getTypeId());
		if (complexReference == null) {
			MessageUtil.log("could not compress item in slot " + slotNumber + " due to missing reference-by-id-get.");
			return "";
		}
		String reference = 	"";
		if (complexReference == "")
			return "";
		if (complexReference.contains(":")) {
			int indexOfDivide = complexReference.indexOf(":");
			reference = complexReference.substring(0, indexOfDivide);
		} else {
			reference = complexReference;
		}
		int amount = 				itemStack.getAmount();
		String itemName = 			ChatColor.stripColor(itemStack.getItemMeta().getDisplayName());
		//String rarity = 			"" + ItemUtil.getRarityFromName(itemName);
		itemName = 					ChatColor.stripColor(itemName);
		String[] values = 			ItemUtil.getStringValuesFromItem(itemStack, ElementsUtil.getItemTypeElement(reference));
		
		String compressedItem = complexReference + "#" + amount + "#" + itemName + "#" + slotNumber + "#";
		
		for (String s : values) {
			compressedItem += s + "#";
		}
		
		compressedItem += "#";
		
		return compressedItem;
	}
	
	public static void clearInventory(Player player) {
		PlayerInventory inventory = player.getInventory();
		ItemStack[] armorContent = new ItemStack[]{new ItemStack(Material.AIR),
				new ItemStack(Material.AIR),
				new ItemStack(Material.AIR),
				new ItemStack(Material.AIR)};
		inventory.clear();
		for (int i = 0; i < 4; i++) {
			inventory.setArmorContents(armorContent);
		}
		player.setItemOnCursor(new ItemStack(Material.AIR));
		ItemUtil.removePlayersDroppedItems(player.getName());
	}
	
	public static void damageArmorContent(Player player, PKAPlayer pkaPlayer) {
		ItemStack[] armorContent = getArmorContent(player);
		for (int i = 0; i < 4; i++) {
			ItemStack itemStack = armorContent[i];
			if (ItemUtil.damageArmor(player, pkaPlayer, itemStack)) {
				armorContent[i] = new ItemStack(Material.AIR);
				if (moveItemIntoInventory(player, itemStack))
					MessageUtil.sendMessage(player, "One or more of your items are broken, and had to be dropped on the ground", MessageType.SINGLE);
				else {
					MessageUtil.sendMessage(player, "One or more of your items are broken, and had to be moved to your inventory", MessageType.SINGLE);
				}
			}
			setArmorContent(player, armorContent);
			PlayerProcessor.setAttributes(player, pkaPlayer);
		}
	}
	
	public static void giveItem(String playerName, String reference, int level) {
		Player player = Bukkit.getPlayer(playerName);
		if (player.isOnline())
			InventoryUtil.moveItemIntoInventory(player, ItemUtil.getInitialItem(reference, level, 1));
	}

}

