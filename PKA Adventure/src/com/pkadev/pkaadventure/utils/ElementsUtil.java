package com.pkadev.pkaadventure.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import com.pkadev.pkaadventure.Main;
import com.pkadev.pkaadventure.objects.InventoryWithType;
import com.pkadev.pkaadventure.objects.ItemType;
import com.pkadev.pkaadventure.objects.PKAQuest;
import com.pkadev.pkaadventure.processors.QuestProcessor;
import com.pkadev.pkaadventure.types.InventoryType;
import com.pkadev.pkaadventure.types.QuestCompletionType;

public class ElementsUtil {
	
	private static World world = Bukkit.getWorld(FileUtil.getStringValueFromConfig(FileUtil.getConfig(), "homeworld", "config.yml"));
	
	public static World getWorld() {
		return world;
	}
	
	public static void load() {
		loreElements = new HashMap<String, String>();
		itemElements = new HashMap<String, ItemStack>();
		itemTypeElements = new HashMap<String, ItemType>();
		nameElements = new HashMap<String, List<String>>();
		dropElements = new HashMap<String, List<String>>();
		inventoryElements = new HashMap<String, InventoryWithType>();
		pageElements = new HashMap<String, String>();
		locationDescriptionElements = new HashMap<String, String>();
		questNamesInBook = new HashMap<String, String>();
		
		abilityInventoryName = FileUtil.getStringValueFromConfig(FileUtil.getInventoryConfig(), "ability_inventory_name", "inventories.yml");
		selectionInventoryName = FileUtil.getStringValueFromConfig(FileUtil.getInventoryConfig(), "selection_inventory_name", "inventories.yml");
		playerGUIInventoryName = FileUtil.getStringValueFromConfig(FileUtil.getInventoryConfig(), "playergui_inventory_name", "inventories.yml");
		
		loreElements.put("", "");
		itemTypeElements.put("", new ItemType(null, null, 0));
		dropElements.put("default_drop", FileUtil.getStringListFromConfig(FileUtil.getDropConfig(), "default_drop", 
					     "drops.yml"));
	}
	
	/**
	 * used to store elements of the lores such as:
	 *  skillexp - §7Exp: §f
	 *  level - §7Level: §l
	 *  strength - §7Strength: §f
	 */
	private static HashMap<String, String> loreElements = null;
	
	public static String getLoreElementMod(String reference) {
		if (loreElements.containsKey(reference))
			return loreElements.get(reference);
		else {
			String configFileReference = "config.yml";
			if (reference.endsWith("damage"))
				reference = "damage";
			else if (reference.endsWith("price"))
				reference = "price";
			else if (reference.endsWith("worth"))
				reference = "worth";
			String element = FileUtil.getStringValueFromConfig(FileUtil.getConfig(), "Lore." + reference, configFileReference);
			setLoreElement(reference, element);
			return element;
		}
	}
	
	private static void setLoreElement(String reference, String element) {
		loreElements.put(reference, element);
	}

	/**
	 * mod + new value determined by mathutil
	 * @param reference
	 * @param level
	 * @return
	 */
	public static String getInitialLoreElement(String reference, int level) {
		String mod = getLoreElementMod(reference);
		if (mod == "")
			return "";
		if (reference.endsWith("divide"))
			return mod + getLoreElementValueDivide(reference, level);
		return mod + getLoreElementValue(reference, level);
	}

	/**
	 * mod + value
	 * @param reference
	 * @param value
	 * @return
	 */
	public static String getExistingLoreElement(String reference, int value) {
		String mod = getLoreElementMod(reference);
		if (mod == "")
			return "";
		if (reference.endsWith("blank")) {
			return mod;
		}
		return mod + value;
	}
	
	public static String getExistingLoreElement(String reference, String value) {
		String mod = getLoreElementMod(reference);
		if (mod == "")
			return "";
		if (reference.endsWith("blank")) {
			return mod;
		}
		return mod + value;
	}

	/**
	 * only used if you need something like: Strength: 5/10
	 * @param divideReference
	 * @param level
	 * @param value
	 * @return
	 */
	public static String getLoreElementValueDivide(String divideReference, int level) {
		int value = MathUtil.getValue(level, divideReference);
		return value + "/" + value;
	}
	
	public static String getLoreElementValue(String reference, int level) {
		if (reference.endsWith("blank")) {
			return "";
		} else if (reference.endsWith("level_fixed")) {
			return "" + level;
		} else {
			return "" + MathUtil.getValue(level, reference);
		}
	}
	
