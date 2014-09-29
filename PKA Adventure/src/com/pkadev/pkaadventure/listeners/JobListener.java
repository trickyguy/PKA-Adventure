package com.pkadev.pkaadventure.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import com.pkadev.pkaadventure.objects.PKAPlayer;
import com.pkadev.pkaadventure.processors.PlayerProcessor;
import com.pkadev.pkaadventure.utils.SkillsUtil;

public class JobListener implements Listener {

	private static JobListener i;
	private JobListener() {}

	public static JobListener i() {
		if (i == null) i = new JobListener(); return i;
	}
	
	@EventHandler
	public void pickupItem(PlayerPickupItemEvent event) {
		Item item = event.getItem();
		ItemStack itemStack = item.getItemStack();

		if(SkillsUtil.isSkillItem(itemStack)) {
			Player player = event.getPlayer();
			PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(player);
			
			SkillsUtil.updateSkillItemWithStats(player, itemStack, pkaPlayer.getMiningLevel(), pkaPlayer.getMiningExp());
		}
	}

	@EventHandler
	public void dropItem(PlayerDropItemEvent event) {
		Item item = event.getItemDrop();
		ItemStack itemStack = item.getItemStack();

		if(SkillsUtil.isSkillItem(itemStack)) {
			Player player = event.getPlayer();
			SkillsUtil.updateSkillItemWithStats(player, itemStack, 1, 0);
		}
	}
	
	@EventHandler
	public void oreBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		if(block.getType().toString().endsWith("_ORE")) {
			event.setCancelled(true);
			SkillsUtil.createBrokenOre(block, block.getType(), 5);
			
		}
	}
}
