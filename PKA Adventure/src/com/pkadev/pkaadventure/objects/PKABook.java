package com.pkadev.pkaadventure.objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class PKABook {

	List<PKAPage> pages = new ArrayList<PKAPage>();
	Player owner = null;
	
	public PKABook(Player player, List<PKAPage> pages) {
		owner = player;
		this.pages = pages;
	}
	
	public ItemStack getItemStack() {
		ItemStack itemStack = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
		List<String> bookLore = new ArrayList<String>();
		bookLore.add("§6Plugin Developed by:");
		bookLore.add("§7TwoPointDuck §6and §7Marcus_WD");
		bookLore.add("");
		bookLore.add("§6Special thanks to:");
		bookLore.add("§7trickyguy123 §6and the §7build team§6!");
		
		
		bookMeta.setAuthor("WoodySquarePants");
		bookMeta.setDisplayName("Personal Information");
		bookMeta.setLore(bookLore);
		for (PKAPage pkaPage : pages) {
			bookMeta.addPage(pkaPage.getPage());
		}
		
		itemStack.setItemMeta(bookMeta);
		return itemStack;
	}
	
}
