package com.pkadev.pkaadventure.processors;

import java.util.List;
import java.util.Set;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MiscDisguise;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.FallingBlockWatcher;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.pkadev.pkaadventure.inventories.InventoryMain;
import com.pkadev.pkaadventure.objects.PKAPlayer;
import com.pkadev.pkaadventure.types.InventoryType;
import com.pkadev.pkaadventure.types.MessageType;
import com.pkadev.pkaadventure.utils.FileUtil;
import com.pkadev.pkaadventure.utils.InventoryUtil;
import com.pkadev.pkaadventure.utils.MessageUtil;
import com.pkadev.pkaadventure.utils.ShopUtil;

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
			InventoryUtil.openShopInventory(player, InventoryType.FOOD_STORE_BUYING);
			PKAPlayer pkaPlayer = PlayerProcessor.getPKAPlayer(player.getName());
			player.sendMessage("" + pkaPlayer.getClassType().toString());
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
