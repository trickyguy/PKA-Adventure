package com.pkadev.pkaadventure.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import com.pkadev.pkaadventure.Main;
import com.pkadev.pkaadventure.interfaces.Ability;
import com.pkadev.pkaadventure.objects.ItemType;
import com.pkadev.pkaadventure.objects.PKAPlayer;
import com.pkadev.pkaadventure.types.AbilityType;
import com.pkadev.pkaadventure.types.ClassType;


public class ItemUtil {
	private static Random random = new Random();
	private static Main plugin = Main.instance;

	public static void load() {
		startTimer();
	}

	/**
	 * used in order to only give certain players certain items
	 */
	private static HashMap<Item, String> droppedItems = new HashMap<Item, String>();

	/**
	 * removes them if the tick is at 1
	 */
	private static HashMap<Item, Integer> droppedItemsRemoveTick = new HashMap<Item, Integer>();

	public static void addDroppedItem(Item item, String playerName) {
		droppedItems.put(item, playerName);
		droppedItemsRemoveTick.put(item, 5);
	}
	
	public static void addDroppedItem(Location location, ItemStack itemStack, String playerName) {
		Item item = location.getWorld().dropItem(location, itemStack);
		addDroppedItem(item, playerName);
	}

	public static String getDroppedItemOwner(Item item) {
		if (droppedItems.containsKey(item)) {
			return droppedItems.get(item);
		} else {
			return "";
		}
	}

	public static void removeDroppedItem(Item item) {
		if (droppedItems.containsKey(item))
			droppedItems.remove(item);
		if (droppedItemsRemoveTick.containsKey(item))
			droppedItemsRemoveTick.remove(item);
		item.remove();
	}

	private static void removeExistingDroppedItem(Item item) {
		droppedItems.remove(item);
		droppedItemsRemoveTick.remove(item);
		item.remove();
	}
	
	private static void tickDroppedItems() {
		ArrayList<Item> temp = new ArrayList<Item>();
		for (Item item : droppedItems.keySet()) {
			int value = droppedItemsRemoveTick.get(item);
			if (value == 1) {
				temp.add(item);
			} else {
				droppedItemsRemoveTick.put(item, value - 1);
			}
		}
		
		for (Item item : temp) {
			removeExistingDroppedItem(item);
		} temp.clear();
	}


	private static void startTimer() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