	public static List<String> getMultipleInitialLoreElements(List<String> references, int level) {
		List<String> elements = new ArrayList<String>();
		for (String reference : references) {
			elements.add(getInitialLoreElement(reference, level));
		}
		return elements;
	}

	public static List<String> getMultipleExistingLoreElements(List<String> references, int[] values) {
		List<String> elements = 		new ArrayList<String>();
		if (references == null || values == null)
			return elements;
		
		int valuesIndex = 0;
		for (int i = 0 ; i < references.size(); i++) {
			if (!references.get(i).endsWith("blank") && !references.get(i).equals("")) {
				elements.add(getExistingLoreElement(references.get(i), values[valuesIndex]));
				valuesIndex += 1;
			} else {
				elements.add(ElementsUtil.getInitialLoreElement(references.get(i), -1));
			}
		}
		return elements;
	}
	
	public static List<String> getMultipleExistingLoreElements(ItemType itemType, String[] values) {
		List<String> elements = 		itemType.getElements();
		List<String> endElements = 		itemType.getEndElements();
		List<String> lore = 			new ArrayList<String>();
		
		if (elements == null || values == null)
			return lore;
		int valuesIndex = 0;
		
		for (String reference : elements) {
			if (reference.endsWith("blank")) {
				lore.add(getInitialLoreElement(reference, -1));
			} else {
				lore.add(getExistingLoreElement(reference, values[valuesIndex]));
				valuesIndex += 1;
			}
		}
		
		if (valuesIndex != values.length) {
			for (int i = valuesIndex - 1; i < values.length; i++) {
				String[] splitValue = values[i].split(":");
				lore.add(ElementsUtil.getExistingLoreElement(endElements.get(Integer.parseInt(splitValue[0])), splitValue[1]));
			}
		}
		
		return lore;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private static HashMap<String, ItemStack> itemElements = null;
	
	/**
	 * only returns the itemStack, not with itemMeta
	 * @param reference
	 * @return
	 */
	public static ItemStack getItemElement(String reference) {
		if (reference.equals(""))
			return new ItemStack(Material.AIR);
		if (itemElements.containsKey(reference))
			return itemElements.get(reference);
		else {
			String configFileReference = "itemtypes.yml";
			int id = 1;
			YamlConfiguration itemTypeConfig = FileUtil.getItemTypeConfig();
			if (itemTypeConfig.contains(reference + ".id")) {
				if (itemTypeConfig.isInt(reference + ".id"))
					id = FileUtil.getIntValueFromConfig(itemTypeConfig, reference + ".id", configFileReference);
				else {
					return new ItemStack(Material.STONE);
				}
			} else {
				return new ItemStack(Material.STONE);
			}
			@SuppressWarnings("deprecation")
			ItemStack element = new ItemStack(id);
			setItemElement(reference, element);
			return element;
		}
	}
	
	private static void setItemElement(String reference, ItemStack element) {
		itemElements.put(reference, element);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * itemType stores things like elements and endElements, this all is used when putting things into an items lore
	 */
	private static HashMap<String, ItemType> itemTypeElements = null;
	
	public static ItemType getItemTypeElement(String reference) {
		if (itemTypeElements.containsKey(reference))
			return itemTypeElements.get(reference);
		else {
			String configFileReference = "itemtypes.yml";
			YamlConfiguration config = FileUtil.getItemTypeConfig();

			ItemType element = new ItemType(FileUtil.getStringListFromConfig(config, reference + ".elements", configFileReference), 
					FileUtil.getStringListFromConfig(config, reference + ".endelements", configFileReference), 
					FileUtil.getIntValueFromConfig(config, reference + ".maxendelements", configFileReference));
			
			setItemTypeElement(reference, element);
			return element;
		}
	}
	
	private static void setItemTypeElement(String reference, ItemType element) {
		itemTypeElements.put(reference, element);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private static HashMap<String, List<String>> nameElements = null;
	
	public static List<String> getNameElement(String reference) {
		if (nameElements.containsKey(reference))
			return nameElements.get(reference);
		else {
			String configFileReference = "names.yml";
			List<String> element = FileUtil.getStringListFromConfig(FileUtil.getNameConfig(), reference, configFileReference);
			setNameElement(reference, element);
			return element;
		}
	}
	
	private static void setNameElement(String reference, List<String> element) {
		nameElements.put(reference, element);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * you can add in drops that drop for mobs with certain names
	 *  if it cant find a certain value, it will try fetching it, if it cant find it, it will just return the element with default_drop as reference
	 */
	private static HashMap<String, List<String>> dropElements = null;
	
	public static List<String> getDropElement(String reference) {
		if (dropElements.containsKey(reference))
			return dropElements.get(reference);
		else {
			String configFileReference = "drops.yml";
			List<String> element = FileUtil.getStringListFromConfigSAFE(FileUtil.getDropConfig(), reference, configFileReference);
			if (element == null)
				return dropElements.get("default_drop");
			setDropElement(reference, element);
			return element;
		}
	}
	
	private static void setDropElement(String reference, List<String> element) {
		dropElements.put(reference, element);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private static HashMap<String, InventoryWithType> inventoryElements = null;
	private static String abilityInventoryName = null;
	private static String selectionInventoryName = null;
	private static String playerGUIInventoryName = null;
	
	public static InventoryWithType getInventoryElement(String reference, int level) {
		if (reference.equals("ability"))
			return new InventoryWithType(Bukkit.createInventory(null, 9, abilityInventoryName), InventoryType.ABILITY);
		if (inventoryElements.containsKey(reference))
			return inventoryElements.get(reference);
		else {
			InventoryType inventoryType = 	InventoryType.valueOf(FileUtil.getStringValueFromConfig(FileUtil.getInventoryConfig(), reference + ".inventorytype", "inventories.yml").toUpperCase());
			InventoryWithType element = 	new InventoryWithType(InventoryUtil.getInitialInventory(reference, inventoryType, level), inventoryType);
			setInventoryElement(reference, element);
			return element;
		}
	}
	
	private static void setInventoryElement(String reference, InventoryWithType element) {
		inventoryElements.put(reference, element);
	}
	
	public static String getSelectionInventoryName() {
		return selectionInventoryName;
	}
	
	public static String getAbilityInventoryName() {
		return abilityInventoryName;
	}
	
	public static String getPlayerGUIInventoryName() {
		return playerGUIInventoryName;
	}
	
	public static void removeInventoryElement(String reference) {
		if (inventoryElements.containsKey(reference))
			inventoryElements.remove(reference);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private static HashMap<String, String> pageElements = null;
	private static HashMap<String, String> locationDescriptionElements = null;

	public static String getPageElement(String reference) {
		if (pageElements.containsKey(reference))
			return pageElements.get(reference);
		else {
			String element = FileUtil.getStringValueFromConfig(FileUtil.getPageConfig(), "Pages." + reference, "pages.yml");
			element = element.replace('/', '\n');
			setPageElement(reference, element);
			return element;
		}
	}

	private static void setPageElement(String reference, String element) {
		pageElements.put(reference, element);
	}
	
	public static String getLocationDescriptionElement(String reference) {
		if (locationDescriptionElements.containsKey(reference))
			return locationDescriptionElements.get(reference);
		else {
			String element = FileUtil.getStringFromConfigSAFE(FileUtil.getSpawnNodeConfig(), "Beacons." + reference + ".Description", "spawnnodes.yml");
			setLocationDescriptionElement(reference, element);
			return element;
		}
	}
	
	private static void setLocationDescriptionElement(String reference, String element) {
		locationDescriptionElements.put(reference, element);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	private static HashMap<String, String> questNamesInBook = null;
	
	public static String getQuestNameInBookElement(String questReference) {
		if (questNamesInBook.containsKey(questReference))
			return questNamesInBook.get(questReference);
		else {
			String element = QuestProcessor.getQuestSection(questReference).getString("name_in_book");
			if (element == null) {
				MessageUtil.log("quest " + questReference + " is missing name_in_book.");
				return "QUEST IS BROKEN: " + questReference;
			}
			questNamesInBook.put(questReference, element);
			return element;
		}
	}
	
	public static PKAQuest getQuestElement(String questReference, String playerName) {
		PKAQuest pkaQuest = Main.instance.getDatabase().find(PKAQuest.class).where().ieq("playerName", playerName).eq("questReference", questReference).findUnique();
		if (pkaQuest == null) {
			ConfigurationSection questSection = QuestProcessor.getQuestSection(questReference);
			int level = questSection.getInt("level");
			String questCompletionTypeString = questSection.getString("completion-type");
			if (questCompletionTypeString == null) {
				MessageUtil.log("quest " + questReference + " is missing the initial completion-type.");
				return null;
			}
			QuestCompletionType questCompletionType = QuestCompletionType.valueOf(questCompletionTypeString);
			pkaQuest = new PKAQuest(questReference, playerName, level, questCompletionType);
			Main.instance.getDatabase().save(pkaQuest);
			pkaQuest = Main.instance.getDatabase().find(PKAQuest.class).where().ieq("playerName", playerName).eq("questReference", questReference).findUnique();
		}
		return pkaQuest;
	}
	
}
