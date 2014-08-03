package com.pkadev.pkaadventure.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import com.pkadev.pkaadventure.Main;
import com.pkadev.pkaadventure.objects.ItemType;

public class ElementsUtil {
	private static Main plugin = Main.instance;
	
	public static void load() {
		itemTypeElements.put("", new ItemType(null, null, 0));
		dropElements.put("default_drop", FileUtil.getStringArrayFromConfig(FileUtil.getDropConfig(), "default_drop", 
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
		if (reference == "")
			return "";
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
		return mod + value;
	}

	public static String getLoreElementValue(String reference, int level) {
		return "" + MathUtil.getValue(level, reference);
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
			String configFileReference = "config.yml";
			ItemStack element = new ItemStack(FileUtil.getIntValueFromConfig(FileUtil.getConfig(), "ItemStack." + reference, configFileReference));
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
			ItemType element = new ItemType(	FileUtil.getStringArrayFromConfig(config, reference + "elements", configFileReference), 
												FileUtil.getStringArrayFromConfig(config, reference + "endelements", configFileReference),
												FileUtil.getIntValueFromConfig(config, reference + "maxendelements", configFileReference));
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
			List<String> element = FileUtil.getStringArrayFromConfig(FileUtil.getNameConfig(), reference, configFileReference);
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
			List<String> element = FileUtil.getStringArrayFromConfigSAFE(FileUtil.getDropConfig(), reference, configFileReference);
			if (element == null)
				return dropElements.get("default_drop");
			setDropElement(reference, element);
			return element;
		}
	}
	
	private static void setDropElement(String reference, List<String> element) {
		dropElements.put(reference, element);
	}
	
}
