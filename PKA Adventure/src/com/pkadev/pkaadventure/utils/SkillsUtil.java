package com.pkadev.pkaadventure.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.pkadev.pkaadventure.objects.BrokenOreBlock;
import com.pkadev.pkaadventure.objects.ItemType;
import com.pkadev.pkaadventure.threads.OreTimer;

public class SkillsUtil {

	public static void load() {
		setMiningValues();
	}
	
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
			
			itemStack.setType(getSkillMaterial(itemStack, level, getMaterialSuffix(itemStack.getType())));

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

	public static String getMaterialSuffix(Material material) {
		if(material.toString().endsWith("_PICKAXE"))
			return "_PICKAXE";
		else if(material.toString().endsWith("_AXE"))
			return "_AXE";
		return null;
	}
	
	//TODO Improve this shit, make it more dynamic. Works for now though.

	public static Material getSkillMaterial(ItemStack itemStack, int level, String suffix) {
		if(level >= 0 && level < 25) {
			return Material.getMaterial("WOOD" + suffix);
		} else if(level >= 25 && level < 50) {
			return Material.getMaterial("STONE" + suffix);
		} else if(level >= 50 && level < 75) {
			return Material.getMaterial("IRON" + suffix);
		} else if(level >= 75 && level < 100) {
			return Material.getMaterial("DIAMOND" + suffix);
		} else if(level == 100) {
			return Material.getMaterial("DIAMOND" + suffix);
		}
		return null;
	}

	public static String getSkillName(Material material) {
		String materialName = material.toString();
		if(materialName.endsWith("_PICKAXE")) {
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
		if(materialName.endsWith("_AXE")) {
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

	public static void createBrokenOre(Block block, Material material, int time) {
		new BrokenOreBlock(block, material, time);
		block.setType(Material.STONE);
		
		if(!OreTimer.isRunning())
			OreTimer.start();
	}

	public static void removeBrokenOreBlock(BrokenOreBlock oreBlock) {
		Block block = oreBlock.getOreBlock();
		block.setType(oreBlock.getMaterial());
		
		BrokenOreBlock.getAllBlocks().remove(oreBlock);
		
		if(BrokenOreBlock.getAllBlocks().size() == 0)
			OreTimer.stop();
	}
	
	//TODO fix this hardcoded shit
	
	public static Map<String, Integer> ore_values = new HashMap<>();
	public static Map<String, Integer> pickaxe_values = new HashMap<>();

	public static void setMiningValues() {
		ore_values.put("COAL_ORE", Integer.valueOf(1));
		ore_values.put("LAPIS_ORE", Integer.valueOf(2));
		ore_values.put("IRON_ORE", Integer.valueOf(3));
		ore_values.put("GOLD_ORE", Integer.valueOf(4));
		ore_values.put("DIAMOND_ORE", Integer.valueOf(5));
		ore_values.put("EMERALD_ORE", Integer.valueOf(6));
		
		pickaxe_values.put("WOODEN_PICKAXE", Integer.valueOf(1));
		pickaxe_values.put("STONE_PICKAXE", Integer.valueOf(2));
		pickaxe_values.put("IRON_PICKAXE", Integer.valueOf(4));
		pickaxe_values.put("DIAMOND_ORE", Integer.valueOf(6));
	}
}