			@Override
			public void run() {
				tickDroppedItems();
			}

		}, 1200, 600);
	}

	/**
	 * To skip all the annoying if statements
	 * @param is The item stack you want to check
	 * @return
	 */
	public static boolean isAttributeItem(ItemStack itemStack) {
		if (itemStack == null || itemStack.getType() == Material.AIR || !itemStack.hasItemMeta() || !itemStack.getItemMeta().hasLore())
			return false;
		return true;
	}

	public static boolean isArmorItem(ItemStack itemStack) {
		String materialString = itemStack.getType().toString();
		if (materialString.endsWith("_HELMET") || materialString.endsWith("_CHESTPLATE") || materialString.endsWith("_LEGGINGS") || materialString.endsWith("_BOOTS"))
			return true;
		return false;
	}

	public static boolean isWeapon(ItemStack itemStack) {
		if (!isAttributeItem(itemStack))
			return false;
		String name = itemStack.getItemMeta().getDisplayName();
		name = ChatColor.stripColor(name);
		if (name.startsWith("Woody's") || name.startsWith("Kyle's") || name.startsWith("Wing's") || name.startsWith("Lefty's"))
			return true;
		return false;
	}
	
	public static boolean isAbilityItem(ItemStack itemStack) {
		if (!isAttributeItem(itemStack))
			return false;
		List<String> possibleIds = FileUtil.getStringListFromConfig(FileUtil.getItemTypeConfig(), "ability_id.id", "itemtypes.yml");
		if (possibleIds.contains(itemStack.getType().toString()))
			return true;
		return false;
	}

	public static ItemStack getWeapon(Player player) {
		PlayerInventory playerInventory = player.getInventory();
		for (int i = 0; i < playerInventory.getSize(); i++) {
			if (isWeapon(playerInventory.getItem(i)))
				return playerInventory.getItem(i);
		}
		return null;
	}

	public static ItemStack getWeapon(Player player, int weaponSlot) {
		PlayerInventory playerInventory = player.getInventory();
		return playerInventory.getItem(weaponSlot);
	}

	public static boolean isStatItem(ItemStack itemStack) {
		if (!isAttributeItem(itemStack))
			return false;
		if (itemStack.getType() == Material.ENDER_PEARL || itemStack.getType() == Material.EYE_OF_ENDER)
			if (itemStack.getItemMeta().getDisplayName().endsWith("points!"))
				return true;
		return false;
	}
	
	public static void removeInventoryItems(Inventory inv, ItemStack item) {
		removeInventoryItems(inv, item.getType(), item.getAmount());
	}

	public static void removeInventoryItems(Inventory inv, Material type, int amount) {
		ItemStack[] items = inv.getContents();
		for (int i = 0; i < items.length; i++) {
			ItemStack is = items[i];
			if (is != null && is.getType() == type) {
				int newamount = is.getAmount() - amount;
				if (newamount > 0) {
					is.setAmount(newamount);
					break;
				} else {
					items[i] = new ItemStack(Material.AIR);
					amount = -newamount;
					if (amount == 0) break;
				}
			}
		}
		inv.setContents(items);
	}

	public static int getTotalItems(Inventory inv, Material type) {
		int amount = 0;
		for (ItemStack is : inv.getContents()) {
			if (is != null) {
				if (is.getType() == type) {
					amount += is.getAmount();
				}
			}
		}
		return amount;   
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static int[] getAttributesFromItemStack(ItemStack itemStack) {
		return getValuesFromItem(itemStack, 1);
	}

	public static ClassType getClassTypeFromSelectionMenuItem(ItemStack itemStack) {
		if (!isAttributeItem(itemStack))
			return ClassType.NONE;
		String itemName = itemStack.getItemMeta().getDisplayName();
		if 			(itemName.equals(ElementsUtil.getLoreElementMod("woody_selection_name"))) {
			return ClassType.WOODY;
		} else if 	(itemName.equals(ElementsUtil.getLoreElementMod("wings_selection_name"))) {
			return ClassType.WINGS;
		} else if 	(itemName.equals(ElementsUtil.getLoreElementMod("lefty_selection_name"))) {
			return ClassType.LEFTY;
		} else if 	(itemName.equals(ElementsUtil.getLoreElementMod("kyle_selection_name"))) {
			return ClassType.KYLE;
		}
		return ClassType.NONE;
	}

	public static void updateWeaponLore(ItemStack itemStack, ClassType classType, int level) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		List<String> itemLore = new ArrayList<String>();
		switch(classType) {
		case WOODY: {
			itemMeta.setDisplayName("�6Woody's Sword");
			itemLore.add(ElementsUtil.getInitialLoreElement("woody_damage", level));
			break;
		}
		case WINGS: {
			itemMeta.setDisplayName("�6Wings's Sword");
			itemLore.add(ElementsUtil.getInitialLoreElement("wings_damage", level));
			break;
		}
		case LEFTY: {
			itemMeta.setDisplayName("�6Lefty's Sword");
			itemLore.add(ElementsUtil.getInitialLoreElement("lefty_damage", level));
			break;
		}
		case KYLE: {
			itemMeta.setDisplayName("�6Kyle's Sword");
			itemLore.add(ElementsUtil.getInitialLoreElement("kyle_damage", level));
			break;
		}
		default:return;
		}
	}

	private static int[] getValuesFromItem(ItemStack itemStack, int startLine) {
		boolean isArmor = isArmorItem(itemStack);
		List<String> itemLore = itemStack.getItemMeta().getLore();
		int[] values = new int[itemLore.size() - startLine];

		for (int i = startLine; i < itemLore.size(); i++) {
			String line = itemLore.get(i);
			byte[] bytes = stripAndGetBytes(line);

			//attributes[addto] = value;
			int addto = 1;
			//value in the form of String
			String value = "";
			//       j = 8 so we skip most of the word
			for (int j = 8; j < bytes.length; j++) {
				Character c = (char) bytes[j];
				if (isArmor) {
					if (c == ' ') {
						/*toughness is default, it's rewarded most*/
						if (j == 9/*strength*/) {
							addto = 0;
						} else if (j == 8/*agility*/) {
							addto = 2;
						} else if (j == 12/*restoration*/) {
							addto = 3;
						}

						//skip the �f
						continue;
					} else {
						if (Character.isDigit(c)) {
							value += c;
						}
					}
				} else {
					if (Character.isDigit(c))
						value += c;
					addto = i;
				}
			}
			values[addto] = Integer.valueOf(value);
		}

		return values;
	}

	/**
	 * @param itemLore
	 * @return int[0] = exp, int[1] = reqexp for next level
	 */
	private static int[] getExpFromSkillItemLore(List<String> itemLore) {
		int[] exp = new int[2];
		byte[] bytes = stripAndGetBytes(itemLore.get(1));

		String xp = "";
		String req = "";

		boolean xporreq = true;
		for (int i = 5; i < bytes.length; i++) {
			Character c = (char) bytes[i];
			if (xporreq) {
				if (c == '/') {
					xporreq = false;
					continue;
				}
				xp += c;
			} else {
				req += c;
			}
		}

		exp[0] = Integer.parseInt(xp);
		exp[1] = Integer.parseInt(req);

		return exp;
	}

	/**
	 * if an ability item has been moved into the ability inventory, or moved to a new slot
	 * @param slot
	 * @return
	 */
	public static Ability getAbilityFromItem(ItemStack itemStack) {
		String itemName = ChatColor.stripColor(itemStack.getItemMeta().getDisplayName());
		itemName = itemName.replace(' ', '_');
		return AbilityType.valueOf(itemName).getAbility();
	}
	
	public static ItemStack getItemFromAbility(Ability ability) {
		return ItemUtil.getExistingItem(ability.getReference(), ability.getValues(), ability.getRarity());
	}
	
	public static HashMap<Integer, ItemStack> getItemStacksFromAbilities(HashMap<Integer, Ability> abilities) {
		HashMap<Integer, ItemStack> itemStacks = new HashMap<Integer, ItemStack>();
		for (Integer i : abilities.keySet()) {
			if (abilities.containsValue(i) && abilities.get(i) != null)
				itemStacks.put(i, getItemFromAbility(abilities.get(i)));
			else {
				itemStacks.put(i, new ItemStack(Material.AIR));
			}
		}
		return itemStacks;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * @param line
	 * @return The line in form of byte[]
	 */
	private static byte[] stripAndGetBytes(String line) {
		line = ChatColor.stripColor(line);
		byte[] bytes = new byte[line.length()];
		bytes = line.getBytes();
		return bytes;
	}

	/**
	 * @param itemLore
	 * @param modification: The value you are looking for. i.e.: "Strength", "Toughness", "Level"
	 * @return -1 if it couldn't find a value
	 */
	public static int getIntValueFromLore(List<String> itemLore, String modification) {	
		String value = getStringValueFromLore(itemLore, modification);
		if (value == "") return -1;
		return Integer.parseInt(value);
	}

	/**
	 * @param itemLore
	 * @param modification: The value you are looking for. i.e.: "Strength", "Toughness", "Level"
	 * @return -1 if it couldn't find a value
	 */
	private static String getStringValueFromLore(List<String> itemLore, String modification) {	
		for (String line : itemLore) {
			String value = "";
			String mod = "";
			byte[] bytes = stripAndGetBytes(line);
			//modorval = will it add to modification or value
			boolean modorval = true;
			for (int i = 0; i < bytes.length; i++) {
				Character c = (char) bytes[i];
				if (modorval) {
					if (c == ':') {
						modorval = false;
						i += 1;
						continue;
					} else {
						mod += c;
					}
				} else {
					value += c;
				}
			}
			if (mod.equals(modification)) return value;
		}
		return "";
	}

	/**
	 * @param itemLore
	 * @param lineIndex: The line you wish to get the value of
	 * @return The value of a certain line
	 */
	private static String getValueFromString(String lineString) {
		if (lineString.contains("/")) {
			return getValueFromBytesDivide(stripAndGetBytes(lineString));
		}
		return getValueFromBytes(stripAndGetBytes(lineString));
	}
	
	private static String getValueFromBytes(byte[] bytes) {
		String value = "";
		for (int i = bytes.length - 1; i > 1; i--) {
			Character c = (char) bytes[i];
			if (c == ' ')
				return value;
			value += c;
		}
		return value;
	}
	
	private static String getValueFromBytesDivide(byte[] bytes) {
		String value = "";
		boolean isValue = false;
		for (int i = bytes.length - 2; i > 1; i--) {
			Character c = (char) bytes[i];
			if (c == ' ')
				return value;
			if (isValue)
				value += c;
			if (c == '/')
				isValue = true;
		}
		return value;
	}

	/**
	 * lets you replace the value of a certain modification with a new value, it finds the line that modification is at
	 * @param itemLore
	 * @param modificationToBeReplaced
	 * @param newValue
	 */
	public static void replaceValueInItemLore(List<String> itemLore, String referenceToBeReplaced, String newValue) {
		replaceValueInItemLore(itemLore, referenceToBeReplaced, Integer.parseInt(newValue));
	}
	
	/**
	 * lets you replace the value of a certain modification with a new value, it finds the line that modification is at
	 * @param itemLore
	 * @param modificationToBeReplaced
	 * @param newValue
	 */
	public static void replaceValueInItemLore(List<String> itemLore, String referenceToBeReplaced, int newValue) {
		String modificationToBeReplaced = ElementsUtil.getLoreElementMod(referenceToBeReplaced);
		for (int i = 0; i < itemLore.size(); i++) {
			if (itemLore.get(i).startsWith(modificationToBeReplaced)) {
				itemLore.set(i, ElementsUtil.getExistingLoreElement(referenceToBeReplaced, newValue));
				return;
			}
		}
	}
	
	/**
	 * @param itemLore
	 * @param referenceToBeReplaced
	 * @param value
	 * @return the new value
	 */
	public static int addValueInItemLore(List<String> itemLore, String referenceToBeReplaced, int value) {
		String modificationToBeReplaced = ElementsUtil.getLoreElementMod(referenceToBeReplaced);
		for (int i = 0; i < itemLore.size(); i++) {
			if (itemLore.get(i).startsWith(modificationToBeReplaced)) {
				int oldValue = Integer.parseInt(getValueFromString((itemLore.get(i))));
				int newValue = oldValue + value;
				return newValue;
			}
		}
		return -1;
	}
	
	
	
	
	
	
	


	
	
	
	
	public static void updateStatItemMeta(Player player, PKAPlayer pkaPlayer) {
		ItemStack itemStack = InventoryUtil.getStatItem(player);
		ItemMeta itemMeta = itemStack.getItemMeta();
		updateStatItemMetaLore(itemMeta, pkaPlayer);
		updateStatItemMetaName(itemMeta, pkaPlayer);
		itemStack.setItemMeta(itemMeta);
	}

	private static void updateStatItemMetaLore(ItemMeta itemMeta, PKAPlayer pkaPlayer) {
		// new String[]{"stat_item_strength", "stat_item_toughness", "stat_item_agility", "stat_item_restoration"
		//TODO update this?
		List<String> attributes = new ArrayList<String>();
		attributes.add("strength");
		attributes.add("toughness");
		attributes.add("agility");
		attributes.add("restoration");
		List<String> newLore = ElementsUtil.getMultipleExistingLoreElements(attributes,
				pkaPlayer.getAttributes());
		itemMeta.setLore(newLore);
	}
	
	private static void updateStatItemMetaName(ItemMeta itemMeta, PKAPlayer pkaPlayer) {
		int availableUpgradePoints = pkaPlayer.getAvailableUpgradePoints();
		String newName = "";
		if (availableUpgradePoints == 0)
			newName = "�r�6No �5available upgrade points!";
		else {
			newName = "�r�6" + availableUpgradePoints + " �5available upgrade points!";
		}
		itemMeta.setDisplayName(newName);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * if rareItemInt == -1 then it wont give out a rare item otherwise it does
	 * !!make sure to remove the "" before fetching this list
	 * @param level
	 * @param rareItemInt
	 * @return
	 */
	public static HashMap<String, List<ItemStack>> getNewItemDrop(Set<String> playersSet, String mobName, int level, int rareItemInt) {
		HashMap<String, List<ItemStack>> finalDrops = new HashMap<String, List<ItemStack>>();
		
		List<String> players = new ArrayList<String>();
		List<String> drops   = ElementsUtil.getDropElement(mobName);
		players.addAll(playersSet);
		Collections.shuffle(drops);
		Collections.shuffle(players);
		
		int amount = 1 + random.nextInt(MathUtil.getInt("max_mobdrop_amount"));
		for (int i = 0; i < amount; i++) {
			if (drops.size() <= i || players.size() <= i)
				break;
			int slot = 		0;
			int rarity = 	0;
			
			if (rareItemInt != -1) {
				int rarityDeterminer = random.nextInt(10);
				if (rarityDeterminer < 6)
					rarity = 1;
				else if (rarityDeterminer < 8)
					rarity = 2;
				else {
					rarity = 3;
				}
				slot = rareItemInt;
				rareItemInt = -1;
			}
			
			ItemStack drop = new ItemStack(Material.AIR);
			
			if (drops.get(i).equals("armor")) {
				drop = ItemUtil.getInitialArmor(level, slot, rarity);
			} else {
				drop = ItemUtil.getInitialItem(drops.get(i), level, rarity);
			}
			
			List<ItemStack> finalDropsForPlayer = new ArrayList<ItemStack>();
			finalDropsForPlayer.add(new ItemStack(Material.GOLD_NUGGET, MathUtil.getValue(level, "gold_mobdrop_amount")));
			finalDropsForPlayer.add(drop);
			finalDrops.put(players.get(i), finalDropsForPlayer);
		}
		
		return finalDrops;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * get any kind of item that can be referenced with itemMeta
	 * @param reference: i.e.: "bread", "armor"
	 * @param level: the level at which this item is initiated, some references dont use this information
	 * @param rarity: only determines the color of writing given (0,1,2,3)
	 * @return
	 */
	public static ItemStack getInitialItem(String reference, int level, int rarity) {
		ItemStack itemStack = 	ElementsUtil.getItemElement(reference);
		return getInitialItem(itemStack, reference, level, rarity);
	}
	
	private static ItemStack getInitialItem(ItemStack itemStack, String reference, int level, int rarity) {
		ItemMeta itemMeta = 	itemStack.getItemMeta();
		List<String> itemLore = getInitialItemLore(reference, level);
		String itemName = 		getInitialItemName(reference, rarity);
		itemMeta.setDisplayName(itemName);
		itemMeta.setLore(itemLore);
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}
	
	public static ItemStack getInitialArmor(int level, int slot, int rarity) {
		ItemStack itemStack = 	getInitialArmorItemStack(level, slot, rarity);
		return getInitialItem(itemStack, "armor", level, rarity);
	}
	
	private static ItemStack getInitialArmorItemStack(int level, int slot, int rarity) {
		//TODO USE RARE
		String material = "LEATHER_";
		String item = "HELMET";
		if (level < 10) {

		} else if (level < 20) {
			material = "CHAINMAIL_";
		} else if (level < 30) {
			material = "IRON_";
		} else if (level < 40) {
			material = "GOLD_";
		} else {
			material = "DIAMOND_";
		}
		if (slot == 0) {

		} else if (slot == 1) {
			item = "CHESTPLATE";
		} else if (slot == 2) {
			item = "LEGGINGS";
		} else if (slot == 3) {
			item = "BOOTS";
		}
		return new ItemStack(Material.valueOf(material + item));
	}

	public static ItemStack[] getInitialContent(int level, int rareItemInt) {
		//TODO rareItemInt implementation (-1 means there will be none)
		ItemStack[] armorContent = new ItemStack[4];
		for (int i = 0; i < 4; i++) {
			if (random.nextBoolean()) {
				//TODO implement rarity
				armorContent[i] = getInitialArmorItemStack(level, i, -1);
			} else {
				armorContent[i] = new ItemStack(Material.AIR);
			}
		}
		return armorContent;
	}
	
	public static ItemStack getInitialStatItem(PKAPlayer pkaPlayer) {
		ItemStack statItem = null;
		if (pkaPlayer.getAvailableUpgradePoints() == 0) {
			statItem = new ItemStack(Material.ENDER_PEARL);
		} else {
			statItem = new ItemStack(Material.EYE_OF_ENDER);
		}
		ItemMeta itemMeta = statItem.getItemMeta();
		updateStatItemMetaLore(itemMeta, pkaPlayer);
		updateStatItemMetaName(itemMeta, pkaPlayer);
		statItem.setItemMeta(itemMeta);
		return statItem;
	}
	
	public static ItemStack getExistingItem(String reference, int[] values, int rarity) {
		ItemStack itemStack = ElementsUtil.getItemElement(reference);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(getExistingItemName(reference, rarity));
		itemMeta.setLore(getExistingItemLore(reference, values));
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}
	
	
	
	
	
	
	
	
	
	
	public static ItemStack getClassWeapon(ClassType classType) {
		Material material = null;
		switch(classType) {
		case WOODY: {
			material = Material.WOOD_SWORD;
			break;
		}
		case WINGS: {
			material = Material.BOW;
			break;
		}
		case LEFTY: {
			material = Material.GOLD_AXE;
			break;
		}
		case KYLE: {
			material = Material.BOW;
			break;
		}
		default:material = Material.AIR;
		}
		return new ItemStack(material);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * puts a new itemMeta onto the given itemStack depending on the kind of item, the level and the rarity
	 */
	private static List<String> getInitialItemLore(String reference, int level) {
		List<String> itemLore = new ArrayList<String>();
		ItemType itemType = ElementsUtil.getItemTypeElement(reference);
		itemLore.addAll(ElementsUtil.getMultipleInitialLoreElements(itemType.getElements(), level));
		List<String> endElements = itemType.getEndElements();
		List<String> finalEndElements = new ArrayList<String>();
		Collections.shuffle(endElements);
		int maxEndElements = itemType.getMaxEndElements();
		for (int i = 0; i < maxEndElements; i++) {
			String element = endElements.get(i);
			if (finalEndElements.contains(element))
				maxEndElements++;
			else {
				finalEndElements.add(element);
			}
		}
		itemLore.addAll(ElementsUtil.getMultipleInitialLoreElements(finalEndElements, level));
		return itemLore;
	}
	
	/**
	 * cannot add endElements
	 * @param reference
	 * @param values
	 * @return
	 */
	private static List<String> getExistingItemLore(String reference, int[] values) {
		List<String> itemLore = new ArrayList<String>();
		ItemType itemType = ElementsUtil.getItemTypeElement(reference);
		itemLore.addAll(ElementsUtil.getMultipleExistingLoreElements(itemType.getElements(), values));
		return itemLore;
	}
	
	private static String getInitialItemName(String reference, int rarity) {
		List<String> possibleNames = ElementsUtil.getNameElement(reference);
		String itemName = possibleNames.get(random.nextInt(possibleNames.size()));
		return getFinalizedItemName(itemName, rarity);
	}
	
	/**
	 * gets the first entry in the list of possible names
	 * @param reference
	 * @return
	 */
	private static String getExistingItemName(String reference, int rarity) {
		return getFinalizedItemName(ElementsUtil.getNameElement(reference).get(0), rarity);
	}
	
	private static String getFinalizedItemName(String itemName, int rarity) {
		switch (rarity) {
		case 1: {
			itemName += "�7";
			break;
		}
		case 2: {
			itemName += "�e";
			break;
		}
		case 3: {
			itemName += "�b";
			break;
		}
		case 4: {
			itemName += "�d";
			break;
		}
		default:return "INVALID RARITY VALUE";
		}
		return itemName;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	/**
	 * used to compress items for the file (including itemMeta)
	 * # = typeId
	 * ! = itemType
	 * * = itemName
	 * $ = itemMeta next element
	 * % = itemMeta next endElement
	 * Elements are like this: number/number: first number is the element id second number is the value on that line
	 * Example of elements: %5/1$3/2
	 * Full example: #13!SKILL*�6Kidney$5/1%3/1 = itemId(13) itemName(�6Kidney) itemLore line1(element 5, value 1) itemLore line2(endElement 3, value 2)
	 * @param itemStack
	 * @return
	 
	private static String getCompressedItem(ItemStack itemStack) {
		String compressedString = "";
		compressedString += ("#" + itemStack.getTypeId());
		if (!isAttributeItem(itemStack))
			return compressedString;
		ItemType itemType = getItemType(itemStack);
		if (itemType == null)
			return compressedString;
		compressedString += ("!" + itemType.toString());
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (itemMeta.hasDisplayName())
			compressedString += ("*" + itemMeta.getDisplayName());
		List<String> itemLore = itemMeta.getLore();
		for (int i = 0; i < itemType.getElements().length; i++) {
			compressedString += ("$" + i + "/" + getValueFromLoreLine(itemLore.get(i)));
		}
		for (int i = itemType.getElements().length; i < itemType.getEndElements().length; i++) {
			String mod = itemLore.get(i);
			for (int j = 0; j < itemType.getEndElements().length; i++) {
				//im checking if the line starts with any of the endElements of that itemType
				if (mod.startsWith(getMod(itemType.getEndElements()[j]))) {
					compressedString += ("%" + j + "/" + getValueFromLoreLine(itemLore.get(i)));
					break;
				}	
			}
		}
		return compressedString;
	}

	private static ItemStack getUncompressedItem(String compressedString) {
		ItemStack itemStack = null;
		ItemMeta itemMeta = null;
		ItemType itemType = null;
		int typeId = 0;
		String itemName = null;
		List<String> itemLore = new ArrayList<String>();

		byte[] bytes = getBytes(compressedString);
		Character currentPrefix = null;
		String currentMod = "";
		boolean isValue = false;
		String currentValue = "";
		for (int i = 0; i < bytes.length; i++) {
			Character c = (char) bytes[i];
			if (c == '#' || c == '!' || c == '*' || c == '$' || c == '%' || i == (bytes.length - 1)) {
				if (currentMod == "") {
					//hasnt started yet
				} else {
					//put in the value that you received
					if (currentPrefix == '#') {
						itemStack = new ItemStack(Integer.parseInt(currentMod));
						itemMeta = itemStack.getItemMeta();
					} else if (currentPrefix == '!') {
						itemType = ItemType.valueOf(currentMod);
					} else if (currentPrefix == '*') {
						itemName = currentMod;
					} else if (currentPrefix == '$') {
						int modInt = Integer.parseInt(currentMod);
						String reference = itemType.getElements()[modInt];
						if (currentValue == "")
							itemLore.add(getMod(reference));
						else {
							int valueInt = Integer.parseInt(currentValue);
							itemLore.add(getElement(reference, valueInt));
						}
					} else if (currentPrefix == '%') {
						int modInt = Integer.parseInt(currentMod);
						String reference = itemType.getEndElements()[modInt];
						if (currentValue == "")
							itemLore.add(getMod(reference));
						else {
							int valueInt = Integer.parseInt(currentValue);
							itemLore.add(getElement(reference, valueInt));
						}
					}
				}
				currentPrefix = c;
				currentMod = "";
				currentValue = "";
				isValue = false;
			} else if (c == '/') {
				isValue = true;
			} else {
				if (isValue) {
					currentValue += c;
					continue;
				}
				currentMod += c;
			}
		}
		if (itemName != null)
			itemMeta.setDisplayName(itemName);
		itemMeta.setLore(itemLore);
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}
	*/

}
