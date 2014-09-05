package com.pkadev.pkaadventure.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.pkadev.pkaadventure.Main;
import com.pkadev.pkaadventure.objects.ItemType;
import com.pkadev.pkaadventure.types.InventoryType;

public class ElementsUtil {
	private static Main plugin = Main.instance;
	
	public static void load() {
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
	private static HashMap<String, String> loreElements = new HashMap<String, String>();
	
	public static String getLoreElementMod(String reference) {
		if (loreElements.containsKey(reference))
			return loreElements.get(reference);
		else {
			String configFileReference = "config.yml";
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
			return mod + getLoreElementValue(reference, level, 0);
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

	/**
	 * only used if you need something like: Strength: 5/10
	 * @param divideReference
	 * @param level
	 * @param value
	 * @return
	 */
	public static String getLoreElementValue(String divideReference, int level, int value) {
		return value + "/" + MathUtil.getValue(level, divideReference);
	}
	
	public static String getLoreElementValue(String reference, int level) {
		if (reference.endsWith("blank")) {
			return "";
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

	/**
	 * used in the cases where you wanna join up a bunch of values with presets
	 *  this is used in the StatItem and the AbilityItems
	 * @param references: "stat_item_strength", "stat_item_agility", "ability_lightning_numberofbolts"
	 * @param values: "1, 4, 6, 3"
	 * @return
	 */
	public static List<String> getMultipleExistingLoreElements(List<String> references, int[] values) {
		List<String> elements = new ArrayList<String>();
		if (references == null || values == null || references.size() > values.length || references.size() < values.length)
			return elements;
		for (int i = 0 ; i < references.size(); i++) {
			elements.add(getExistingLoreElement(references.get(i), values[i]));
		}
		return elements;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private static HashMap<String, ItemStack> itemElements = new HashMap<String, ItemStack>();
	
	/**
	 * only returns the itemStack, not with itemMeta
	 * @param reference
	 * @return
	 */
	public static ItemStack getItemElement(String reference) {
		if (reference == "")
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
	private static HashMap<String, ItemType> itemTypeElements = new HashMap<String, ItemType>();
	
	public static ItemType getItemTypeElement(String reference) {
		if (itemTypeElements.containsKey(reference))
			return itemTypeElements.get(reference);
		else {
			String configFileReference = "itemtypes.yml";
			YamlConfiguration config = FileUtil.getItemTypeConfig();
			ItemType element = new ItemType(	FileUtil.getStringListFromConfig(config, reference + ".elements", configFileReference), 
												FileUtil.getStringListFromConfig(config, reference + ".endelements", configFileReference),
												FileUtil.getIntValueFromConfig(config, reference + ".maxendelements", configFileReference));
			setItemTypeElement(reference, element);
			return element;
		}
	}
	
	private static void setItemTypeElement(String reference, ItemType element) {
		itemTypeElements.put(reference, element);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private static HashMap<String, List<String>> nameElements = new HashMap<String, List<String>>();
	
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
	private static HashMap<String, List<String>> dropElements = new HashMap<String, List<String>>();
	
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private static HashMap<String, Inventory> inventoryElements = new HashMap<String, Inventory>();
	private static HashMap<String, InventoryType> inventoryNameElements = new HashMap<String, InventoryType>();
	private static String abilityInventoryName = FileUtil.getStringValueFromConfig(FileUtil.getInventoryConfig(), "ability_inventory_name", "inventories.yml");
	private static String selectionInventoryName = FileUtil.getStringValueFromConfig(FileUtil.getInventoryConfig(), "selection_inventory_name", "inventories.yml");
	
	public static Inventory getInventoryElement(String reference, int level) {
		if (reference.equals("ability"))
			return Bukkit.createInventory(null, 9, abilityInventoryName);
		if (inventoryElements.containsKey(reference))
			return inventoryElements.get(reference);
		else {
			InventoryType inventoryType = 	InventoryType.valueOf(FileUtil.getStringValueFromConfig(FileUtil.getInventoryConfig(), reference + ".inventorytype", "inventories.yml").toUpperCase());
			Inventory element = 			InventoryUtil.getInitialInventory(reference, inventoryType, level);
			String nameReference = 			element.getName();
			setInventoryElement(reference, element);
			setInventoryNameElement(nameReference, inventoryType);
			return element;
		}
	}
	
	public static InventoryType getInventoryTypeElement(String nameReference) {
		if (inventoryNameElements.containsKey(nameReference))
			return inventoryNameElements.get(nameReference);
		return InventoryType.NONE;
	}
	
	private static void setInventoryElement(String reference, Inventory element) {
		inventoryElements.put(reference, element);
	}
	
	private static void setInventoryNameElement(String nameReference, InventoryType inventoryType) {
		inventoryNameElements.put(nameReference, inventoryType);
	}
	
	public static String getSelectionInventoryName() {
		return selectionInventoryName;
	}
	
	public static String getAbilityInventoryName() {
		return abilityInventoryName;
	}
	
}
