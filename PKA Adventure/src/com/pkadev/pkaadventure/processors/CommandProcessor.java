package com.pkadev.pkaadventure.processors;

import java.util.Random;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pkadev.pkaadventure.objects.PKAPlayer;
import com.pkadev.pkaadventure.types.MessageType;
import com.pkadev.pkaadventure.utils.BookUtil;
import com.pkadev.pkaadventure.utils.InventoryUtil;
import com.pkadev.pkaadventure.utils.ItemUtil;
import com.pkadev.pkaadventure.utils.MessageUtil;
import com.pkadev.pkaadventure.utils.SidebarUtil;
import com.pkadev.pkaadventure.utils.SkillsUtil;
import com.pkadev.pkaadventure.utils.TeamUtil;

public class CommandProcessor implements CommandExecutor {

	private static CommandProcessor i; private CommandProcessor(){} public static CommandProcessor i() {if (i == null)i = new CommandProcessor();return i;}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLbl, String[] args) {
		int argsLength = args.length;

		if (!(sender instanceof Player))
			return false;
		Player player = (Player) sender;

		if (cmd.getName().equalsIgnoreCase("pka")) {
			if (argsLength == 0) {
				InventoryUtil.openInventory(player, -1, "selection");
				
				// Marcus testing
				PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(player);
				if (pkaPlayer == null)
					return true;
				pkaPlayer.setMiningLevel(24);
				pkaPlayer.setMiningExp(SkillsUtil.getMaxExpFromLevel(24) - 500);
				
			} else if (argsLength == 1) {
				if (args[0].equalsIgnoreCase("create")) {
					MessageUtil.sendMessage(player, "/pka create fileName mobName radius level amount mob strength stance type", MessageType.SINGLE);
				} else if (args[0].equalsIgnoreCase("ability")) {
					InventoryUtil.openInventory(player, -1, "ability");
				} else if (args[0].equalsIgnoreCase("test")) {
					TEST(player);
				} else if (args[0].equalsIgnoreCase("compress")) {
					InventoryUtil.saveInventory(player.getInventory(), "PlayerInventory", player.getName());
					InventoryUtil.saveInventory(PlayerProcessor.getPKAPlayer(player).getAbilityInventory(), "Ability", player.getName());
				} else if (args[0].equalsIgnoreCase("leave")) {
					MessageUtil.sendMessage(player, "You have been removed from gameplay.", MessageType.SINGLE);
					PlayerProcessor.unloadPlayerForever(player);
				} else {
					invalidCommand(player);
				}
			} else if (argsLength == 2) {
				if (args[0].equalsIgnoreCase("ability")) {
					try {
						int abilityTriggerType = Integer.parseInt(args[1]);
						PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(player);
						if (pkaPlayer == null)
							return true;
						pkaPlayer.setAbilitySelectionType(abilityTriggerType);
					} catch (IllegalArgumentException ex) {
						MessageUtil.sendMessage(player, "usage: /pka ability triggerType", MessageType.SINGLE);
					}
				} else if (args[0].equalsIgnoreCase("item")) {
					InventoryUtil.moveItemIntoInventory(player, ItemUtil.getInitialItem(args[1], player.getLevel(), 1));
				} else {
					invalidCommand(player);
				}
			} else if (argsLength == 3) {
				if (args[0].equalsIgnoreCase("create")) {
					//pka create lootcrate fileName
					if (args[1].equalsIgnoreCase("lootcrate")) {
						newSpawnNode(player, null, args[2]);
					} else {
						invalidCommand(player);
					}
				} else {
					invalidCommand(player);
				}
			} else if (argsLength == 4) {
				//pka create beacon filename name
				if (args[1].equalsIgnoreCase("beacon")) {
					String name = args[3].replace('_', ' ');
					newSpawnNode(player, name, args[2]);
				} else {
					invalidCommand(player);
				}
			} else if (argsLength == 9) {
				newSpawnNode(player, args);
			} else {
				invalidCommand(player);
			}
		} else if (cmd.getName().equalsIgnoreCase("team")) {
			teamCommand(player, args);
		}

		return true;
	}
	
	private static void teamCommand(Player sender, String args[]) {
		String playerName = sender.getName();
		if (args.length == 0) {
			TeamUtil.teamInfo(playerName);
		} else if (args.length == 1) {
			if (args[0].equals("help")) {
				MessageUtil.sendMessage(sender, "/team <info/invite/uninvite/leave/create/join/kick/promote/demote/disband> <player/team>", MessageType.SINGLE);
			} else if (args[0].equals("info")) {
				TeamUtil.teamInfo(playerName, args[1]);
			} else if (args[0].equals("leave")) {
				TeamUtil.leaveTeam(playerName);
			} else if (args[0].equals("disband")) {
				TeamUtil.disband(playerName);
			} else {
				invalidCommand(sender);
			}
		} else if (args.length == 2) {
			if (args[0].equals("join")) {
				TeamUtil.acceptInvite(playerName, args[1]);
			} else if (args[0].equals("kick")) {
				TeamUtil.kickFromTeam(args[1], playerName);
			} else if (args[0].equals("invite")) {
				TeamUtil.invite(args[1], playerName);
			} else if (args[0].equals("promote")) {
				TeamUtil.promote(args[1], playerName);
			} else if (args[0].equals("demote")) {
				TeamUtil.demote(args[1], playerName);
			} else if (args[0].equals("uninvite")) {
				TeamUtil.deinvited(args[1], playerName);
			} else if (args[0].equals("create")) {
				TeamUtil.create(playerName, args[1]);
			} else {
				invalidCommand(sender);
			}
		} else {
			invalidCommand(sender);
		}
	}
	
	
	private static void TEST(Player player) {
		
	}
	

	private static void invalidCommand(Player player) {
		MessageUtil.sendMessage(player, "Not a valid command", MessageType.SINGLE);
	}

	public void newSpawnNode(Player player, String name, String fileName) {
		if (name.equals(""))
			if (!SpawnNodeProcessor.newSpawnNode(player.getLocation(), null, fileName))
				MessageUtil.sendMessage(player, "failed creating node", MessageType.SINGLE);
			else {
				MessageUtil.sendMessage(player, "created new Lootcrate node", MessageType.SINGLE);
			}
		else {
			if (!SpawnNodeProcessor.newSpawnNode(player.getLocation(), new String[]{name}, fileName))
				MessageUtil.sendMessage(player, "failed creating node", MessageType.SINGLE);
			else {
				MessageUtil.sendMessage(player, "created new Beacon node", MessageType.SINGLE);
			}
		}
	}
	// 0      1      2       3 4 5  6      7       8
	//create testMob testMob 1 1 1 zombie minion hostile
	public void newSpawnNode(Player player, String[] args) {
		String[] newArgs = new String[7];
		int j = 0;
		for (int i = 2; i < 9; i++) {
			newArgs[j] = args[i];
			j++;
		}
		String fileName = args[1];
		if (!SpawnNodeProcessor.newSpawnNode(player.getLocation(), newArgs, fileName))
			MessageUtil.sendMessage(player, "failed creating node", MessageType.SINGLE);
		else {
			MessageUtil.sendMessage(player, "created new Mob node", MessageType.SINGLE);
		}
	}

}
