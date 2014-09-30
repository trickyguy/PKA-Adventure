package com.pkadev.pkaadventure.listeners;

import org.bukkit.ChatColor;
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
		Player player = event.getPlayer();
		Block block = event.getBlock();
		Material material = block.getType();

		if(material.toString().endsWith("_ORE")) {
			event.setCancelled(true);
			event.setExpToDrop(0);
			
			if(player.getItemInHand().equals(null)) return;
			ItemStack item = player.getItemInHand();
			
			if(item.getType().toString().endsWith("_PICKAXE")) {
				int pick = ((Integer) SkillsUtil.pickaxe_values.get(item.getType().toString())).intValue();
				int ore = ((Integer) SkillsUtil.ore_values.get(material.toString())).intValue();
				
				String oreName = SkillsUtil.getOreMaterialName(material);
				
				if(ore > pick) {
					player.sendMessage("§cYour " + ChatColor.stripColor(SkillsUtil.getSkillName(item.getType())) + " is not capable of mining " + ChatColor.stripColor(oreName) + ".");
					return;
				} else {
					SkillsUtil.createBrokenOre(block, material, 5);
					PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(player);
					
					int level = pkaPlayer.getMiningLevel(); // NULL IF A CLASS ISNT SELECTED.
					int exp = pkaPlayer.getMiningExp();
					
					if(SkillsUtil.checkOreChance(SkillsUtil.defaultOreChance(material, level)) == true) {
						int oreExp = SkillsUtil.defaultOreExp(material);
						int maxExp = SkillsUtil.getMaxExpFromLevel(level);
						int totalExp = exp + oreExp;
						
						if(exp + oreExp >= maxExp + 1) {
							pkaPlayer.setMiningLevel(level += 1);
							pkaPlayer.setMiningExp(totalExp - maxExp);
						} else {
							pkaPlayer.setMiningExp(totalExp);
						}
						
						SkillsUtil.updateSkillItemWithStats(player, player.getItemInHand(), pkaPlayer.getMiningLevel(), pkaPlayer.getMiningExp());
						
					} else {
						player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "You failed to retrieve any " + ChatColor.stripColor(oreName) + ".");
						return;
					}
				}
			}
		}
	}
}

/*
 * getPickaxeMultipliers()
 * returns an array
 * 
 * [0, 1, 2, 3]
 * 0 would be the multipler for gold etc
 * array would be referenced when checking chances
 */





