package com.pkadev.pkaadventure.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.pkadev.pkaadventure.objects.ItemType;
import com.pkadev.pkaadventure.objects.PKAPlayer;

public class SkillsUtil {

	public static void updateSkillItemWithStats(Player player, PKAPlayer pkaPlayer, ItemStack itemStack) {
		if(isSkillItem(itemStack)) {
			int level = 1; // pkaPlayer.getMiningLevel();
			int exp = 20; // pkaPlayer.getMiningExp();
			
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
			
			itemMeta.setLore(newLore);
			itemStack.setItemMeta(itemMeta);
		}
	}

	public static boolean isSkillItem(ItemStack itemStack) {
		Material type = itemStack.getType();
		if (type.equals(Material.WOOD_PICKAXE) || type.equals(Material.WOOD_AXE))
			return true;
		else
			return false;
	}
	
	public static int getMaxExpFromLevel(int level) {
		int maxExp = (level + 3) * 8 * 4 * level + 128;
		return maxExp;
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
