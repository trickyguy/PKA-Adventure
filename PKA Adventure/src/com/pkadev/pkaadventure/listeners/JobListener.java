package com.pkadev.pkaadventure.listeners;

import com.pkadev.pkaadventure.objects.PKAPlayer;
import com.pkadev.pkaadventure.processors.PlayerProcessor;
import com.pkadev.pkaadventure.types.MessageType;
import com.pkadev.pkaadventure.utils.MessageUtil;
import com.pkadev.pkaadventure.utils.SkillsUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

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

	/*
	 * Slow mining, PlayerInteractEvent
	 */

	@EventHandler
	public void oreBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();

		Material material = block.getType();
		String oreMaterial = material.toString();

		if(oreMaterial.endsWith("_ORE")) {
			event.setCancelled(true);
			event.setExpToDrop(0);

			if(player.getItemInHand() == null) return;
			if(!player.getItemInHand().hasItemMeta()) return;

			ItemStack item = player.getItemInHand();
			String itemMaterial = item.getType().toString();

			if(itemMaterial.endsWith("_PICKAXE")) {

				if(SkillsUtil.pickaxe_values.isEmpty())
					Bukkit.broadcastMessage("empty");

				if(!SkillsUtil.pickaxe_values.containsKey(itemMaterial) || !SkillsUtil.ore_values.containsKey(oreMaterial)) return;

				int pick = SkillsUtil.pickaxe_values.get(itemMaterial).intValue();
				int ore = SkillsUtil.ore_values.get(oreMaterial).intValue();

				String oreName = SkillsUtil.getOreMaterialName(material);

				if(ore > pick) {
					MessageUtil.sendMessage(player, "§cYour " + ChatColor.stripColor(SkillsUtil.getSkillName(item.getType())) + " is not capable of mining " + ChatColor.stripColor(oreName) + ".", MessageType.SINGLE);
					return;
				} else {
					PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(player);

					int level = pkaPlayer.getMiningLevel(); // NULL IF A CLASS ISNT SELECTED.
					int exp = pkaPlayer.getMiningExp();

					SkillsUtil.createBrokenOre(block, material, SkillsUtil.getBlockCooldown(material));
					// SkillsUtil.getPickaxeMultipliers(item);

					if(SkillsUtil.checkRandomChance(SkillsUtil.defaultOreChance(material, level), 7)) {

						int oreExp = SkillsUtil.defaultOreExp(material);
						int maxExp = SkillsUtil.getMaxExpFromLevel(level);
						int totalExp = exp + oreExp;

						player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.4F);

						if(totalExp >= maxExp + 1) {
							int remainder = totalExp - maxExp;
							int newMaxExp = SkillsUtil.getMaxExpFromLevel(level + 1);

							pkaPlayer.setMiningLevel(level + 1);
							pkaPlayer.setMiningExp(remainder);

							MessageUtil.sendMessage(player, MessageUtil.centerText("§e+" + oreExp + " §lEXP " + "§7[" + remainder + "§l/§7" + newMaxExp + "]"), MessageType.SINGLE);
							MessageUtil.sendMessage(player, MessageUtil.centerText("§e§lLEVEL UP! §e" + level + " §l-> §e" + (level + 1)), MessageType.SINGLE);
							
							if(SkillsUtil.checkRandomChance(level, 20))
								SkillsUtil.setNewEnchantment(player, item);
							
							if(SkillsUtil.isUpgradable(level + 1)) {
								Material materialUpgrade = SkillsUtil.getItemUpgrade(item.getType());
								String upgradeName = SkillsUtil.getSkillName(materialUpgrade);

								SkillsUtil.upgradeSkillItem(player, item, materialUpgrade, upgradeName);
							} else {
								SkillsUtil.createFirework(player, Color.YELLOW, Color.ORANGE);
							}
						} else {
							MessageUtil.sendMessage(player, MessageUtil.centerText("§e+" + oreExp + " §lEXP " + "§7[" + totalExp + "§l/§7" + maxExp + "]"), MessageType.SINGLE);
							pkaPlayer.setMiningExp(totalExp);
						}

						SkillsUtil.updateSkillItemWithStats(player, player.getItemInHand(), pkaPlayer.getMiningLevel(), pkaPlayer.getMiningExp());

					} else {
						MessageUtil.sendMessage(player, "§7§oYou failed to retrieve any " + ChatColor.stripColor(oreName) + ".", MessageType.SINGLE);
						return;
					}
				}
			}
		}
	}
}
