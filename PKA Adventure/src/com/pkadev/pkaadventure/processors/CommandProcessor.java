package com.pkadev.pkaadventure.processors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pkadev.pkaadventure.inventories.InventoryMain;
import com.pkadev.pkaadventure.types.InventoryType;
import com.pkadev.pkaadventure.types.MessageType;
import com.pkadev.pkaadventure.utils.InventoryUtil;
import com.pkadev.pkaadventure.utils.MessageUtil;

public class CommandProcessor implements CommandExecutor {

	private static CommandProcessor i; private CommandProcessor(){} public static CommandProcessor i() {if (i == null)i = new CommandProcessor();return i;}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLbl,
			String[] args) {
		int argsLength = args.length;
		
		if (!(sender instanceof Player))
			return false;
		Player player = (Player) sender;
		
		if (argsLength == 0) {
			// InventoryMain.loadShops();
			InventoryUtil.openShopInventory(player, InventoryType.FOOD_STORE_BUYING);
			player.sendMessage("Opening");
		} else if (argsLength == 1) {
			if (args[0].equalsIgnoreCase("create")) {
				MessageUtil.sendMessage(player, "/create fileName mobName radius level amount mob strength stance type", MessageType.SINGLE);
			} else {
				invalidCommand(player);
			}
		} else if (argsLength == 3) {
			if (args[0].equalsIgnoreCase("create")) {
				//create lootcrate/beacon
				if (args[1].equalsIgnoreCase("lootcrate")) {
					newSpawnNode(player, true, args[2]);
				} else if (args[1].equalsIgnoreCase("beacon")) {
					newSpawnNode(player, false, args[2]);
				} else {
					invalidCommand(player);
				}
			} else {
				invalidCommand(player);
			}
		} else if (argsLength == 10) {
			newSpawnNode(player, args);
		} else {
			invalidCommand(player);
		}
		
		return true;
	}
	
	private static void invalidCommand(Player player) {
		MessageUtil.sendMessage(player, "Not a valid command", MessageType.SINGLE);
	}
	
	public void newSpawnNode(Player player, boolean isLootCrate, String fileName) {
		if (isLootCrate)
			if (!SpawnNodeProcessor.newSpawnNode(player.getLocation(), new String[]{""}, fileName))
				MessageUtil.sendMessage(player, "failed creating node", MessageType.SINGLE);
			else {
				MessageUtil.sendMessage(player, "created new Lootcrate node", MessageType.SINGLE);
			}
		else {
			if (!SpawnNodeProcessor.newSpawnNode(player.getLocation(), null, fileName))
				MessageUtil.sendMessage(player, "failed creating node", MessageType.SINGLE);
			else {
				MessageUtil.sendMessage(player, "created new Beacon node", MessageType.SINGLE);
			}
		}
	}
	
	public void newSpawnNode(Player player, String[] args) {
		String[] newArgs = new String[7];
		for (int i = 1; i < 7; i++) {
			newArgs[i] = args[i];
		}
		String fileName = args[1];
		if (!SpawnNodeProcessor.newSpawnNode(player.getLocation(), newArgs, fileName))
			MessageUtil.sendMessage(player, "failed creating node", MessageType.SINGLE);
		else {
			MessageUtil.sendMessage(player, "created new Mob node", MessageType.SINGLE);
		}
	}
	
}
