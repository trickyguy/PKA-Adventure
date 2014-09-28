package com.pkadev.pkaadventure.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.pkadev.pkaadventure.Main;
import com.pkadev.pkaadventure.interfaces.Ability;
import com.pkadev.pkaadventure.objects.ItemType;
import com.pkadev.pkaadventure.objects.PKAPlayer;
import com.pkadev.pkaadventure.processors.PlayerProcessor;
import com.pkadev.pkaadventure.types.AbilityType;
import com.pkadev.pkaadventure.types.ClassType;
import com.pkadev.pkaadventure.types.MessageType;


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

	public static void removePlayersDroppedItems(String playerName) {
		List<Item> toBeRemoved = new ArrayList<Item>();
		for (Item item : droppedItems.keySet()) {
			if (droppedItems.get(item).equals(playerName))
				toBeRemoved.add(item);
		}
		for (Item item : toBeRemoved) {
			removeDroppedItem(item);
		}
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

	public static String getReferenceFromItemId(int id) {
		ConfigurationSection section = FileUtil.getItemTypeConfig().getConfigurationSection("id_references");
		if (id > 297 && id < 318)
			return "armor:" + id;
		else if (id == 268 || id == 261 || id == 286)
			return "";
		else {
			try {
				return section.getString("" + id);
			} catch(NullPointerException ex) {
				ex.printStackTrace();
			}
		}
		return "";
	}

	public static int getRarityFromName(String itemName) {
		if (itemName.contains("§e"))
			return 2;
		else if (itemName.contains("§b"))
			return 3;
		return 1;
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

	public static boolean isGold(ItemStack itemStack) {
		Material material = itemStack.getType();
		if (material == Material.GOLD_NUGGET || material == Material.GOLD_INGOT ||
				material == Material.GOLD_BLOCK)
			return true;
		return false;
	}

	public static int getGoldWorth(ItemStack itemStack) {
		int amount = itemStack.getAmount();
		int worth = 1;
		if (itemStack.getType() == Material.GOLD_INGOT)
			worth = 10;
		else if (itemStack.getType() == Material.GOLD_BLOCK)
			worth = 100;
		return amount * worth;
	}

	public static boolean isWeapon(ItemStack itemStack) {
		if (itemStack != null) {
			if (!isAttributeItem(itemStack))
				return false;

			if(!itemStack.getItemMeta().hasDisplayName())
				return false;
			
			String name = itemStack.getItemMeta().getDisplayName();
			name = ChatColor.stripColor(name);
			if (name.startsWith("Woody's") || name.startsWith("Kyle's") || name.startsWith("Wings's") || name.startsWith("Lefty's"))
				return true;
		} return false;
	}

	public static boolean isAbilityItem(ItemStack itemStack) {
		if (!isAttributeItem(itemStack))
			return false;
		List<String> possibleIds = FileUtil.getStringListFromConfig(FileUtil.getItemTypeConfig(), "ability_id.id", "itemtypes.yml");
		if (possibleIds.contains(itemStack.getType().toString()))
			return true;
		return false;
	}

	public static boolean isStatItem(ItemStack itemStack) {
		if (itemStack.getType() == Material.WATCH)
			return true;
		if (!isAttributeItem(itemStack))
			return false;
		return false;
	}

	public static boolean isJournal(ItemStack itemStack) {
		return (itemStack.getType() == Material.WRITTEN_BOOK);
	}

	public static void giveWeapon(Player player, ClassType classType) {
		ItemStack weapon = ItemUtil.getInitialItem(classType.toString().toLowerCase() + "_weapon", player.getLevel(), 1);
		ItemUtil.updateWeaponLore(weapon, classType, player.getLevel());
		int actualWeaponSlot = InventoryUtil.getActualWeaponSlot(player);
		if (actualWeaponSlot != -1)
			InventoryUtil.removeItem(player, actualWeaponSlot);
		InventoryUtil.moveItemIntoInventory(player, weapon);
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
















	public static ClassType getClassTypeFromSelectionMenuItem(ItemStack itemStack) {
		String itemName = ChatColor.stripColor(itemStack.getItemMeta().getDisplayName());
		if (itemName.startsWith("Wo")) {
			return ClassType.WOODY;
		} else if (itemName.startsWith("L")) {
			return ClassType.LEFTY;
		} else if (itemName.startsWith("Wi")) {
			return ClassType.WINGS;
		}
		return ClassType.KYLE;
	}

	public static void updateWeaponLore(ItemStack itemStack, ClassType classType, int level) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		List<String> itemLore = new ArrayList<String>();
		switch(classType) {
		case WOODY: {
			itemMeta.setDisplayName("§6Woody's Sword");
			itemLore.add(ElementsUtil.getInitialLoreElement("woody_damage", level));
			break;
		}
		case WINGS: {
			itemMeta.setDisplayName("§6Wings's Sword");
			itemLore.add(ElementsUtil.getInitialLoreElement("wings_damage", level));
			break;
		}
		case LEFTY: {
			itemMeta.setDisplayName("§6Lefty's Sword");
			itemLore.add(ElementsUtil.getInitialLoreElement("lefty_damage", level));
			break;
		}
		case KYLE: {
			itemMeta.setDisplayName("§6Kyle's Sword");
			itemLore.add(ElementsUtil.getInitialLoreElement("kyle_damage", level));
			break;
		}
		default:return;
		}
	}

	private static int getFirstLineWithValue(List<String> itemLore) {
		for (int i = 0; i < itemLore.size(); i++) {
			String line = itemLore.get(i);
			if (Character.isDigit(line.charAt(line.length() - 1)))
				return i;
		}
		return 0;
	}

	@Deprecated
	protected static int[] getIntValuesFromItem(ItemStack itemStack, ItemType itemType) {
		return getIntValuesFromItem(itemStack.getItemMeta().getLore(), itemType);
	}

	@Deprecated
	protected static int[] getIntValuesFromItem(List<String> itemLore, ItemType itemType) {
		String[] stringValues = getStringValuesFromItem(itemLore, itemType);
		int[] values = new int[stringValues.length];
		for (int i = 0; i < values.length; i++)
			values[i] = Integer.parseInt(stringValues[i]);
		return values;
	}

	protected static String[] getStringValuesFromItem(ItemStack itemStack, ItemType itemType) {
		return getStringValuesFromItem(itemStack.getItemMeta().getLore(), itemType);
	}

	protected static String[] getStringValuesFromItem(List<String> itemLore, ItemType itemType) {
		return getStringValuesFromItem(itemLore, getFirstLineWithValue(itemLore), itemType);
	}

	private static String[] getStringValuesFromItem(List<String> itemLore, int startLine, ItemType itemType) {
		List<String> valuesList = new ArrayList<String>();

		boolean isEndElement = false;
		for (int i = startLine; i < itemLore.size(); i++) {
			String line = itemLore.get(i);
			if (line == "") {
				isEndElement = true;
				continue;
			}
			valuesList.add(getStringValueFromLine(line, isEndElement, itemType));
		}

		String[] values = new String[valuesList.size()];
		for (int i = 0; i < valuesList.size(); i++) {
			values[i] = valuesList.get(i);
		}

		return values;
	}

	private static String getStringValueFromLine(String line, boolean isEndElement, ItemType itemType) {
		byte[] bytes = stripAndGetBytes(line);

		String value = "";
		boolean isLastSpace = false;
		boolean isValue = false;

		for (int j = 1; j < bytes.length; j++) {
			Character c = (char) bytes[j];
			if (c == ':') {
				isLastSpace = true;
				continue;
			}
			if (c == ' ') {
				if (isLastSpace) {
					if (isEndElement) {
						for (int k = 0; k < itemType.getEndElements().size(); k++) {
							String endElement = ChatColor.stripColor(ElementsUtil.getLoreElementMod(itemType.getEndElements().get(k)));
							if (ChatColor.stripColor(line).startsWith(endElement)) {
								value += k + ":";
								break;
							}
						}
					}
					isValue = true;
				}
				continue;
			}
			if (isValue)
				value += c;
		}

		return value;
	}

	protected static int[] getArmorAttributesFromItem(ItemStack itemStack) {
		List<Integer> valuesList = new ArrayList<Integer>();
		for (int i = 0; i < 4; i++) {
			valuesList.add(0);
		}

		List<String> itemLore = itemStack.getItemMeta().getLore();

		for (int i = 5; i < itemLore.size(); i++) {
			String line = itemLore.get(i);
			byte[] bytes = stripAndGetBytes(line);

			//attributes[addto] = value;
			int addto = 1;
			//value in the form of String
			String value = "";
			//       j = 8 so we skip most of the word
			for (int j = 8; j < bytes.length; j++) {
				Character c = (char) bytes[j];
				if (c == ' ') {
					/*toughness is default, it's rewarded most*/
					if (j == 9/*strength*/) {
						addto = 0;
					} else if (j == 8/*agility*/) {
						addto = 2;
					} else if (j == 12/*restoration*/) {
						addto = 3;
					}
				} else {
					if (Character.isDigit(c)) {
						value += c;
					}
				}
			}
			valuesList.set(addto, Integer.valueOf(value));
		}

		int[] values = new int[4];
		for (int i = 0; i < valuesList.size(); i++) {
			values[i] = valuesList.get(i);
		}

		return values;
	}

	/**
	 * CAREFUL, THIS IS NOT RELATED TO GETTING THE ATTRIBUTES #MONEY
	 * @param itemStack
	 * @return
	 */
	protected static String[] getArmorValuesFromItem(ItemStack itemStack) {
		List<String> itemLore = itemStack.getItemMeta().getLore();
		int startLine = ItemUtil.getFirstLineWithValue(itemLore);
		List<String> valuesList = new ArrayList<String>();

		boolean isEndElements = false;
		for (int i = startLine; i < itemLore.size(); i++) {
			String line = itemLore.get(i);
			if (line == "") {
				isEndElements = true;
				continue;
			}
			byte[] bytes = stripAndGetBytes(line);

			String value = "";
			boolean isLastSpace = false;
			boolean isValue = false;

			for (int j = 1; j < bytes.length; j++) {
				Character c = (char) bytes[j];
				if (c == ':') {
					isLastSpace = true;
					continue;
				}
				if (c == ' ') {
					if (isLastSpace) {
						MessageUtil.d(j, "ItemUtil");
						isValue = true;
						if (isEndElements) {
							if (j == 9) {
								value += "0:";
							} else if (j == 10) {
								value += "1:";
							} else if (j == 8) {
								value += "2:";
							} else {
								value += "3:";
							}
						}
					}
					continue;
				}
				if (isValue)
					value += c;
			}
			valuesList.add(value);
		}

		String[] values = new String[valuesList.size()];
		for (int i = 0; i < valuesList.size(); i++) {
			values[i] = valuesList.get(i);
		}

		return values;
	}

	/**
	 * if an ability item has been moved into the ability inventory, or moved to a new slot
	 * @param slot
	 * @return
	 */
	public static Ability getAbilityFromItem(ItemStack itemStack, PKAPlayer pkaPlayer) {
		if (itemStack == null || itemStack.getType() == Material.AIR)
			return null;
		String itemName = ChatColor.stripColor(itemStack.getItemMeta().getDisplayName());
		itemName = itemName.replace(' ', '_');
		Ability ability = AbilityType.valueOf(itemName).getAbility();
		List<String> itemLore = itemStack.getItemMeta().getLore();
		int[] values = getIntValuesFromItem(itemLore, ElementsUtil.getItemTypeElement("ability"));
		int rarity = getRarityFromName(itemStack.getItemMeta().getDisplayName());
		ability.initialize(pkaPlayer, values, rarity);
		return ability;
	}

	public static ItemStack getItemFromAbility(Ability ability) {
		return getExistingItem(ability.getReference(), ability.getValues(), ability.getRarity());
	}

	public static HashMap<Integer, ItemStack> getItemStacksFromAbilities(HashMap<Integer, Ability> abilities) {
		HashMap<Integer, ItemStack> itemStacks = new HashMap<Integer, ItemStack>();
		for (Integer i : abilities.keySet()) {
			itemStacks.put(i, getItemFromAbility(abilities.get(i)));
		}
		return itemStacks;
	}







	public static boolean isArmorBroken(ItemStack itemStack) {
		if (getIntValueFromLore(itemStack.getItemMeta().getLore(), "Durability") < 6)
			return true;
		return false;
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
			boolean isModification = true;
			for (int i = 0; i < bytes.length; i++) {
				Character c = (char) bytes[i];
				if (isModification) {
					if (c == ':') {
						isModification = false;
						i += 1;
						continue;
					} else {
						mod += c;
					}
				} else {
					if (Character.isDigit(c)) {
						value += c;
					} else {
						break;
					}
				}
			}
			if (mod.equals(modification)) return value;
		}
		return "";
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

	public static int addValueInItemLore(List<String> itemLore, String referenceToBeReplaced, int value) {
		int newValue = -1;
		List<String> newItemLore = new ArrayList<String>();
		String modificationToBeReplaced = ElementsUtil.getLoreElementMod(referenceToBeReplaced);
		for (String line : itemLore) {
			if (line.startsWith(modificationToBeReplaced)) {
				int oldValue = getIntValueFromLore(itemLore, modificationToBeReplaced);
				newValue = oldValue + value;
				newItemLore.add(ElementsUtil.getExistingLoreElement(referenceToBeReplaced, newValue));
			} else {
				newItemLore.add(line);
			}
		}
		return newValue;
	}

	/**
	 * @param player
	 * @param pkaPlayer
	 * @param itemStack
	 * @return true if broken
	 */
	public static boolean damageArmor(Player player, PKAPlayer pkaPlayer, ItemStack itemStack) {
		if (!ItemUtil.isArmorItem(itemStack))
			return false;
		ItemMeta itemMeta = itemStack.getItemMeta();
		List<String> lore = itemMeta.getLore();
		List<String> newLore = new ArrayList<String>();
		int durability = 6;

		String modificationToBeReplaced = ElementsUtil.getLoreElementMod("durability_divide");
		for (int i = 0; i < lore.size(); i++) {
			String line = lore.get(i);
			if (line.contains("/")) {
				String values = getStringValueFromLine(line, false, null);
				String[] dividedValues = values.split("/");
				durability = Integer.parseInt(dividedValues[0]) - 5;
				line = modificationToBeReplaced + durability + "/" + dividedValues[1];
			}
			newLore.add(line);
		}
		itemMeta.setLore(newLore);
		itemStack.setItemMeta(itemMeta);
		if (durability <= 5)
			return true;
		return false;
	}




	public static void updateStatItemMeta(final Player player, final PKAPlayer pkaPlayer) {
		Bukkit.getScheduler().runTask(plugin, new Runnable() {

			@Override
			public void run() {
				ItemStack itemStack = InventoryUtil.getStatItem(player);
				ItemMeta itemMeta = itemStack.getItemMeta();
				updateStatItemMetaLore(itemMeta, pkaPlayer);
				updateStatItemMetaName(itemMeta, pkaPlayer);
				itemStack.setItemMeta(itemMeta);
			}

		});
	}

	private static void updateStatItemMetaLore(ItemMeta itemMeta, PKAPlayer pkaPlayer) {
		// new String[]{"stat_item_strength", "stat_item_toughness", "stat_item_agility", "stat_item_restoration"
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
			newName = "§r§6No §5available upgrade points!";
		else {
			newName = "§r§6" + availableUpgradePoints + " §5available upgrade points!";
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
			int rarity = 	1;

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
			} else {
				slot = random.nextInt(4);
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
		ItemStack itemStack = null;
		if (reference.equals("armor")) {
			itemStack = getInitialArmorItemStack(level, random.nextInt(4));
		} else {
			itemStack = ElementsUtil.getItemElement(reference);
		}
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
		ItemStack itemStack = 	getInitialArmorItemStack(level, slot);
		return getInitialItem(itemStack, "armor", level, rarity);
	}

	private static ItemStack getInitialArmorItemStack(int level, int slot) {
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
		armorContent[3] = getInitialArmorItemStack(level, 3);
		for (int i = 0; i < 3; i++) {
			if (random.nextBoolean()) {
				armorContent[i] = getInitialArmorItemStack(level, i);
			} else {
				armorContent[i] = new ItemStack(Material.AIR);
			}
		}
		return armorContent;
	}

	public static ItemStack getInitialStatItem(PKAPlayer pkaPlayer) {
		ItemStack statItem = new ItemStack(Material.ENDER_PEARL);
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

	@SuppressWarnings("deprecation")
	public static ItemStack getExistingItem(String reference, String[] values, String itemName) {
		String id = "-1";
		int indexOfDivide = 0;
		if (reference.contains(":")) {
			indexOfDivide = reference.indexOf(":");
			id = reference.substring(indexOfDivide + 1);
			reference = reference.substring(0, indexOfDivide);
		}
		int idInt = Integer.parseInt(id);
		ItemStack itemStack = null;
		if (idInt == -1)
			itemStack = ElementsUtil.getItemElement(reference);
		else {
			itemStack = new ItemStack(idInt);
		}
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(itemName);
		itemMeta.setLore(getExistingItemLore(reference, values));
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}




















	/**
	 * puts a new itemMeta onto the given itemStack depending on the kind of item, the level and the rarity
	 */
	private static List<String> getInitialItemLore(String reference, int level) {
		List<String> itemLore = new ArrayList<String>();
		ItemType itemType = ElementsUtil.getItemTypeElement(reference);
		int maxEndElements = itemType.getMaxEndElements();

		itemLore.addAll(ElementsUtil.getMultipleInitialLoreElements(itemType.getElements(), level));
		if (maxEndElements > 0) {
			List<String> endElements = itemType.getEndElements();
			List<String> finalEndElements = new ArrayList<String>();
			Collections.shuffle(endElements);

			maxEndElements = 1 + random.nextInt(maxEndElements);
			for (int i = 0; i < maxEndElements; i++) {
				String element = endElements.get(i);
				if (finalEndElements.contains(element))
					maxEndElements++;
				else {
					finalEndElements.add(element);
				}
			}
			itemLore.addAll(ElementsUtil.getMultipleInitialLoreElements(finalEndElements, level));
		}
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

	private static List<String> getExistingItemLore(String reference, String[] values) {
		List<String> itemLore = new ArrayList<String>();
		ItemType itemType = ElementsUtil.getItemTypeElement(reference);
		itemLore.addAll(ElementsUtil.getMultipleExistingLoreElements(itemType, values));
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
		String finalizedItemName = "";
		switch (rarity) {
		case 1: {
			finalizedItemName += "§7";
			break;
		}
		case 2: {
			finalizedItemName += "§e";
			break;
		}
		case 3: {
			finalizedItemName += "§b";
			break;
		}
		case 4: {
			finalizedItemName += "§d";
			break;
		}
		default:return "INVALID RARITY VALUE";
		}
		return finalizedItemName + itemName;
	}

}
