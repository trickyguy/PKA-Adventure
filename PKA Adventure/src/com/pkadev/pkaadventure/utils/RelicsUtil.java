package com.pkadev.pkaadventure.utils;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.pkadev.pkaadventure.Main;
import com.pkadev.pkaadventure.objects.PKAPlayer;

public class RelicsUtil {
	private static Main plugin = null;
	
	private static ItemStack nextPageItem = null;
	private static HashMap<Integer, List<String>> relicReferencesByPage = null;
	
	public static void load(Main instance) {
		plugin = instance;
		
		nextPageItem = ItemUtil.getInitialItem("relics_next_page", 1, 1);
		YamlConfiguration relicConfig = FileUtil.getRelicConfig();
		for (String section : relicConfig.getKeys(false)) {
			Integer pageNumber = null;
			try {
				pageNumber = Integer.parseInt(section);
			} catch (NumberFormatException ex) {
				continue;
			}
			
			relicReferencesByPage.put(pageNumber, relicConfig.getStringList(section));
		}
	}
	
	public static void openRelicsInventory(final Player player, final PKAPlayer pkaPlayer, final int page) {
		Bukkit.getScheduler().runTask(plugin, new Runnable() {

			@Override
			public void run() {
				openRelicsInventoryDelayed(player, pkaPlayer, page);
			}
			
		});
	}
	
	private static void openRelicsInventoryDelayed(Player player, PKAPlayer pkaPlayer, int page) {
		if (player == null || pkaPlayer == null)
			return;
		if (!relicReferencesByPage.containsKey(Integer.valueOf(page)))
			return;
			
		
		Inventory inventory = ElementsUtil.getInventoryElement("relics", -1).getInventory();
		List<String> relicReferences = relicReferencesByPage.get(Integer.valueOf(page));
		for (String relicReference : relicReferences) {
			inventory.addItem(ItemUtil.getInitialItem(relicReference, -1, 1));
		}
		if (!relicReferencesByPage.containsKey(Integer.valueOf(page + 1)))
			inventory.setItem(8, nextPageItem);
	}
	
}
