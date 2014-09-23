package com.pkadev.pkaadventure.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.pkadev.pkaadventure.objects.ItemType;

public class SkillsUtil {

	//TODO When specifying level and exp, need to make a check for which skill information should be used.
	public static void updateSkillItemWithStats(Player player, ItemStack itemStack, int level, int exp) {
		if(isSkillItem(itemStack)) {

			ItemType itemType = ElementsUtil.getItemTypeElement("skill");
			ItemMeta itemMeta = itemStack.getItemMeta();

			List<String> lore = itemMeta.getLore();
			List<String> newLore = new ArrayList<String>();

			for (int i = 0; i < lore.size(); i++) {
				String line = lore.get(i);
				String editedLine = ChatColor.stripColor(lore.get(i));

				if(editedLine.startsWith("Level")) {
					String replacement = ElementsUtil.getLoreElementMod(itemType.getElements().get(i));
					line = replacement + level;
				} if(editedLine.startsWith("EXP")) {
					String replacement = ElementsUtil.getLoreElementMod(itemType.getElements().get(i));
					line = replacement + exp + "/" + getMaxExpFromLevel(level);
				}

				newLore.add(line);
			}
			
			itemStack.setType(getSkillMaterial(itemStack, level));
			
			itemMeta.setLore(newLore);
			itemMeta.setDisplayName(getSkillName(itemStack.getType()));
			itemStack.setItemMeta(itemMeta);
		}
	}

	public static int getMaxExpFromLevel(int level) {
		int maxExp = (level + 3) * 8 * 4 * level + 128;
		return maxExp;
	}

	public static boolean isSkillItem(ItemStack itemStack) {
		Material type = itemStack.getType();
		if (type.equals(Material.WOOD_PICKAXE) || type.equals(Material.STONE_PICKAXE) || type.equals(Material.IRON_PICKAXE) ||
				type.equals(Material.DIAMOND_PICKAXE) || type.equals(Material.WOOD_AXE) || type.equals(Material.STONE_AXE) ||
				type.equals(Material.IRON_AXE) || type.equals(Material.DIAMOND_AXE))
			return true;
		else
			return false;
	}
	
	//TODO Improve this shit, make it more dynamic. Works for now though.
	
	public static Material getSkillMaterial(ItemStack itemStack, int level) {
		String materialName = itemStack.getType().toString();
		if(materialName.endsWith("PICKAXE")) {
			if(level >= 0 && level < 25) {
				return Material.WOOD_PICKAXE;
			} else if(level >= 25 && level < 50) {
				return Material.STONE_PICKAXE;
			} else if(level >= 50 && level < 75) {
				return Material.IRON_PICKAXE;
			} else if(level >= 75 && level < 100) {
				return Material.DIAMOND_PICKAXE;
			} else if(level == 100) {
				return Material.DIAMOND_PICKAXE;
			}
		}

		if(materialName.endsWith("AXE")) {
			if(level >= 0 && level < 25) {
				return Material.WOOD_AXE;
			} else if(level >= 25 && level < 50) {
				return Material.STONE_AXE;
			} else if(level >= 50 && level < 75) {
				return Material.IRON_AXE;
			} else if(level >= 75 && level < 100) {
				return Material.DIAMOND_AXE;
			} else if(level == 100) {
				return Material.DIAMOND_AXE;
			}
		}
		return null;
	}

	public static String getSkillName(Material material) {
		String materialName = material.toString();
		if(materialName.endsWith("PICKAXE")) {
			switch(material) {
			default: return null;
			case WOOD_PICKAXE:{
				return "§eBeginner Pickaxe";
			} case STONE_PICKAXE:{
				return "§aAmateur Pickaxe";
			} case IRON_PICKAXE:{
				return "§dElite Pickaxe";
			} case DIAMOND_PICKAXE:{
				return "§bMaster Pickaxe";
			}
			}
		}
		if(materialName.endsWith("AXE")) {
			switch(material) {
			default: return null;
			case WOOD_AXE:{
				return "§eBeginner Axe";
			} case STONE_AXE:{
				return "§aAmateur Axe";
			} case IRON_AXE:{
				return "§dElite Axe";
			} case DIAMOND_AXE:{
				return "§bMaster Axe";
			}
			}
		}
		
		return "Error";
	}

	/* public static void getSkillItem(ItemStack item) {
		Material type = item.getType();
		switch(type) {
		case WOOD_PICKAXE: {
			itemMeta.setDisplayName("§6Woody's Sword");
			itemLore.add(ElementsUtil.getInitialLoreElement("woody_damage", level));
			break;
		}
		case WOOD_AXE: {
			itemMeta.setDisplayName("§6Wings's Sword");
			itemLore.add(ElementsUtil.getInitialLoreElement("wings_damage", level));
			break;
		}
		default:return;
		}
	} */
}
